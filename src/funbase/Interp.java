/*
 * Interp.java
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

import java.util.Stack;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import funbase.Value.WrongKindException;

/** A trivial runtime translator that interprets the funcode */
public class Interp implements FunCode.Jit, Evaluator.Backtrace {
    private static Stack<FunCode> backtrace = new Stack<FunCode>();
    private FunCode root = null;

    /** Create a function factory that builds interpreted closures */
    @Override
    public Function.Factory translate(final FunCode funcode) {
	return new Function.Factory() {
	    @Override
	    public Function newClosure(Value func, Value fvars[]) {
		return new InterpFunction(funcode.arity, funcode, fvars);
	    }
	};
    }  

    private Primitive.Factory primFactory = new ReflectionFactory();

    public Primitive.Factory primitiveFactory() {
        return primFactory;
    }

    public Evaluator.Backtrace backtrace() {
        return this;
    }

    @Override
    public void initStack() {
	backtrace.clear();
	this.root = null;
    }

    @Override
    public void setRoot(Value root) { 
	Function.Closure cl = (Function.Closure) root.subr;
	this.root = cl.getCode();
    }

    @Override
    public String[] context(String me) {
	String caller = null, callee = me;

	for (int i = backtrace.size()-1; i >= 0; i--) {
	    FunCode f = backtrace.get(i);
	    if (f == root) break;

	    if (f.frozen) 
		callee = f.name;
	    else {
		caller = f.name;
		break;
	    }
	}

	return new String[] { caller, callee };
    }

    /** A closure containing an interpreter for funcode */
    public static class InterpFunction extends Function.Closure {
	public InterpFunction(int arity, FunCode code, Value fvars[]) {
	    super(arity, code, fvars);
	}

        private static final int FRAME = 16;

        private Value[] expand(Value frame[], int sp, int fsize) {
            int n = frame.length;
            Value newframe[] = new Value[2*n];
            System.arraycopy(frame, 0, newframe, 0, sp);
            System.arraycopy(frame, n-fsize, newframe, 2*n-fsize, fsize);
            return newframe;
        }

	@Override
	public Value apply(int nargs, Value args[], int base) {
	    backtrace.push(code);

	    if (nargs != arity) 
		Evaluator.err_nargs(code.name, nargs, arity);

	    if (--Evaluator.quantum <= 0) Evaluator.checkpoint();

	    int prog[] = code.code;
            Value frame[] = new Value[FRAME];
	    int pc = 0, trap = -1, sp = 0, fsize = 0;

	    for (;;) {
                if (sp + 1 > frame.length - fsize)
                    frame = expand(frame, sp, fsize);

		FunCode.Opcode op = FunCode.decode[prog[pc++]];
		int rand = 0;
                if (op.nrands > 0) rand = prog[pc++];
		
		switch (op) {
		    case GLOBAL: {
			Name x = (Name) code.consts[rand];
			Value v = x.getGlodef();
			if (v == null) Evaluator.err_notdef(x);
			frame[sp++] = v;
			break;
		    }

		    case LOCAL:
			frame[sp++] = frame[frame.length-rand-1];
			break;

		    case ARG:
			frame[sp++] = args[base+rand];
			break;

		    case FVAR:
			frame[sp++] = fvars[rand];
			break;

		    case BIND:
                        if (rand >= fsize) fsize = rand+1;
			frame[frame.length-rand-1] = frame[--sp];
			break;

		    case POP:
			sp--;
			break;

                    case PUSH:
                        frame[sp++] = Value.number(rand);
                        break;

		    case QUOTE:
			frame[sp++] = code.consts[rand];
			break;

		    case NIL:
			frame[sp++] = Value.nil;
			break;

		    case CONS:
			sp--;
			frame[sp-1] = Value.cons(frame[sp-1], frame[sp]);
			break;

		    case CALL:
			sp -= rand;
			Value fun = frame[sp-1];
			frame[sp-1] = fun.subr.apply(rand, frame, sp);
			break;

		    case TCALL:
			if (rand != nargs)
			    Evaluator.err_nargs(code.name, rand, nargs);
			sp -= rand;
			System.arraycopy(frame, sp, args, base, nargs);
			pc = 0; trap = -1; sp = 0;
			if (--Evaluator.quantum <= 0) Evaluator.checkpoint();
			break;

		    case CLOSURE: {
			sp -= rand-1;
			FunCode body = (FunCode) frame[sp-1];
			Value fvars[] = new Value[rand];
			System.arraycopy(frame, sp, fvars, 1, rand-1);
			frame[sp-1] = body.makeClosure(fvars);
			break;
		    }

		    case TRAP:
			trap = rand;
			break;

		    case FAIL:
			Evaluator.err_nomatch(args, base, code.arity);
			break;

		    case JFALSE:
			try {
			    Value b = frame[--sp];
			    if (! b.asBoolean()) pc = rand;
			} catch (WrongKindException ex) {
			    Evaluator.err_boolcond();
			}
			break;

		    case JUMP:
			pc = rand;
			break;

		    case RETURN:
			backtrace.pop();
                        assert (sp == 1);
			return frame[--sp];

		    case MPLUS: {
                        Value v = frame[--sp].matchPlus(code.consts[rand]);
                        if (v != null)
                            frame[sp++] = v;
                        else
                            pc = trap;
			break;
                    }

		    case MEQ:
			sp -= 2;
			if (! frame[sp].equals(frame[sp+1])) pc = trap;
			break;

		    case MNIL:
			if (frame[--sp] != Value.nil) pc = trap;
			break;

		    case MCONS:
			try {
			    Value.Cons cell = (Value.Cons) frame[sp-1];
                            frame[sp-1] = cell.tail;
			    frame[sp++] = cell.head;
			} catch (ClassCastException ex) {
                            sp--; pc = trap;
			}
			break;

                    case MPAIR: {
                        try {
                            Value.Pair cell = (Value.Pair) frame[sp-1];
                            frame[sp-1] = cell.snd;
                            frame[sp++] = cell.fst;
                        }
                        catch (ClassCastException ex) {
                            sp--; pc = trap;
                        }
                        break;
                    }

		    case MPRIM: {
			Primitive.Constructor cons =
                            (Primitive.Constructor) frame[--sp].subr;
			Value obj = frame[--sp];
			Value vs[] = cons.pattMatch(rand, obj);
			if (vs == null)
			    pc = trap;
			else {
                            while (sp + rand > frame.length - fsize)
                                frame = expand(frame, sp, fsize);
			    System.arraycopy(vs, 0, frame, sp, rand);
			    sp += rand;
			}
			break;
		    }

		    case PREP:
		    case PUTARG:
                    case FRAME:
			// Used only by JIT
			break;

		    default:
			throw new Error("bad opcode " + op);
		}
	    }
	}
    }
}
