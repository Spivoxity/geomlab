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

import funbase.Primitive.PRIMITIVE;
import funbase.Primitive.DESCRIPTION;
import funbase.Primitive.CONSTRUCTOR;
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
	return subr.apply(args);
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

    /** Make a list from a sequence of values */
    public static Value makeList(Value... elems) {
        Value val = nil;
        for (int i = elems.length-1; i >= 0; i--)
            val = ConsValue.instance(elems[i], val);
        return val;
    }
    
    public static class WrongKindException extends Exception { }

    public boolean asBoolean() throws WrongKindException {
	throw new WrongKindException();
    }

    public double asNumber() throws WrongKindException {
	throw new WrongKindException();
    }

    public int asInteger() throws WrongKindException {
        return (int) Math.round(this.asNumber());
    }

    public String asString() throws WrongKindException {
        throw new WrongKindException();
    }

    /** Print a number nicely. */
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

    /** A numeric value represented as a double-precision float */
    @DESCRIPTION("a numeric")
    public static class NumValue extends Value {
	private static final long serialVersionUID = 1L;

	/** The value */
	private final double val;
	
	private NumValue(double val) {
	    this.val = val;
	}
	
	@Override
	public double asNumber() {
	    return val;
	}

	@Override
	public void printOn(PrintWriter out) {
	    Value.printNumber(out, val);
	}

	private static final int MIN = -1, MAX = 2000;
	private static NumValue smallints[] = new NumValue[MAX-MIN+1];

	public static NumValue instance(double val) {
	    int n = (int) val;
	    if (val != n || n < MIN || n > MAX)
		return new NumValue(val);
	    else {
		if (smallints[n-MIN] == null)
		    smallints[n-MIN] = new NumValue(n);
		return smallints[n-MIN];
	    }
	}

        public static NumValue instance(int val) {
            return instance((double) val);
        }

	@Override
	public boolean equals(Object a) {
	    return (a instanceof NumValue && val == ((NumValue) a).val);
	}
	
        @Override
        public int hashCode() {
            long x = Double.doubleToLongBits(val);
            return (int) (x ^ (x >> 32));
        }

	public Value matchPlus(Value iv) {
	    double inc = ((NumValue) iv).val;
	    double x = val - inc;
	    if (inc > 0 && x >= 0 && x == (int) x)
		return NumValue.instance(x);
	    else
		return null;
	}

	@Override
        public void dump(PrintWriter out) {
            int n = (int) Math.round(val);
	    if (val == n)
		out.printf("number %d\n", n);
	    else
		throw new Error("Can't dump a non-integer");
	}
    }
    
    /** A boolean value */
    @DESCRIPTION("a boolean")
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
	public Object readResolve() { return instance(val); }
	
	@Override
	public void dump(PrintWriter out) {
	    out.printf("boolean %d\n", (val ? 1 : 0));
	}

	public static final BoolValue 
	    truth = new BoolValue(true), 
	    falsity = new BoolValue(false);
    
	public static BoolValue instance(boolean val) {
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

        public static FunValue instance(Function subr) {
            return new FunValue(subr);
        }
    }

    /** A string value */
    @DESCRIPTION("a string")
    public static class StringValue extends Value {
	private static final long serialVersionUID = 1L;

	public final String text;
	
	private StringValue(String text) {
	    this.text = text;
	}
	
        public String asString() {
            return text;
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
	private static StringValue emptyString = new StringValue("");

	/** Singletons for one-character strings */
	private static StringValue charStrings[] = new StringValue[256];

	/* The "explode" primitive can create lists of many one-character 
	 * strings, so we create shared instances in advance */
	static {
	    for (int i = 0; i < 256; i++)
		charStrings[i] = new StringValue(String.valueOf((char) i));
	}

	public static StringValue instance(char ch) {
	    if (ch < 256)
		return charStrings[ch];
            else
                return new StringValue(String.valueOf(ch));
	}

	public static StringValue instance(String text) {
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
		return instance(text);
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
    @DESCRIPTION("a list")
    public static class ConsValue extends Value {
	private static final long serialVersionUID = 1L;

	public final Value head, tail;
	
	private ConsValue(Value head, Value tail) {
	    Evaluator.countCons();
	    this.head = head;
	    this.tail = tail;
	}
	
        public static ConsValue instance(Value hd, Value tl) {
            if (! (tl instanceof ConsValue) && ! tl.equals(Value.nil)) 
                Evaluator.expect("':'", "a list");
            return new ConsValue(hd, tl);
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

    @PRIMITIVE
    @CONSTRUCTOR(ConsValue.class)
    public static class Cons extends Primitive.Prim2 {
        public Cons() { super(":"); }

	@Override
	public Value apply2(Value hd, Value tl) {
	    return Value.ConsValue.instance(hd, tl);
	}
	    
	private Value args[] = new Value[2];

	@Override
	public Value[] pattMatch(int nargs, Value obj) {
	    if (nargs != 2) Evaluator.err_patnargs(name);
	    try {
		Value.ConsValue cell = (Value.ConsValue) obj;
		args[0] = cell.tail;
		args[1] = cell.head;
		return args;
	    }
	    catch (ClassCastException ex) {
		return null;
	    }
	}
    }

    @PRIMITIVE
    public static Value head(Value x) {
	try {
	    Value.ConsValue xs = (Value.ConsValue) x;
	    return xs.head;
	}
	catch (ClassCastException ex) {
	    Evaluator.list_fail(x, "#head");
	    return null;
	}
    }
	
    @PRIMITIVE
    public static Value tail(Value x) {
	try {
	    Value.ConsValue xs = (Value.ConsValue) x;
	    return xs.tail;
	}
	catch (ClassCastException ex) {
	    Evaluator.list_fail(x, "#tail");
	    return null;
	}
    }

    @DESCRIPTION("a pair")
    public static class PairValue extends Value {
        private static final long serialVersionUID = 1L;

        @PRIMITIVE("_fst")
        public final Value fst;

        @PRIMITIVE("_snd")
        public final Value snd;

        private PairValue(Value fst, Value snd) {
            this.fst = fst; this.snd = snd;
        }

        public static PairValue instance(Value fst, Value snd) {
            Evaluator.countCons();
            return new PairValue(fst, snd);
        }

        @Override
        public void printOn(PrintWriter out) {
            out.print("_pair(");
            fst.printOn(out);
            out.print(", ");
            snd.printOn(out);
            out.print(")");
        }

        @Override
        public boolean equals(Object a) {
            if (! (a instanceof PairValue)) return false;
            PairValue apair = (PairValue) a;
            return fst.equals(apair.fst) && snd.equals(apair.snd);
        }

        @Override
        public int hashCode() {
            return 17 + 37 * fst.hashCode() + 53 * snd.hashCode();
        }
    }

    @PRIMITIVE
    @CONSTRUCTOR(PairValue.class)
    public static class Pair extends Primitive.Prim2 {
        public Pair() { super("_pair"); }

        @Override
        public Value apply2(Value fst, Value snd) {
            return PairValue.instance(fst, snd);
        }

        private Value args[] = new Value[2];

        @Override
        public Value[] pattMatch(int nargs, Value obj) {
            if (nargs != 2) Evaluator.err_patnargs(name);
            if (! (obj instanceof PairValue)) return null;
            PairValue v = (PairValue) obj;
            args[0] = v.fst;
            args[1] = v.snd;
            return args;
        }
    };

    public static class BlobValue extends Value {
        private static final long serialVersionUID = 1L;

        public final Name functor;
        public final Value args[];

        private BlobValue(Name functor, Value args[]) {
            this.functor = functor; this.args = args;
        }
        
        public static BlobValue instance(Name functor, Value args[]) {
            Evaluator.countCons();
            return new BlobValue(functor, args);
        }

        @Override
        public void printOn(PrintWriter out) {
            out.print("#");
            out.print(functor);
            out.print("(");
            if (args.length > 0) {
                args[0].printOn(out);
                for (int i = 1; i < args.length; i++) {
                    out.print(", ");
                    args[i].printOn(out);
                }
            }
            out.print(")");
        }

	@Override
	public boolean equals(Object a) {
	    if (! (a instanceof BlobValue)) return false;
	    BlobValue ablob = (BlobValue) a;
	    if (! functor.equals(ablob.functor) 
                || args.length != ablob.args.length) return false;
            for (int i = 0; i < args.length; i++)
                if (! args[i].equals(ablob.args[i])) return false;
            return true;
	}

        @Override
        public int hashCode() {
            int x = 23 + 37 * functor.hashCode();
            if (args.length > 0)
                x += 43 * args[0].hashCode();
            return x;
        }
    }
}
