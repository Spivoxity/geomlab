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

import funbase.Value.WrongKindException;

/** A trivial runtime translator that interprets the funcode */
public class Interp implements FunCode.Jit {
    /** Create a function factory that builds interpreted closures */
    @Override
    public Function.Factory translate(final FunCode funcode) {
	return new Function.Factory() {
	    @Override
	    public Function newClosure(Value.FunValue func, Value fvars[]) {
		return new InterpFunction(funcode.arity, funcode, fvars);
	    }
	};
    }  

    /** A closure containing an interpreter for funcode */
    public static class InterpFunction extends Function.Closure {
	public InterpFunction(int arity, FunCode code, Value fvars[]) {
	    super(arity, code, fvars);
	}

	@Override
	public Value apply(Value args[], int base, int nargs, 
		ErrContext cxt) {
	    if (nargs != arity) 
		cxt.err_nargs(code.name, nargs, arity);

	    if (code.frozen && !Name.freezer)
		cxt = cxt.freezeEnter(code.name);
	    else
		cxt = cxt.enter(code.errcxt);

	    if (--Evaluator.quantum <= 0) Evaluator.checkpoint();

	    FunCode.Opcode instrs[] = code.instrs;
	    int rands[] = code.rands;
	    Value frame[] = new Value[code.fsize + code.ssize];
	    int pc = 0, trap = -1, sp = code.fsize;

	    for (;;) {
		FunCode.Opcode op = instrs[pc];
		int rand = rands[pc];
		pc++;
		
		switch (op) {
		    case GLOBAL: {
			Name x = (Name) code.consts[rand];
			Value v = x.getGlodef();
			if (v == null) cxt.err_notdef(x);
			frame[sp++] = v;
			break;
		    }

		    case LOCAL:
			frame[sp++] = frame[rand];
			break;

		    case ARG:
			frame[sp++] = args[base+rand];
			break;

		    case FVAR:
			frame[sp++] = fvars[rand];
			break;

		    case BIND:
			frame[rand] = frame[--sp];
			break;

		    case POP:
			sp--;
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
			try {
			    sp -= rand;
			    frame[sp-1] = 
				((Value.FunValue) frame[sp-1]).subr.apply
				    (frame, sp, rand, cxt);
			}
			catch (ClassCastException _) {
			    cxt.err_apply();
			}
			break;

		    case TCALL:
			if (rand != nargs)
			    cxt.err_nargs(code.name, rand, nargs);
			sp -= rand;
			System.arraycopy(frame, sp, args, base, nargs);
			pc = 0; trap = -1; sp = code.fsize;
			if (--Evaluator.quantum <= 0) Evaluator.checkpoint();
			break;

		    case CLOSURE: {
			Value fvars[] = new Value[rand+1];
			sp -= rand;
			System.arraycopy(frame, sp, fvars, 1, rand);
			frame[sp-1] =
			    frame[sp-1].makeClosure(fvars);
			break;
		    }

		    case TRAP:
			trap = rand;
			break;

		    case FAIL:
			cxt.err_nomatch(args, base, code.arity);
			break;

		    case JFALSE:
			try {
			    if (! frame[--sp].asBoolean()) 
				pc = rand;
			} catch (WrongKindException _) {
			    cxt.err_boolcond();
			}
			break;

		    case JUMP:
			pc = rand;
			break;

		    case RETURN:
			return frame[--sp];

		    case MPLUS: {
			sp -= 2;
			Value v = frame[sp].matchPlus(frame[sp+1]);
			if (v == null)
			    pc = trap;
			else
			    frame[sp++] = v;
			break;
		    }

		    case MEQ:
			sp -= 2;
			if (! frame[sp].equals(frame[sp+1]))
			    pc = trap;
			break;

		    case MNIL:
			if (! frame[--sp].isNilValue())
			    pc = trap;
			break;

		    case MCONS: {
			Value v = frame[--sp];
			try {
			    frame[sp] = v.getTail();
			    frame[sp+1] = v.getHead();
			    sp += 2;
			} catch (WrongKindException _) {
			    pc = trap;
			}
			break;
		    }

		    case MPRIM: {
			Value cons = frame[--sp];
			Value obj = frame[--sp];
			Value vs[] = cons.pattMatch(obj, rand, cxt);
			if (vs == null)
			    pc = trap;
			else {
			    System.arraycopy(vs, 0, frame, sp, rand);
			    sp += rand;
			}
			break;
		    }

		    case PREP:
		    case PUTARG:
			// Used only by JIT
			break;

		    default:
			throw new Error("bad opcode " + op);
		}
	    }
	}
    }
}
