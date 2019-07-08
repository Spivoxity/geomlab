/*
 * Function.java
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

package funbase;

import funbase.Primitive.PRIMITIVE;

import java.io.PrintWriter;
import java.io.Serializable;

/** A function on values.  This superclass represents a dummy function
    that cannot be called; subclasses represent primitives and functions
    defined in the Fun language. */
public abstract class Function implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public final String name;

    public final int arity;
    
    public Function(String name, int arity) {
        this.name = name;
	this.arity = arity;
    }

    /** Apply the function to a list of arguments. */
    public Value apply(Value args[]) {
	return apply(args.length, args, 0);
    }

    /** Apply the function to a list of arguments with base. */
    public abstract Value apply(int nargs, Value args[], int base);
    
    /* The default is for the apply<n> methods to delegate to the
       general apply method.  For JIT functions with a small number of
       arguments, one of the apply<n> methods can be overridden, so
       that calls with the correct number of arguments go directly,
       without allocating an argument array.  Then the general apply
       method will also be overridden to call the specific method if
       the number of arguments is correct.  The other apply<n> methods
       remain as delegating to the general one, and so let the general
       method give an error message when the number of arguments is
       wrong. 

       With the funcode interpreter and reflection-based primitives,
       it's best to define the method apply(nargs, args[], base)
       and have the apply(args[]) method delegate to it.  With the JIT,
       it's best to go the other way, since it is apply(args[]) that
       is called.  The other apply method can be redefined (inefficiently,
       to be sure) for completeness, but will never be used. */

    /** Apply the function to 0 arguments */
    public Value apply0() {
	return apply(new Value[0]);
    }

    /** Apply the function to 1 argument */
    public Value apply1(Value x) {
	return apply(new Value[] { x });
    }

    /** Apply the function to 2 arguments */
    public Value apply2(Value x, Value y) {
	return apply(new Value[] { x, y });
    }

    /** Apply the function to 3 arguments */
    public Value apply3(Value x, Value y, Value z) {
	return apply(new Value[] { x, y, z });
    }

    /** Apply the function to 4 arguments */
    public Value apply4(Value x, Value y, Value z, Value u) {
	return apply(new Value[] { x, y, z, u });
    }

    /** Apply the function to 5 arguments */
    public Value apply5(Value x, Value y, Value z,
			Value u, Value v) {
	return apply(new Value[] { x, y, z, u, v });
    }

    /** Apply the function to 6 arguments */
    public Value apply6(Value x, Value y, Value z,
			Value u, Value v, Value w) {
	return apply(new Value[] { x, y, z, u, v, w });
    }

    public void dump(PrintWriter out) {
	throw new Error("dumping a dummy function");
    }

    /** Method called by Lambda.writeReplace to determine a proxy. */
    public Object serialProxy(Value funval) {
	return funval;
    }
    
    /** Method called by Lambda.readResolve to build a closure */
    public Function resolveProxy(Value funval) {
	return this;
    }

    /** A (code, context) pair.  Subclasses are used to represent various
	kinds of compiled function, and instances of this superclass
	represent serialized functions that will be recompiled on loading. */
    public static class Closure extends Function {
	private static final long serialVersionUID = 1L;

        /** Code for the function body */
        protected FunCode code;
    
        /** Values for the free variables of the closure */
        protected Value fvars[];
        
        public Closure(String name, int arity) { 
            super(name, arity); 
        }
    
        public Closure(int arity, FunCode code, Value fvars[]) {
            super(code.name, arity);
            this.code = code;
            this.fvars = fvars;
        }

	public FunCode getCode() { return code; }

	@Override
	public Value apply(int nargs, Value args[], int base) {
	    throw new Error("Calling an abstract closure");
	}

        @Override
        public void dump(PrintWriter out) {
            if (fvars != null && fvars.length > 1)
        	throw new Error("dumping a closure with free variables");
            out.printf("C(");
            code.dump0(out);
            out.printf(")");
        }

        @Override
        public Value serialProxy(Value funval) {
            /* All user functions serialize as funcode, and are rebuilt on
	       loading. For JIT functions, that means that they are 
	       translated again, typically on first use.  An interpreter
	       function can be serialized in one instance of GeomLab
	       and then become a JIT function when it is deserialized
	       in an other instance. */
            
            // Must copy the fvars before tying the knot.
            Value fvars1[] = new Value[fvars.length];
            System.arraycopy(fvars, 1, fvars1, 1, fvars.length-1);
            Value result = 
        	Value.lambda(new Function.Closure(arity, code, fvars1));
            fvars1[0] = result;
            return result;
        }

        /* There's potentially a big problem here: because the closure
           graph can be cyclic in arbitrary ways, we need to be
           sure that there are no other references to this memento 
           that are deserialized before readResolve() gets to replace 
           the memento with a proper JIT translation.  But it's all OK, 
           because the only reference to the memento is from the Lambda 
           wrapper that surrounds it.  The Lambda wrapper is not 
           readResolved, so it can safely be shared. */
    
        @Override
        public Function resolveProxy(Value funval) {
            // Ask the body to build a closure.
            return code.buildClosure(funval, fvars);
        }
    }

    public interface Factory {
	public Function newClosure(Value func, Value fvars[]);
    }

    public static final Function nullFunction = 
        new Function("*unknown*", -1) {
            @Override
            public Value apply(int nargs, Value args[], int base) {
                Evaluator.err_apply();
                return null;
            }

            public Object readResolve() {
                return nullFunction;
            }
        };

    @PRIMITIVE
    public static Value _apply(Primitive prim, Value x, Value y) {
        Value args[] = prim.toArray(y);
        return x.apply(args);
    }

    @PRIMITIVE
    public static Value _closure(FunCode body) {
	return body.makeClosure(new Value[1]);
    }
}
