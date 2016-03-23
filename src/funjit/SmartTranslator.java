/*
 * SmartTranslator.java
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

import funbase.Primitive;
import funbase.Value;
import funbase.Name;
import funbase.Function;
import funbase.FunCode.Opcode;
import funbase.Value.WrongKindException;

import static funjit.Opcodes.*;
import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

import java.util.*;

/** An subclass of InlineTranslator that contains rules for specific 
    primitives, and can add more as they are registered. */
public class SmartTranslator extends InlineTranslator {
    public SmartTranslator() {
	// Inliners for various common primitives
	register(new Equality("=", true));
	register(new Equality("<>", false));
	register(new Operator("+", DADD));
	register(new Operator("-", DSUB));
	register(new Operator("*", DMUL));
	register(new Operator("_uminus", DNEG));
	register(new Comparison("<", DCMPG, IFGE));
	register(new Comparison("<=", DCMPG, IFGT));
	register(new Comparison(">", DCMPL, IFLE));
	register(new Comparison(">=", DCMPG, IFLT));
	register(new ListSelect("head", "#head"));
	register(new ListSelect("tail", "#tail"));
    }

    /** An inline code generator with all args of the same kind */
    private abstract class SimpleInliner extends AbstractInliner {
	private final Species argkind;

        public SimpleInliner(String name) {
            this(name, Species.VALUE);
        }

	public SimpleInliner(String name, Species argkind) {
	    super(name);
	    this.argkind = argkind;
	}

	@Override
	public Species argkind(int i) { return argkind; }
    }

    /** Inliner for = and <> */
    public class Equality extends SimpleInliner {
	private boolean sense;

	public Equality(String name, boolean sense) {
	    super(name, Species.VALUE);
	    this.sense = sense;
	}

	@Override 
	public Species call() {
	    code.gen(INVOKEVIRTUAL, object_cl, "equals", fun_O_B_t);
	    if (! sense) {
		code.gen(CONST, 1); code.gen(IXOR);
	    }
	    return Species.BOOL;
	}

	@Override 
	public boolean jcall(int addr) {
	    code.gen(INVOKEVIRTUAL, object_cl, "equals", fun_O_B_t);
	    code.gen((sense ? IFEQ : IFNE), makeLabel(addr));
	    return true;
	}
    }

    /** Inliner for numeric operations */
    public class Operator extends SimpleInliner {
	private Op op;

	public Operator(String name, Op op) {
	    super(name, Species.NUMBER);
	    this.op = op;
	}

	@Override
	public Species call() {
	    code.gen(op);
	    return Species.NUMBER;
	}
    }

    /** Inliner for numeric comparisons */
    public class Comparison extends SimpleInliner {
	private Op cmp_op;	// Double comparison op DCMPL or DCMPG
	private Op jump_op;	// Conditional branch if condition false

	public Comparison(String name, Op cmp_op, Op jump_op) {
	    super(name, Species.NUMBER);
	    this.cmp_op = cmp_op; this.jump_op = jump_op;
	}

	@Override 
	public Species call() {
	    Label lab = new Label(), lab2 = new Label();    
	    code.gen(cmp_op);
	    code.gen(jump_op, lab);
	    code.gen(CONST, 1);
	    code.gen(GOTO, lab2);
	    code.label(lab);
	    code.gen(CONST, 0);
	    code.label(lab2);
	    return Species.BOOL;
	}

	@Override 
	public boolean jcall(int addr) {
	    code.gen(cmp_op);
	    code.gen(jump_op, makeLabel(addr));
	    return true;
	}
    }

    /** Inliner for head and tail */
    public class ListSelect extends SimpleInliner {
	String errtag;

	public ListSelect(String name, String errtag) {
	    super(name, Species.VALUE);
	    this.errtag = errtag;
	}

	@Override 
	public Species call() {
	    code.gen(DUP);
	    code.gen(ASTORE, _temp);
	    code.cast(consval_cl, code.new Handler(name, "list") {
                @Override 
                public void compile() {
                    // Evaluator.list_fail(<msg>);
                    code.gen(ALOAD, _temp);
                    code.gen(CONST, errtag);
                    code.gen(INVOKESTATIC, evaluator_cl, 
                             "list_fail", fun_VS_t);
                }
            });
	    code.gen(GETFIELD, consval_cl, name, value_t);
	    return Species.VALUE;
	}
    }	

    /** An inliner that calls a specific method */
    private class Invoker extends AbstractInliner {
        String cl, mname;
        boolean isStatic;
        Species argkinds[];
        Species rkind;
        Type mtype;

        public Invoker(String name, String cl, String mname, 
                       boolean isStatic, Species argkinds[], 
                       Species rkind, Type mtype) {
            super(name);
            this.cl = cl; this.mname = mname; this.isStatic = isStatic;
            this.argkinds = argkinds; this.rkind = rkind; 
            this.mtype = mtype;
        }

        @Override
        public Species argkind(int i) {
            return argkinds[i];
        }

        @Override
        public boolean putArg(int i, Species k) {
            convert(name, k, argkinds[i]);
            return true;
        }

        public Species call() {
            // We don't inline the method call, but we do inline
            // the boilerplate code that prepares for it.
            if (isStatic)
                code.gen(INVOKESTATIC, cl, mname, mtype);
            else
                code.gen(INVOKEVIRTUAL, cl, mname, mtype);

            return rkind;
        }
    }

    /** Inliner for generic field selection function */
    public class Selector extends SimpleInliner {
	private final String fname;
	private final Species cl, kind;

	public Selector(String name, Species cl, String fname, Species kind) {
	    super(name, Species.VALUE);
	    this.cl = cl; this.fname = fname; this.kind = kind;
	}

	@Override 
	public Species call() {
	    code.cast(cl.clname, name);
	    code.gen(GETFIELD, cl.clname, fname, kind.type);
	    return kind;
	}
    }

    /** A primitive factory that catches primitives that are implemented
        by methods or field access and creates inliners for them. */
    class InliningPrimFactory extends JitPrimFactory {
        @Override
        protected void noteMethod(String name, Class<?> cl, String mname,
                                  boolean isStatic, Species pkinds[], 
                                  Species rkind) { 
            int rcvr = (isStatic ? 0 : 1);
            int nargs = pkinds.length+rcvr;
            Species argkinds[] = new Species[nargs];
            if (! isStatic) argkinds[0] = Species.find(cl);
            System.arraycopy(pkinds, 0, argkinds, rcvr, pkinds.length);
            register(new Invoker(name, Type.className(cl), mname, 
                                 isStatic, argkinds, rkind, 
                                 Species.methType(pkinds, rkind)));
        }

        @Override
        protected void noteSelector(String name, Species cl, String fname, 
                                    Species rkind) {
            register(new Selector(name, cl, fname, rkind));
        }
    }

    @Override
    public Primitive.Factory makePrimitiveFactory() {
        return new InliningPrimFactory();
    }
}
