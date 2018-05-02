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
	GLOBAL(1),   // [#global, x] becomes GLOBAL i where consts[i] = x:
		     //   push value of global name x
	LOCAL(1),    // [#local, n]: push value of local variable n
	ARG(1),	     // [#arg, n]: push value of argument n
	FVAR(1),     // [#fvar, n]: push value of free variable n
	BIND(1),     // [#bind, n]: pop value and store as local n
	POP,         // [#pop]: pop and discard a value
	QUOTE(1),    // [#quote, x] becomes QUOTE i where consts[i] = x:
		     //   push the constant x
        PUSH(1),     // #push(n): push the integer constant n
	NIL,	     // [#nil]: push the empty list
	CONS,        // [#cons]: pop a tail then a head, push a cons
	TRAP(1),     // [#trap, lab] becomes TRAP i: set trap register
	FAIL,        // [#fail]: die with "no clause matched"
	JFALSE(1),   // [#jfalse, lab] becomes JFALSE n:
		     //   pop a boolean and jump if false
	JUMP(1),     // [#jump, lab] becomes JUMP n:
		     //   jump to instruction at offset n
	RETURN,      // [#return]: return from function
	MPLUS(1),    // [#mplus, k]: match an n+k pattern by popping integer
		     //   x with x >= k and pushing x-k; otherwise trap
	MEQ,         // [#meq]: pop two values and trap if not equal
	MNIL,        // [#mnil]: pop the empty list; otherwise trap
	MCONS,       // [#mcons]: find a cons cell and push its head
	GETTAIL,     // [#gettail]: fetch tail following MCONS
        MPAIR,       // [#mpair]: find a pair and push its fst
        GETSND,      // [#getsnd]: fetch snd following MPAIR
	TCALL(1),    // [#tcall, n]: tail recursive call
	PREP(1),     // [#prep, n]: prepare for a call with n arguments
        FRAME(1),    // [#frame, n]: create a free var frame with n slots
	PUTARG(1),   // [#putarg, i]: mark i'th argument of a call
        CALL(1),     // [#call, n]: call a function with n arguments
        CLOSURE(1),  // [#closure, n]: form a closure with n free variables
        MPRIM(1);    // [#mprim, n]: pattern match a constructor with n args

        public final int nrands;

        private Opcode() { nrands = 0; }
        private Opcode(int nrands) { this.nrands = nrands; }
    }
    
    public static final Opcode decode[] = Opcode.values();

    /** Name of the function (used for error messages) */
    public final String name;

    /** Number of arguments */
    public final int arity;
    
    /** Whether to freeze the error context on entry */
    public final boolean frozen = Name.getFreezer();

    /** New-style code */
    public final int code[];

    /** Constant pool */
    public final Value consts[];

    public transient Function.Factory jitcode;
    
    private static Jit translator;
    
    public FunCode(String name, int arity, int code[], Value consts[]) {
	this.name = name; this.arity = arity;
	this.code = code; this.consts = consts;
    }

    public FunCode(String name, int arity, List<Integer> code,
                   List<Value> consts) {
        this(name, arity, makeIntArray(code),
             consts.toArray(new Value[consts.size()]));
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
    
    private static int level = 4;
    private static int vcount = 0;

    public void dump0(PrintWriter out) {
        vcount = 0; dump(out);
    }

    @Override
    public void dump(PrintWriter out) {
        if (vcount > 0)
            out.printf("\n%"+level+"s", "");
        out.printf("F(\"%s\", %d,", name, arity);
        level += 2; vcount = 99;

        out.printf(" (() -> {");
        for (int i = 0; i < code.length; ) {
            Opcode op = decode[code[i++]];

            if (vcount < 4)
                out.printf(" ");
            else {
                out.printf("\n%"+level+"s", "");
                vcount = 0;
            }
            
            if (op.nrands == 0)
                out.printf("I(%s);", op.name());
            else
                out.printf("I(%s, %d);", op.name(), code[i++]);

            vcount++;
        }
        out.printf(" })");

        vcount = 99;
        for (int i = 0; i < consts.length; i++) {
            if (vcount < 4)
                out.printf(", ");
            else {
                out.printf(",\n%"+level+"s", "");
                vcount = 0;
            }
            consts[i].dump(out);
            vcount++;
        }

        level -= 2; vcount = 99;
        out.printf(")");
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
                                  int arity, Value codelist) {
        List<Integer> code = new ArrayList<>();
	List<Value> consts = new ArrayList<>();
	
	for (Value xs = codelist; prim.isCons(xs); xs = prim.tail(xs)) {
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
            code.add(op.ordinal());

            if (arg != null) {
                int rand;
                
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

                code.add(rand);
            }
	}
	
	return new FunCode(name.toString(), arity, code, consts);
    }

    private static int[] makeIntArray(List<Integer> xs) {
        int i = 0, n = xs.size();
        int result[] = new int[n];
        for (Integer a : xs) result[i++] = a;
        return result;
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
