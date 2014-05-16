/*
 * Value.java
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

import java.io.*;
import java.text.*;

import funbase.Evaluator.*;

/** Abstract superclass of all values in GeomLab */
public abstract class Value implements Serializable {
    private static final long serialVersionUID = 1L;
	
    /** Code to activate when calling this value as a function */
    public Function subr;

    public Value() {
	this.subr = Function.nullFunction;
    }

    public Value(Function subr) {
	this.subr = subr;
    }

    public Value apply(Value args[]) {
	return subr.apply(args, args.length);
    }

    @Override
    public String toString() {
	StringWriter buf = new StringWriter();
	this.printOn(new PrintWriter(buf));
	return buf.toString();
    }
    
    /** Print the value on a stream */
    public abstract void printOn(PrintWriter out);
    
    /** Dump the value in portable format */
    public void dump(PrintWriter out) {
	throw new Error(String.format("can't dump %s", this.getClass()));
    }
    

    // Factory methods
    
    public static final Value nil = NilValue.instance;

    public static Value cons(Value hd, Value tl) {
        return ConsValue.getInstance(hd, tl);
    }
    
    /** Make a list from a sequence of values */
    public static Value makeList(Value... elems) {
        Value val = nil;
        for (int i = elems.length-1; i >= 0; i--)
            val = cons(elems[i], val);
        return val;
    }
    
    public static class WrongKindException extends Exception { }

    public boolean asBoolean() throws WrongKindException {
	throw new WrongKindException();
    }

    /** A boolean value */
    public static class BoolValue extends Value {
	private static final long serialVersionUID = 1L;

	private final boolean val;
	
	private BoolValue(boolean val) {
	    this.val = val;
	}
	
	@Override
	public boolean asBoolean() {
	    return val;
	}

	@Override
	public void printOn(PrintWriter out) {
	    out.print((val ? "true" : "false"));
	}
	
	@Override
	public boolean equals(Object a) {
	    return (a instanceof BoolValue && val == ((BoolValue) a).val);
	}
	
	/* After input from a serialized stream, readResolve lets us replace
	 * the constructed instance with one of the standard instances. */
	public Object readResolve() { return getInstance(val); }
	
	@Override
	public void dump(PrintWriter out) {
	    out.printf("boolean %d\n", (val ? 1 : 0));
	}

	public static final BoolValue 
	    truth = new BoolValue(true), 
	    falsity = new BoolValue(false);
    
	public static Value getInstance(boolean val) {
	    return (val ? truth : falsity);
	}
    }
    
    /** A function value */
    public static class FunValue extends Value {
	private static final long serialVersionUID = 1L;
	
	private FunValue(Function subr) {
	    super(subr);
	}

	@Override
	public void printOn(PrintWriter out) {
	    out.printf("<function(%d)>", subr.arity);
	}

	@Override
	public void dump(PrintWriter out) {
	    subr.dump(out);
	}

	protected Object writeReplace() {
	    return subr.serialProxy(this);
	}

        private Object readResolve() {
            /* Ask the body to build a closure */
            subr = subr.resolveProxy(this);
	    return this;
        }

        public static Value getInstance(Function subr) {
            return new FunValue(subr);
        }
    }

    /** A string value */
    public static class StringValue extends Value {
	private static final long serialVersionUID = 1L;

	public final String text;
	
	private StringValue(String text) {
	    this.text = text;
	}
	
	@Override
	public void printOn(PrintWriter out) {
	    out.format("\"%s\"", text);
	}
	
	@Override
	public String toString() { return text; }
	
	@Override
	public boolean equals(Object a) {
	    return (a instanceof StringValue 
		    && text.equals(((StringValue) a).text));
	}

        @Override 
        public int hashCode() { return text.hashCode(); }

	/** The empty string as a value */
	private static Value emptyString = new StringValue("");

	/** Singletons for one-character strings */
	private static Value charStrings[] = new StringValue[256];

	/* The "explode" primitive can create lists of many one-character 
	 * strings, so we create shared instances in advance */
	static {
	    for (int i = 0; i < 256; i++)
		charStrings[i] = new StringValue(String.valueOf((char) i));
	}

	public static Value getInstance(char ch) {
	    if (ch < 256)
		return charStrings[ch];
            else {
                Evaluator.countCons();
                return new StringValue(String.valueOf(ch));
            }
	}

	public static Value getInstance(String text) {
	    if (text.length() == 0)
		return emptyString;
	    else if (text.length() == 1 && text.charAt(0) < 256)
		return charStrings[text.charAt(0)];
	    else {
		Evaluator.countCons();
		return new StringValue(text);
	    }
	}

	/* After input from a serialized stream, readResolve lets us replace
	 * the constructed instance with a singleton. */
	public Object readResolve() {
	    if (text.length() < 2)
		return getInstance(text);
	    else
		return this;
	}
	
	@Override
	public void dump(PrintWriter out) {
	    out.printf("string \"%s\"\n", text);
	}
    }
    
    /** A value representing the empty list */
    public static class NilValue extends Value {
	private static final long serialVersionUID = 1L;

	private NilValue() { super(); }
	
        public static NilValue instance = new NilValue();

	@Override
	public void printOn(PrintWriter out) {
	    out.print("[]");
	}
	
	@Override
	public boolean equals(Object a) {
	    return (a instanceof NilValue);
	}
	
	public Object readResolve() { return instance; }
    }
    
    /** A value representing a non-empty list */
    public static class ConsValue extends Value {
	private static final long serialVersionUID = 1L;

	public final Value head, tail;
	
	private ConsValue(Value head, Value tail) {
	    Evaluator.countCons();
	    this.head = head;
	    this.tail = tail;
	}
	
        public static Value getInstance(Value head, Value tail) {
            Evaluator.countCons();
            return new ConsValue(head, tail);
        }

	@Override
	public void printOn(PrintWriter out) {
	    out.print("[");
	    head.printOn(out);
	    
	    Value xs = tail;
	    while (xs instanceof ConsValue) {
		ConsValue cons = (ConsValue) xs;
		out.print(", ");
		cons.head.printOn(out);
		xs = cons.tail;
	    }
	    if (! xs.equals(nil)) {
		// Can't happen, but let's keep it for robustness.
		out.print(" . ");
		xs.printOn(out);
	    }
	    out.print("]");
	}
	
	@Override
	public boolean equals(Object a) {
	    if (! (a instanceof ConsValue)) return false;
	    ConsValue acons = (ConsValue) a;
	    return (head.equals(acons.head) && tail.equals(acons.tail));
	}
    }
}
