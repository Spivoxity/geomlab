/*
 * JitFunction.java
 * 
 * This file is part of GeomLab
 * Copyright (c) 2010 J. M. Spivey
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

import funbase.ErrContext;
import funbase.FunCode;
import funbase.Function;
import funbase.Value;
import funbase.Evaluator.EvalException;

/** Superclass for all JIT-compiled functions */
public abstract class JitFunction extends Function.Closure 
    		implements Function.Factory, Cloneable {
    /** Name of the function (used for error messages) */
    protected final String name;

    /** Singleton value for error context */
    protected final ErrContext errcxt;
    
    /** Pool of constant values */
    protected Value consts[];

    public JitFunction(String name, int arity) {
	super(arity);
	this.name = name;
	this.errcxt = new ErrContext(name);
    }

    public void init(FunCode source) {
	this.code = source;
	this.consts = source.consts;
    }

    @Override
    public Function newClosure(Value.FunValue func, Value fvars[]) {
	try {
	    JitFunction body = (JitFunction) clone();
	    body.fvars = fvars;
	    return body;
	} catch (CloneNotSupportedException _) {
	    throw new EvalException("Couldn't clone for closure");
	}
    }

    // Subclasses for functions with a small number of arguments

    /* The class Func<n> overrrides the general apply method to check
       the number of arguments and call apply<n>.  A concrete subclass
       should override apply<n> with a method that does the work. */

    public static abstract class Func0 extends JitFunction {
	public Func0(String name) {
	    super(name, 0);
	}

	@Override
	public abstract Value apply0(ErrContext cxt);

	@Override
	public Value apply(Value args[], int base, int nargs, 
		ErrContext cxt) {
	    if (nargs != 0) cxt.err_nargs(name, nargs, 0);
	    return apply0(cxt);
	}
    }

    public static abstract class Func1 extends JitFunction {
	public Func1(String name) {
	    super(name, 1);
	}

	@Override
	public abstract Value apply1(Value x, ErrContext cxt);

	@Override
	public Value apply(Value args[], int base, int nargs, ErrContext cxt) {
	    if (nargs != 1) cxt.err_nargs(name, nargs, 1);
	    return apply1(args[base+0], cxt);
	}
    }

    public static abstract class Func2 extends JitFunction {
	public Func2(String name) {
	    super(name, 2);
	}

	@Override
        public abstract Value apply2(Value x, Value y, ErrContext cxt);

	@Override
	public Value apply(Value args[], int base, int nargs, ErrContext cxt) {
	    if (nargs != 2) cxt.err_nargs(name, nargs, 2);
	    return apply2(args[base+0], args[base+1], cxt);
	}
    }

    public static abstract class Func3 extends JitFunction {
	public Func3(String name) {
	    super(name, 3);
	}

	@Override
	public abstract Value apply3(Value x, Value y, Value z, ErrContext cxt);

	@Override
	public Value apply(Value args[], int base, int nargs, 
		ErrContext cxt) {
	    if (nargs != 3) cxt.err_nargs(name, nargs, 3);
	    return apply3(args[base+0], args[base+1], args[base+2], cxt);
	}
    }
}
