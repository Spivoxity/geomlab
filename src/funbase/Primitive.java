/*
 * Primitive.java
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

import java.util.*;
import java.io.*;

import funbase.Evaluator.*;

/** A value that represents a primitive function like 'sqrt' or '+'. 
 *  
 *  Direct concrete subclasses should implement the invoke method. */
public abstract class Primitive extends Function {
    @SuppressWarnings("unused")
    private static final String svnid =
	"$Id: Primitive.java 642 2012-07-15 22:31:52Z mike $";
    
    /** Name of the primitive */
    public final String name;

    /** Context of current invocation */
    public transient ErrContext cxt;
    
    protected Primitive(String name, int arity) {
	super(arity);
	this.name = name;
    }

    @Override
    public void dump(PrintWriter out) {
	out.printf("primitive \"%s\"\n", name);
    }

    /* Primitives are replaced by Memento objects when making a serialized 
     * stream. This provides independence of the stream from the particular 
     * classes that are used to implement primitives.  Since these are mostly
     * anonymous inner classes scattered throughout the code, this is a
     * very good thing. */
    
    protected Object writeReplace() {
	return new Memento(this);
    }
    
    /** A primitive with zero arguments.
     * 
     *  Concrete subclasses should implement invoke0. */
    public static abstract class Prim0 extends Primitive {
	public Prim0(String name) { super(name, 0); }

	protected abstract Value invoke0();

	@Override
	public Value apply0(ErrContext cxt) {
	    this.cxt = cxt.primEnter(name);
	    return invoke0();
	}

	@Override
	public Value apply(Value args[], int base, int nargs, ErrContext cxt) {
	    if (nargs != 0) cxt.err_nargs(name, nargs, 0);
	    this.cxt = cxt.primEnter(name);
	    return invoke0();
	}
    }

    /** A primitive with one argument.
     * 
     *  Concrete subclasses should implement invoke1. */
    public static abstract class Prim1 extends Primitive {
	public Prim1(String name) { super(name, 1); }

	protected abstract Value invoke1(Value x);

	@Override
	public Value apply1(Value x, ErrContext cxt) {
	    this.cxt = cxt.primEnter(name);
	    return invoke1(x);
	}

	@Override
	public Value apply(Value args[], int base, int nargs, ErrContext cxt) {
	    if (nargs != 1) cxt.err_nargs(name, nargs, 1);
	    this.cxt = cxt.primEnter(name);
	    return invoke1(args[base+0]);
	}
    }

    /** A primitive with two arguments.
     * 
     *  Concrete subclasses should implement invoke2. */
    public static abstract class Prim2 extends Primitive {
	public Prim2(String name) { super(name, 2); }

	protected abstract Value invoke2(Value x, Value y);

	@Override
	public Value apply2(Value x, Value y, ErrContext cxt) {
	    this.cxt = cxt.primEnter(name);
	    return invoke2(x, y);
	}

	@Override
	public Value apply(Value args[], int base, int nargs, ErrContext cxt) {
	    if (nargs != 2) cxt.err_nargs(name, nargs, 2);
	    this.cxt = cxt.primEnter(name);
	    return invoke2(args[base+0], args[base+1]);
	}
    }

    /** A primitive with three arguments.
     * 
     *  Concrete subclasses should implement invoke3.
     */
    public static abstract class Prim3 extends Primitive {
	public Prim3(String name) { super(name, 3); }

	protected abstract Value invoke3(Value x, Value y, Value z);

	@Override
	public Value apply3(Value x, Value y, Value z, ErrContext cxt) {
	    this.cxt = cxt.primEnter(name);
	    return invoke3(x, y, z);
	}

	@Override
	public Value apply(Value args[], int base, int nargs, ErrContext cxt) {
	    if (nargs != 3) cxt.err_nargs(name, nargs, 3);
	    this.cxt = cxt.primEnter(name);
	    return invoke3(args[base+0], args[base+1], args[base+2]);
	}
    }

    public static abstract class PrimN extends Primitive {
	public PrimN(String name, int arity) { super(name, arity); }

	/** Invoke the primitive with arguments args[base..) */ 
	protected abstract Value invoke(Value args[], int base);

	@Override
	public Value apply(Value args[], int base, int nargs, ErrContext cxt) {
	    if (nargs != arity) cxt.err_nargs(name, nargs, arity);
	    this.cxt = cxt.primEnter(name);
	    return invoke(args, base);
	}
    }
    
    /** Table of all primitives */
    protected static Map<String, Primitive> primitives = 
	new HashMap<String, Primitive>(100);
    
    /** Register a new primitive */
    public static void register(Primitive p) {
	primitives.put(p.name, p);
    }
    
    /** Find a registered primitive */
    public static Primitive find(String name) {
	Primitive prim = primitives.get(name);
	if (prim == null)
	    throw new EvalException("Primitive " + name + " is not defined");
	return prim;
    }
    
    /** Discard all registered primitives */
    public static void clearPrimitives() {
	primitives.clear();
    }
    
    /** Serialized substitute for a primitive */
    private static class Memento implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	public Memento(Primitive prim) {
	    this.name = prim.name;
	}
	
	private Object readResolve() throws ObjectStreamException {
	    /* Replace the memento by the genuine primitive */
	    Object prim = primitives.get(name);
	    if (prim == null)
		throw new InvalidObjectException(
			"Primitive " + name + " could not be found");
	    return prim;
	}
    }
}
