/*
 * InlineTranslator.java
 * 
 * This file is part of GeomLab
 * Copyright (c) 2005 J. M. Spivey
 * All rights reserved
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.      
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 *    
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package funjit;

import funbase.FunCode.Opcode;
import funbase.Name;
import funbase.Value;
import funbase.Function;
import funbase.Primitive;
import funbase.Value.WrongKindException;

import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

import java.util.*;

/** A JIT translator with inlining of some primitives, allowing unboxed
    intermediate values in expressions.  This class contains just the
    framework, and rules for specific primitives are added elsewhere. */
public class InlineTranslator extends RuleTranslator {
    /** Stack to track inlinable primitives in a nest of calls */
    private Stack<Inliner> funstack = new Stack<Inliner>();
    
    /** Dictionary mapping primitive names to their inline
        code generators */
    private Map<String, Inliner> primdict = new HashMap<>();

    /** Register an inline code generator */
    protected void register(AbstractInliner c) {
	primdict.put(c.name, c);
    }

    public InlineTranslator() {
	// Add hooks to the parent to spot specific code sequences,
	// general ones before specific ones

	/* Hook to watch for PUTARG instructions not matched below */
	addHook(new CodeHook(Opcode.PUTARG) {
	    @Override
	    public boolean compile(int rands[], int ip) {
		return convertArg(rands[ip], Species.VALUE);
	    }
	});

	/* Hook for other CALL instructions */
	addHook(new CodeHook(Opcode.CALL) {
	    @Override
            public boolean compile(int rands[], int ip) {
		Inliner gen = funstack.pop(); // Pop anyway
		Species k = gen.call();
		if (k == null) 
                    return false;
                code.widen(k);
                nstack.pop();
                return true;
	    }
	});

	/** Hook to watch for GLOBAL / PREP pairs */
	addHook(new CodeHook(Opcode.GLOBAL, Opcode.PREP) {
	    @Override
            public boolean compile(int rands[], int ip) {
		Name f = (Name) funcode.consts[rands[ip]];
                Value v = f.getGlodef();

		if (f.isFrozen() && v != null && v instanceof Value.FunValue) {
		    Function fun = ((Value.FunValue) v).subr;
                    // Calling a known function
		    if ((fun instanceof Primitive) 
                        && fun.arity == rands[ip+1]) {
                        // Calling a primitive with the correct arity
			Primitive p = (Primitive) fun;
			Inliner z = primdict.get(p.name);
			if (z != null) {
                            // Calling a primitive we know how to inline
			    funstack.push(z);
                            // Push a dummy argcollector
                            ArgCollector c = new ArgCollector(0);
                            nstack.push(c);
			    return true;
			}
		    }
		}

		return false;
	    }
	});

	addHook(new CodeHook(Opcode.QUOTE, Opcode.PUTARG) {
	    @Override
            public boolean compile(int rands[], int ip) {
		Inliner gen = funstack.peek();
		Value v = funcode.consts[rands[ip]];
		Species k = gen.argkind(rands[ip+1]);

		// Put numeric constants in the JVM constant pool
		if (k == Species.NUMBER) {
		    try {
			code.gen(CONST, v.asNumber());
			return true;
		    }
		    catch (WrongKindException ex) { /* PUNT */ }
		}
		
		return false;
	    }
	});

	/* Hook to watch for CALL / PUTARG */
	addHook(new CodeHook(Opcode.CALL, Opcode.PUTARG) {
	    @Override
            public boolean compile(int rands[], int ip) {
		Inliner gen = funstack.peek();
                int i = rands[ip+1];
		Species k = gen.call();
		if (k == null) 
                    return false;
                funstack.pop();
                nstack.pop();
                if (!convertArg(i, k)) 
                    translate(Opcode.PUTARG, i);
                return true;
	    }
	});

	addHook(new CodeHook(Opcode.CALL, Opcode.JFALSE) {
	    @Override
            public boolean compile(int rands[], int ip) {
		Inliner gen = funstack.peek();
		if (gen.jcall(rands[ip+1])) {
		    funstack.pop();
                    nstack.pop();
		    return true;
		}
		return false;
	    }
	});
	
	/* Notice FVAR 0 / PREP, but leave it to the existing rule to
	   translate it */
	addHook(new CodeHook(Opcode.FVAR, Opcode.PREP) {
	    @Override
            public boolean compile(int rands[], int ip) {
		if (rands[ip] == 0) funstack.push(nullInliner);
		return false;
	    }
	});

	/* Also notice other PREP instructions */
	addHook(new CodeHook(Opcode.PREP) {
	    @Override
	    public boolean compile(int rands[], int nargs) {
		funstack.push(nullInliner);
		return false;
	    }
	});

        /* And also FRAME instructions */
	addHook(new CodeHook(Opcode.FRAME) {
	    @Override
	    public boolean compile(int rands[], int nargs) {
		funstack.push(nullInliner);
		return false;
	    }
	});

        /* And then again CLOSURE instructions */
	addHook(new CodeHook(Opcode.CLOSURE) {
	    @Override
	    public boolean compile(int rands[], int nargs) {
		funstack.pop();
		return false;
	    }
	});
    }

    /** Convert a value on the stack from species k1 to species k2.  
        May fail, naming primitive |name| in the message. */
    protected void convert(String name, Species k1, Species k2) {
	if (k1 == k2) return;

        if (k1 == Species.INT && k2 == Species.NUMBER) {
            code.gen(I2D);
            return;
        }

        if (k1 == Species.NUMBER && k2 == Species.INT) {
            code.gen(INVOKESTATIC, math_cl, "round", fun_D_L_t);
            code.gen(L2I);
            return;
        }

	// All other useful conversions can go via Value.  Conversions 
        // between different kinds like NUMBER and BOOL will always fail, 
        // but we generate the failing code without complaint.

        code.widen(k1);
        code.narrow(k2, name);
    }

    /** Convert i'th argument to suit function being called. */
    protected boolean convertArg(int i, Species k) {
	Inliner gen = funstack.peek();
	return gen.putArg(i, k);
    }

    /** Prepare for new function */
    @Override 
    protected void init() {
	super.init();
	funstack.clear();
    }

    /** A code generator for a specific primitive */
    public interface Inliner {
	/** Return the preferred kind for an argument */
	public Species argkind(int i);

	/** Compile code for an argument, return true if done */
	public boolean putArg(int i, Species k);

	/** Compile code for a call, or return null to punt */
	public Species call();

	/** Compile code for a call followed by JFALSE, return true if done */
	public boolean jcall(int addr);
    }

    public abstract class AbstractInliner implements Inliner {
	public final String name;

	public AbstractInliner(String name) {
	    this.name = name;
	}

	/** Compile code for an argument, return true if done */
	public boolean putArg(int i, Species k) {
	    convert(name, k, argkind(i));
	    return true;
	}

	/** Compile code for a call followed by JFALSE */
	public boolean jcall(int addr) {
	    Species k = call();
            if (k == null)
                return false;

	    if (k == Species.BOOL) 
                code.gen(IFEQ, makeLabel(addr));
            else {
                code.widen(k);
                translate(Opcode.JFALSE, addr);
	    }

            return true;
	}
    }

    /** An inliner that represents an ordinary, non-inlinable function */
    private Inliner nullInliner = new Inliner() {
        public Species argkind(int i) {
            return Species.VALUE;
        }

	public boolean putArg(int i, Species k) {
	    // Convert argument to Value and punt
	    code.widen(k);
	    return false;
	}

	public Species call() { 
	    // Refuse to handle the call
	    return null;
	}

        public boolean jcall(int addr) {
            return false;
        }
    };
}
