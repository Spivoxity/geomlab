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

import funbase.Primitive.PRIMITIVE;

import java.io.PrintWriter;
import java.util.*;
import java.lang.reflect.Method;

/** Code for a function body. */
public class FunCode extends Value {
    private static final long serialVersionUID = 1L;

    /** Enumerated type of opcodes for the Fun machine */
    public enum Opcode {
	GLOBAL,      /* [#global, x] becomes GLOBAL i where consts[i] = x:
			push value of global name x */
	LOCAL,       /* [#local, n]: push value of local variable n */
	ARG,	     /* [#arg, n]: push value of argument n */
	FVAR,        /* [#fvar, n]: push value of free variable n */
	BIND,        /* [#bind, n]: pop value and store as local n */
	POP,         /* [#pop]: pop and discard a value */
	QUOTE,       /* [#quote, x] becomes QUOTE i where consts[i] = x:
		        push the constant x */
	NIL,	     /* [#nil]: push the empty list */
	PRECONS,     /* [#precons]: prepare for CONS */
	CONS,        /* [#cons]: pop a tail then a head, push a cons */
	TRAP,        /* [#trap, lab] becomes TRAP i: set trap register */
	FAIL,        /* [#fail]: die with "no clause matched" */
	JFALSE,      /* [#jfalse, lab] becomes JFALSE n:
		     	pop a boolean and jump if false */
	JUMP,        /* [#jump, lab] becomes JUMP n:
		     	jump to instruction at offset n */
	PREP,        /* [#prep, n]: prepare for a call with n arguments */
	CLOPREP,     /* [#cloprep, n]: prepare a closure with n fvars */
	RETURN,      /* [#return]: return from function */
	MPLUS,       /* [#mplus, k]: match an n+k pattern by popping integer
		     	x with x >= k and pushing x-k; otherwise trap */
	MEQ,         /* [#meq]: pop two values and trap if not equal */
	MNIL,        /* [#mnil]: pop the empty list; otherwise trap */
	MCONS,       /* [#mcons]: pop a cons cell and push its tail and head */
	GETTAIL,     /* [#gettail]: fetch tail following MCONS */
	TCALL,       /* [#tcall, n]: tail recursive call */
	PUTARG,      /* [#putarg, i]: mark i'th argument of a call */
	PUTFVAR,     /* [#putfvar, i]: mark i'th free var of a closure */
        CALL,        /* [#call, n]: call a function with n arguments */
        CLOSURE,     /* [#closure, n]: form a closure with n free variables */
        MPRIM;       /* [#mprim, n]: pattern match a constructor with n args */
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
    
    public static Primitive primitive(String name, int arity, Method meth) {
	return translator.primitive(name, arity, meth);
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
    
    /** Assemble a list of instructions into a function body */
    @PRIMITIVE
    public static Value _assemble(Primitive prim, Value name, 
				  Value arity, Value fsize,
                                  Value ssize, Value code) {
	int size = 0;
	for (Value xs = code; prim.isCons(xs); xs = prim.tail(xs))
	    if (prim.isCons(prim.head(xs))) size++;
	
	Opcode instrs[] = new Opcode[size];
	int rands[] = new int[size];
	int ip = 0;
	List<Value> consts = new ArrayList<Value>();
	
	/** Mapping from integer labels to use point of each label */
	Map<Integer, Integer> labels = new HashMap<Integer, Integer>();
	
	for (Value xs = code; prim.isCons(xs); xs = prim.tail(xs)) {
	    Value inst = prim.head(xs);
	    if (inst instanceof Value.NumValue) {
		/* A label */
		Integer use = labels.get((int) prim.number(inst));
		if (use != null) rands[use] = ip;
	    } else if (prim.isCons(inst)) {
		/* An instruction [#op, arg] with optional arg */
		Name x = prim.cast(Name.class, prim.head(inst), "an opcode");
		Opcode op = getOpcode(x.tag);
		Value args = prim.tail(inst);
		int rand;

		if (! prim.isCons(args))
		    /* No argument */
		    rand = 0;
		else {
		    Value v = prim.head(args);
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
			rand = (int) prim.number(v);
		    }
		}

		instrs[ip] = op; rands[ip] = rand;

		switch (op) {
		    case JUMP:
		    case JFALSE:
		    case TRAP:
			labels.put(rand, ip);
			break;

		    default:
			break;
		}

		ip++;
	    } else {
		throw new Error("Bad instruction " + inst);
	    }
	}
	
	return new FunCode(name.toString(), // Could be name or string
                           (int) prim.number(arity), 
                           (int) prim.number(fsize), 
                           (int) prim.number(ssize), 
                           instrs, rands,
			   consts.toArray(new Value[consts.size()]));
    }

    /** Interface for JIT translators */
    public interface Jit {
	/** Translate funcode and create a factory for closures */
	public Function.Factory translate(FunCode funcode);

	/** Make a primitive by reflecting a static method */
	public Primitive primitive(String name, int arity, Method meth);

	/** Get execution context */
	public String[] getContext(String me);

	/** Initialise stack */
	public void initStack();

	/** Set stack root */
	public void setRoot(Value root);
    }
}
