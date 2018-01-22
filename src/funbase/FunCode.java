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
import funbase.Primitive.DESCRIPTION;

import java.io.PrintWriter;
import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/** Code for a function body. */
@DESCRIPTION("a function body")
public class FunCode extends Value {
    private static final long serialVersionUID = 1L;

    /** Enumerated type of opcodes for the Fun machine */
    public enum Opcode {
	GLOBAL,      // [#global, x] becomes GLOBAL i where consts[i] = x:
		     //   push value of global name x
	LOCAL,       // [#local, n]: push value of local variable n
	ARG,	     // [#arg, n]: push value of argument n
	FVAR,        // [#fvar, n]: push value of free variable n
	BIND,        // [#bind, n]: pop value and store as local n
	POP,         // [#pop]: pop and discard a value
	QUOTE,       // [#quote, x] becomes QUOTE i where consts[i] = x:
		     //   push the constant x
        PUSH,        // #push(n): push the integer constant n
	NIL,	     // [#nil]: push the empty list
	CONS,        // [#cons]: pop a tail then a head, push a cons
	TRAP,        // [#trap, lab] becomes TRAP i: set trap register
	FAIL,        // [#fail]: die with "no clause matched"
	JFALSE,      // [#jfalse, lab] becomes JFALSE n:
		     //   pop a boolean and jump if false
	JUMP,        // [#jump, lab] becomes JUMP n:
		     //   jump to instruction at offset n
	RETURN,      // [#return]: return from function
	MPLUS,       // [#mplus, k]: match an n+k pattern by popping integer
		     //   x with x >= k and pushing x-k; otherwise trap
	MEQ,         // [#meq]: pop two values and trap if not equal
	MNIL,        // [#mnil]: pop the empty list; otherwise trap
	MCONS,       // [#mcons]: find a cons cell and push its head
	GETTAIL,     // [#gettail]: fetch tail following MCONS
        MPAIR,       // [#mpair]: find a pair and push its fst
        GETSND,      // [#getsnd]: fetch snd following MPAIR
	TCALL,       // [#tcall, n]: tail recursive call
	PREP,        // [#prep, n]: prepare for a call with n arguments
        FRAME,       // [#frame, n]: create a free var frame with n slots
	PUTARG,      // [#putarg, i]: mark i'th argument of a call
        CALL,        // [#call, n]: call a function with n arguments
        CLOSURE,     // [#closure, n]: form a closure with n free variables
        MPRIM;       // [#mprim, n]: pattern match a constructor with n args
    }
    
    /** Name of the function (used for error messages) */
    public final String name;

    /** Number of arguments */
    public final int arity;
    
    /** Whether to freeze the error context on entry */
    public final boolean frozen = Name.getFreezer();

    /** Opcodes for the instructions */
    public final Opcode instrs[];

    /** Operands for the instructions */
    public final int rands[];

    /** Constant pool */
    public final Value consts[];

    /** Value for rand if no operand */
    public static final int NO_RAND = 0x8000000;

    public transient Function.Factory jitcode;
    
    private static Jit translator;
    
    public FunCode(String name, int arity, 
                   Opcode instrs[], int rands[], Value consts[]) {
	this.name = name; this.arity = arity;
	this.instrs = instrs; this.rands = rands; 
	this.consts = consts;
    }

    /** Install a translator to call before building a closure */
    public static void install(Jit translator) {
	FunCode.translator = translator;
    }
    
    public static Primitive.Factory primitiveFactory() {
        return translator.primitiveFactory();
    }

    public static Evaluator.Backtrace backtrace() {
        return translator.backtrace();
    }

    @Override
    public void printOn(PrintWriter out) {
	out.printf("<funcode>");
    }
    
    @Override
    public void dump(PrintWriter out) {
	out.printf("bytecode ");
        dumpCode(out);
    }

    public void dumpCode(PrintWriter out) {
        out.printf("\"%s\" %d\n", name, arity);

        for (int i = 0; i < instrs.length; i++) {
            if (rands[i] == NO_RAND)
                out.printf("%s\n", instrs[i].name());
            else
                out.printf("%s %d\n", instrs[i].name(), rands[i]);
        }
        out.printf("end\n");
        
        for (int i = 0; i < consts.length; i++)
            consts[i].dump(out);
        out.printf("end\n");
    }

    /** Construct a wrapped closure and tie the knot for local recursion */
    public Value makeClosure(Value fvars[]) {
	Value result = FunValue.instance(null);
	result.subr = buildClosure(result, fvars);
	fvars[0] = result;
	return result;
    }

    public Value makeClosure1() {
        return makeClosure(new Value[1]);
    }

    public Value makeClosure2(Value fv1) {
        return makeClosure(new Value[] { null, fv1 });
    }

    public Value makeClosure3(Value fv1, Value fv2) {
        return makeClosure(new Value[] { null, fv1, fv2 });
    }

    public Value makeClosure4(Value fv1, Value fv2, Value fv3) {
        return makeClosure(new Value[] { null, fv1, fv2, fv3 });
    }

    public Value makeClosure5(Value fv1, Value fv2, Value fv3, Value fv4) {
        return makeClosure(new Value[] { null, fv1, fv2, fv3, fv4 });
    }

    public Value makeClosure6(Value fv1, Value fv2, Value fv3, 
                              Value fv4, Value fv5) {
        return makeClosure(new Value[] { null, fv1, fv2, fv3, fv4, fv5 });
    }

    /** Build a closure */
    public Function buildClosure(Value func, Value fvars[]) {
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
                                  int arity, Value code) {
	int size = prim.listLength(code);
	Opcode instrs[] = new Opcode[size];
	int rands[] = new int[size];
	int ip = 0;
	List<Value> consts = new ArrayList<Value>();
	
	for (Value xs = code; prim.isCons(xs); xs = prim.tail(xs)) {
	    Value inst = prim.head(xs);
            Value opcode;
            Value arg = null;

            if (inst instanceof Value.BlobValue) {
                Value.BlobValue blob = (Value.BlobValue) inst;
                opcode = blob.functor;
                if (blob.args.length > 0) arg = blob.args[0];
            } else {
                opcode = inst;
            }

            Name x = prim.cast(Name.class, opcode);
            Opcode op = getOpcode(x.tag);
            int rand = NO_RAND;

            if (arg != null) {
                switch (op) {
                    case GLOBAL:
                    case QUOTE:
                    case MPLUS:
                        /* An argument that goes in the constant pool */
                        rand = consts.indexOf(arg);
                        if (rand < 0) {
                            rand = consts.size();
                            consts.add(arg);
                        }
                        break;
                    default:
                        /* An integer argument */
                        rand = (int) prim.number(arg);
                        break;
                }
            }

            instrs[ip] = op; rands[ip] = rand;
            ip++;
	}
	
	return new FunCode(name.toString(), // Could be name or string
                           arity, instrs, rands,
			   consts.toArray(new Value[consts.size()]));
    }

    /** Interface for JIT translators */
    public interface Jit {
	/** Translate funcode and create a factory for closures */
	public Function.Factory translate(FunCode funcode);

	/** Return a primitive factory */
        public Primitive.Factory primitiveFactory();

        /** Return a backtrace agent */
        public Evaluator.Backtrace backtrace();
    }
}
