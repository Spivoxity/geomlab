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
import java.util.*;
import java.lang.reflect.Array;

import funbase.Evaluator.*;

/** Abstract superclass of all values in GeomLab */
public abstract class Value implements Serializable {
    private static final long serialVersionUID = 1L;
	
    /* The actual classes used to represent values are those contained
     * in this file, together with the classes Closure and Primitive -- plus
     * others that are installed dynamically, like ColorValue and Picture.
     * These classes are partially decoupled from the rest of the
     * system, and hidden behind static factory methods in the Value
     * class. */
    
    /** Code to activate when calling this value as a function */
    public Function subr;

    protected Value(Function subr) {
	this.subr = subr;
    }

    public Value() {
	this(Function.no_func);
    }

    public Value apply(Value args[], ErrContext cxt) {
	return subr.apply(args, 0, args.length, cxt);
    }

    public Value[] pattMatch(Value obj, int nargs, ErrContext cxt) {
	return subr.pattMatch(obj, nargs, cxt);
    }

    @Override
    public String toString() {
	StringWriter buf = new StringWriter();
	this.printOn(new PrintWriter(buf));
	return buf.toString();
    }
    
    /** Print the value on a stream */
    public abstract void printOn(PrintWriter out);
    
    /** Dump the value to standard output in boot format */
    public void dump(PrintWriter out) {
	throw new EvalException
	    (String.format("can't dump %s", this.getClass()));
    }
    
    /** Create a closure with this object as body */
    public Value makeClosure(Value fvars[]) {
	throw new EvalException("bad body for closure");
    }

    // Type tests
    
    public boolean isNumValue() { return (this instanceof NumValue); }
    public boolean isBoolValue() { return (this instanceof BoolValue); }
    public boolean isFunValue() { return (this instanceof FunValue); }
    public boolean isConsValue() { return (this instanceof ConsValue); }
    public boolean isNilValue() { return (this instanceof NilValue); }
    
    // Accessors: the default implementations raise WrongKindException.
    // (see also Primitive.head etc.)
    
    public boolean asBoolean() throws WrongKindException {
	throw new WrongKindException();
    }
    
    public double asNumber() throws WrongKindException {
	throw new WrongKindException();
    }
    
    public String asString() throws WrongKindException {
	throw new WrongKindException();
    }
    
    public Value getHead() throws WrongKindException {
	throw new WrongKindException();
    }
    
    public Value getTail() throws WrongKindException {
	throw new WrongKindException();
    }
    
    public void setTail(Value tail) throws WrongKindException {
	throw new WrongKindException();
    }

    public Value matchPlus(Value inc) {
	return null;
    }

    // Factory methods
    
    public static Value makeNumValue(double val) { 
	return NumValue.getInstance(val);
    }

    public static Value makeBoolValue(boolean val) {
	return BoolValue.getInstance(val);
    }
    
    public static FunValue makeFunValue(Function subr) {
	return new FunValue(subr);
    }

    public static Value makeStringValue(char ch) {
	return StringValue.getInstance(ch);
    }
    
    public static Value makeStringValue(String text) {
	return StringValue.getInstance(text);
    }
    
    public static final Value nil = new NilValue();
    
    public static Value cons(Value hd, Value tl) {
	return new ConsValue(hd, tl);
    }
    
    /** Make a list from a sequence of values */
    public static Value makeList(Value... elems) {
	return makeList(elems, 0, elems.length);
    }
    
    /** Convert elems[base..base+count) to a list value */
    public static Value makeList(Value elems[], int base, int count) {
        Value val = nil;
        for (int i = count-1; i >= 0; i--)
            val = cons(elems[base+i], val);
        return val;
    }
    
    /** Convert a list value to an array of values. */
    public static Value[] makeArray(Value xs) throws WrongKindException {
	List<Value> elems = new ArrayList<Value>();

	while (xs.isConsValue()) {
	    elems.add(xs.getHead());
	    xs = xs.getTail();
	}

	if (! xs.isNilValue()) throw new WrongKindException();

	return elems.toArray(new Value[elems.size()]);
    }

    /** Convert a list value to an array, casting each element to class cl. */
    public static <T> T[] makeArray(Class<T> cl, Value xs) 
					throws WrongKindException {
	List<T> elems = new ArrayList<T>();

	while (xs.isConsValue()) {
	    try {
		elems.add(cl.cast(xs.getHead()));
	    } catch (ClassCastException _) {
		throw new WrongKindException();
	    }
	    xs = xs.getTail();
	}

	if (! xs.isNilValue()) throw new WrongKindException();

	@SuppressWarnings("unchecked")
	T[] result = (T[]) Array.newInstance(cl, elems.size());
	return elems.toArray(result);
    }

    public static void printNumber(PrintWriter out, double x) {
	if (x == (int) x)
	    out.print((int) x);
	else if (Double.isNaN(x))
	    out.print("NaN");
	else {
	    double y = x;
	    if (y < 0.0) {
		out.print('-');
		y = -y;
	    }
	    if (Double.isInfinite(y))
		out.print("Infinity");
	    else {
		// Sometimes stupid persistence is the best way ...
		String pic;
		if (y < 0.001)           pic = "0.0######E0";
		else if (y < 0.01)       pic = "0.000#######";
		else if (y < 0.1)        pic = "0.00#######";
		else if (y < 1.0)        pic = "0.0#######";
		else if (y < 10.0)       pic = "0.0######";
		else if (y < 100.0)      pic = "#0.0#####";
		else if (y < 1000.0)     pic = "##0.0####";
		else if (y < 10000.0)    pic = "###0.0###";
		else if (y < 100000.0)   pic = "####0.0##";
		else if (y < 1000000.0)  pic = "#####0.0#";
		else if (y < 10000000.0) pic = "######0.0";
		else                     pic = "0.0######E0";
		NumberFormat fmt = new DecimalFormat(pic);
		out.print(fmt.format(y));
	    }
	}
    }

    /** Exception that is thrown when accessors are applied to the
     *  wrong kind of Value */
    public static class WrongKindException extends Exception { /* Empty */ }
    
    /** A numeric value represented as a double-precision float */
    public static class NumValue extends Value {
	private static final long serialVersionUID = 1L;

	/** The value */
	public final double val;
	
	private NumValue(double val) {
	    this.val = val;
	}
	
	@Override
	public void printOn(PrintWriter out) {
	    Value.printNumber(out, val);
	}

	@Override
	public double asNumber() {
	    return val;
	}
	
	private static final int MIN = -1, MAX = 2000;
	private static Value smallints[] = new Value[MAX-MIN+1];

	public static Value getInstance(double val) {
	    int n = (int) val;
	    if (val != n || n < MIN || n > MAX)
		return new NumValue(val);
	    else {
		if (smallints[n-MIN] == null)
		    smallints[n-MIN] = new NumValue(n);
		return smallints[n-MIN];
	    }
	}

	@Override
	public boolean equals(Object a) {
	    return (a instanceof NumValue && val == ((NumValue) a).val);
	}
	
	@Override
	public Value matchPlus(Value iv) {
	    double inc = ((NumValue) iv).val;
	    double x = val - inc;
	    if (inc > 0 && x >= 0 && x == (int) x)
		return makeNumValue(x);
	    else
		return null;
	}

	@Override
	public void dump(PrintWriter out) {
	    if (val == (int) val)
		out.printf("integer %d\n", (int) val);
	    else
		out.printf("real %.12g\n", val);
	}
    }
    
    /** A boolean value */
    public static class BoolValue extends Value {
	private static final long serialVersionUID = 1L;

	public final boolean val;
	
	private BoolValue(boolean val) {
	    this.val = val;
	}
	
	@Override
	public void printOn(PrintWriter out) {
	    out.print((val ? "true" : "false"));
	}
	
	@Override
	public boolean asBoolean() { return val; }
	
	@Override
	public boolean equals(Object a) {
	    return (a instanceof BoolValue && val == ((BoolValue) a).val);
	}
	
	/** Singletons */
	public static final BoolValue 
	    truth = new BoolValue(true), 
	    falsity = new BoolValue(false);
	
	public static Value getInstance(boolean val) {
	    return (val ? truth : falsity);
	}
	
	/* After input from a serialized stream, readResolve lets us replace
	 * the constructed instance with one of the standard instances. */
	public Object readResolve() { return getInstance(val); }
	
	@Override
	public void dump(PrintWriter out) {
	    out.printf("boolean %d\n", (val ? 1 : 0));
	}
    }
    
    /** A function value */
    public static class FunValue extends Value {
	private static final long serialVersionUID = 1L;
	
	public FunValue(Function subr) {
	    super(subr);
	}

	@Override
	public void dump(PrintWriter out) {
	    subr.dump(out);
	}

	@Override
	public void printOn(PrintWriter out) {
	    out.printf("<function(%d)>", subr.arity);
	}

	protected Object writeReplace() {
	    return subr.serialProxy(this);
	}

        private Object readResolve() {
            /* Ask the body to build a closure */
            subr = subr.resolveProxy(this);
	    return this;
        }
    }

    /** A string value */
    private static class StringValue extends Value {
	private static final long serialVersionUID = 1L;

	private String text;
	
	private StringValue(String text) {
	    Evaluator.countCons();
	    this.text = text;
	}
	
	@Override
	public void printOn(PrintWriter out) {
	    out.format("\"%s\"", text);
	}
	
	@Override
	public String asString() { return text; }
	@Override
	public String toString() { return text; }
	
	@Override
	public boolean equals(Object a) {
	    return (a instanceof StringValue 
		    && text.equals(((StringValue) a).text));
	}

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

	    return new StringValue(String.valueOf(ch));
	}

	public static Value getInstance(String text) {
	    if (text.length() == 0)
		return emptyString;
	    else if (text.length() == 1 && text.charAt(0) < 256)
		return charStrings[text.charAt(0)];
	    else
		return new StringValue(text);
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
	
	@Override
	public void printOn(PrintWriter out) {
	    out.print("[]");
	}
	
	@Override
	public boolean equals(Object a) {
	    return (a instanceof NilValue);
	}
	
	public Object readResolve() { return Value.nil; }
	
	@Override
	public void dump(PrintWriter out) {
	    out.printf("nil\n");
	}
    }
    
    /** A value representing a non-empty list */
    public static class ConsValue extends Value {
	private static final long serialVersionUID = 1L;

	public Value head, tail;
	
	public ConsValue(Value head, Value tail) {
	    Evaluator.countCons();
	    this.head = head;
	    this.tail = tail;
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
	    if (! (xs instanceof NilValue)) {
		out.print(" . ");
		xs.printOn(out);
	    }
	    out.print("]");
	}
	
	@Override
	public Value getHead() { return head; }
	@Override
	public Value getTail() { return tail; }
	@Override
	public void setTail(Value tail) { this.tail = tail; }

	@Override
	public boolean equals(Object a) {
	    if (! (a instanceof ConsValue)) return false;
	    ConsValue acons = (ConsValue) a;
	    return (head.equals(acons.head) && tail.equals(acons.tail));
	}
    }
}
