/*
 * FunCode.java
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

package funbase;

import java.io.PrintWriter;
import java.util.*;

/** Code for a function body. */
public class FunCode extends Value {
    private static final long serialVersionUID = 1L;

    /** Enumerated type of opcodes for the Fun machine */
    public enum Opcode {
	GLOBAL(1),   /* [#global, x] becomes GLOBAL i where consts[i] = x:
			push value of global name x */
	LOCAL(1),    /* [#local, n]: push value of local variable n */
	ARG(1),	     /* [#arg, n]: push value of argument n */
	FVAR(1),     /* [#fvar, n]: push value of free variable n */
	BIND(-1),    /* [#bind, n]: pop value and store as local n */
	POP(-1),     /* [#pop]: pop and discard a value */
	QUOTE(1),    /* [#quote, x] becomes QUOTE i where consts[i] = x:
		        push the constant x */
	NIL(1),	     /* [#nil]: push the empty list */
	CONS(-1),    /* [#cons]: pop a tail then a head, push a cons */
	TRAP(0),     /* [#trap, lab] becomes TRAP i: set trap register */
	FAIL(0),     /* [#fail]: die with "no clause matched" */
	JFALSE(-1),  /* [#jfalse, lab] becomes JFALSE n:
		     	pop a boolean and jump if false */
	JUMP(0),     /* [#jump, lab] becomes JUMP n:
		     	jump to instruction at offset n */
	PREP(0),     /* [#prep, n]: prepare for a call with n arguments */
	RETURN(-1),  /* [#return]: return from function */
	MPLUS(0),    /* [#mplus, k]: match an n+k pattern by popping integer
		     	x with x >= k and pushing x-k; otherwise trap */
	MEQ(-2),     /* [#meq]: pop two values and trap if not equal */
	MNIL(-1),    /* [#mnil]: pop the empty list; otherwise trap */
	MCONS(1),    /* [#mcons]: pop a cons cell and push its tail and head */
	TCALL(0),    /* [#tcall, n]: tail recursive call */
	PUTARG(0),   /* [#putarg, i]: mark i'th argument of a call */
	CALL { @Override public int delta(int arg) { return -arg; } }, 
	             /* [#call, n]: call a function with n arguments */
	CLOSURE { @Override public int delta(int arg) { return -arg; } }, 
		     /* [#closure, n]: form a closure with n free variables */
	MPRIM { @Override public int delta(int arg) { return arg-2; } };
	      	     /* [#mprim, n]: pattern match a constructor with n args */

	private final int delta;
	private Opcode() { this(0); }
	private Opcode(int delta) { this.delta = delta; }
	public int delta(int arg) { return delta; }
    }
    
    /** Name of the function (used for error messages) */
    public final String name;

    /** Number of arguments */
    public final int arity;
    
    /** Size of local variable frame */
    protected final int fsize;
    
    /** Max size of evaluation stack */
    protected final int ssize;

    /** Whether to freeze the error context on entry */
    public final boolean frozen = Name.freezer;

    /** Opcodes for the instructions */
    public final Opcode instrs[];

    /** Operands for the instructions */
    public final int rands[];

    /** Constant pool */
    public final Value consts[];

    public transient Function.Factory jitcode;
    
    private static Jit translator;
    
    public FunCode(String name, int arity, int fsize, int ssize,
		   Opcode instrs[], int rands[], Value consts[]) {
	this.name = name; this.arity = arity;
	this.fsize = fsize; this.ssize = ssize;
	this.instrs = instrs; this.rands = rands; 
	this.consts = consts;
    }

    /** Install a translator to call before building a closure */
    public static void install(Jit translator) {
	FunCode.translator = translator;
    }
    
    public static String[] getContext(String me) {
	return translator.getContext(me);
    }

    public static void initStack() {
	translator.initStack();
    }

    public static void setRoot(Value root) {
	translator.setRoot(root);
    }

    @Override
    public void printOn(PrintWriter out) {
	out.printf("<funcode>");
    }
    
    @Override
    public void dump(PrintWriter out) {
	out.printf("funcode \"%s\" %d %d %d %d %d\n", 
		   name, arity, instrs.length, consts.length, fsize, ssize);
	for (int i = 0; i < instrs.length; i++)
	    out.printf("  %s %d\n", instrs[i].name(), rands[i]);
	for (int j = 0; j < consts.length; j++)
	    consts[j].dump(out);
	out.printf("{ end of %s }\n", name);
    }

    /** Construct a wrapped closure and tie the knot for local recursion */
    public Value makeClosure(Value fvars[]) {
	Value.FunValue result = makeFunValue(null);
	result.subr = buildClosure(result, fvars);
	fvars[0] = result;
	return result;
    }

    /** Build a closure */
    public Function buildClosure(Value.FunValue func, Value fvars[]) {
	if (jitcode == null)
	    jitcode = translator.translate(this);

	return jitcode.newClosure(func, fvars);
    }

    /** Find an opcode from its name */
    public static Opcode getOpcode(String x) { 
	return Enum.valueOf(Opcode.class, x); 
    }
    
    /** A label in the code for a function.
     * 
     *  Each label may be used in only one jump instruction, and all
     *  jumps are forward jumps. */
    private static class Label {
	/* Offset where the label is used. */
	public final int use;
	
	/** Stack depth at jump target. */
	public final int depth;

	public Label(int use, int depth) {
	    this.use = use; this.depth = depth;
	}
    }
    
    /** Assemble a list of instructions into a function body */
    public static final Primitive assemble = 
	new Primitive.Prim3("assemble") {
	    @Override
	    public Value apply3(Value name0, Value arity0, Value code) {
		String name = name0.toString(); // Could be name or string
		int arity = (int) number(arity0);
		int size = 0;

		for (Value xs = code; isCons(xs); xs = tail(xs))
		    if (isCons(head(xs))) size++;
	
		Opcode instrs[] = new Opcode[size];
		int rands[] = new int[size];
		int ip = 0, sp = 0, fsize = 0, ssize = 0;
		List<Value> consts = new ArrayList<Value>();
	
		/** Mapping from integer labels to info about each label */
		Map<Integer, Label> labels = new HashMap<Integer, Label>();
	
		for (Value xs = code; isCons(xs); xs = tail(xs)) {
		    Value inst = head(xs);
		    if (inst instanceof Value.NumValue) {
			/* A label */
			Label lab = labels.get((int) number(inst));
			if (lab != null) {
			    rands[lab.use] = ip;
			    sp = lab.depth;
			}
		    } else if (isCons(inst)) {
			/* An instruction [#op, arg] with optional arg */
			Name x = cast(Name.class, head(inst), "opcode");
			Opcode op = getOpcode(x.tag);
			Value args = tail(inst);
			int rand;

			if (! isCons(args))
			    /* No argument */
			    rand = 0;
			else {
			    Value v = head(args);
			    if (op == Opcode.GLOBAL || op == Opcode.QUOTE
				|| op == Opcode.MPLUS) {
				/* An argument that goes in the constant pool */
				rand = consts.indexOf(v);
				if (rand < 0) {
				    rand = consts.size();
				    consts.add(v);
				}
			    } else {
				/* An integer argument */
				rand = (int) number(v);
			    }
			}

			instrs[ip] = op; rands[ip] = rand;
			sp += instrs[ip].delta(rand);
			if (sp > ssize) ssize = sp;

			switch (op) {
			    case BIND:
				/* Update the frame size */
				if (rand >= fsize) fsize = rand+1;
				break;

			    case JUMP:
			    case JFALSE:
				/* Create a label */
				labels.put(rand, new Label(ip, sp));
				break;
				
			    case TRAP:
				/* Create a label, noting one value will 
				   be popped */
				labels.put(rand, new Label(ip, sp-1));
				break;

			    default:
				break;
			}

			ip++;
		    } else {
			Evaluator.error("Bad instruction " + inst);
		    }
		}

		return new FunCode(name, arity, fsize, ssize, instrs, rands,
				   consts.toArray(new Value[consts.size()]));
	    }
	};

    /** Interface for JIT translators */
    public interface Jit {
	/** Translate funcode and create a factory for closures */
	public Function.Factory translate(FunCode funcode);

	/** Get execution context */
	public String[] getContext(String me);

	/** Initialise stack */
	public void initStack();

	/** Set stack root */
	public void setRoot(Value root);
    }
}
