/*
 * Evaluator.java
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

import java.io.PrintWriter;
import funbase.Primitive.PRIMITIVE;

/** This class provides the context for evaluating paragraphs: it imposes
 *  resource limits, and deals with errors that occur during evaluation. */
public class Evaluator {
    protected static boolean runFlag;
    private static int steps;
    private static int conses;
    private static Thread timer;

    public static int debug = 0;
    /* Debug levels:
       1 -- see ASTs before code gen
       2 -- see funcode
       3 -- see JIT statistics
       4 -- see JVM code 
       5 -- save JVM code as class files */

    public static final int QUANTUM = 10000;
    public static int quantum = QUANTUM;

    protected static int timeLimit = 30000;
    protected static int stepLimit = 500000000;
    protected static int consLimit = 100000000;

    private static class ExecThread extends Thread {
	public Value fun;
	public Value args[];
	public Value result;
	public Error error = null;

	private static int thrcount = 0;

	public ExecThread(Value fun, Value args[]) {
	    super(null, null, "exec" + thrcount++, 16*1024*1024);
	    this.fun = fun; this.args = args;
	}

	private void body() {
	    try {
		result = fun.apply(args);
		checkpoint();
	    }
	    catch (StackOverflowError e) {
		throw new EvalError("#stack");
	    }
	}

	@Override 
	public void run() {
	    try {
		body();
	    }
	    catch (Error e) {
		error = e;
	    }
	}
    }

    public static void reset() {
        quantum = QUANTUM;
        steps = conses = 0;
    }

    public static Value execute(Value fun, Value... args) {
	runFlag = true; steps = conses = 0; timer = null;
	Backtrace backtrace = FunCode.backtrace();
        backtrace.initStack();
	ExecThread exec = new ExecThread(fun, args);

	startTimer();

	try { 
	    exec.start();
	    exec.join(); 
	} 
	catch (InterruptedException e) {
	    throw new EvalError("#interrupt");
	}
	finally {
	    if (timer != null) timer.interrupt();
	}

	if (exec.error != null) throw exec.error;
	return exec.result;
    }

    private static void startTimer() {
	if (timeLimit > 0) {
	    timer = new Thread() {
		@Override 
		public void run() {
		    try {
			sleep(timeLimit);
			runFlag = false;
		    }
		    catch (InterruptedException e) { }
		}
	    };
	    timer.start();
	}
    }

    public static void checkpoint() {
	steps += (QUANTUM - quantum);
	if (stepLimit > 0 && steps > stepLimit) 
	    throw new EvalError("#steps");
	if (! runFlag) 
	    throw new EvalError("#time");
	quantum = QUANTUM;

	Thread.yield();
    }
    
    public static void countCons() { 
	conses++; 
	if (consLimit > 0 && conses > consLimit) 
	    throw new EvalError("#memory");
    }
    
    public static void setLimits(int timeLimit, int stepLimit, int consLimit) {
	Evaluator.timeLimit = timeLimit;
	Evaluator.stepLimit = stepLimit;
	Evaluator.consLimit = consLimit;
    }
    
    /** Print statistics about cost of evaluation */
    public static void printStats(PrintWriter log) {
        log.format("(%d %s, %d %s)\n", 
		   steps, (steps == 1 ? "step" : "steps"), 
		   conses, (conses == 1 ? "cons" : "conses"));
    }

    /** An exception defined by an error tag */
    public static abstract class TaggedError extends Error {
	public final String errtag;
	public final Object args[];

	public TaggedError(String errtag, Object args[]) {
	    this.errtag = errtag;
	    this.args = args;
	}

	public TaggedError(String errtag) {
	    this(errtag, null);
	}
    }

    /** An exception raised because of a run-time error */
    public static class EvalError extends TaggedError {
	public final String context;

        public EvalError(String errtag, Object args[], String context) {
            super(errtag, args);
	    this.context = context;
        }            

        public EvalError(String errtag) {
            this(errtag, null, null);
        }
    }

    public static void error(String errtag, Object... args) {
        Backtrace backtrace = FunCode.backtrace();
	String context[] = backtrace.context(null);
	throw new EvalError(errtag, args, context[0]);
    }

    /** Message "<primitive> expects a <kind> argument" */
    public static void expect(String name, String expected) {
        Backtrace backtrace = FunCode.backtrace();
	String context[] = backtrace.context(name);
	error("#expect", context[1], expected);
    }

    public static void expect(String name, Class<?> expected) {
        expect(name, Primitive.description(expected));
    }

    /** Complain about calling a non-function */
    public static void err_apply() {
	error("#apply");
    }

    /** Complain about pattern-matching with a non-constructor */
    public static void err_match() {
	error("#constr");
    }

    /** Complain when the wrong number of arguments is provided */
    public static void err_nargs(String name, int nargs, int arity) {
	if (nargs == 1)
	    error("#numargs1", name, arity);
	else
	    error("#numargs", name, nargs, arity);
    }

    /** Complain when no pattern matches in a function definition */
    public static void err_nomatch(Value args[], int base, int arity) {
	StringBuilder buf = new StringBuilder();
	if (arity > 0) {
	    buf.append(args[base+0]);
	    for (int i = 1; i < arity; i++)
		buf.append(", " + args[base+i]);
	}

	if (arity == 1)
	    error("#match1", buf);
	else
	    error("#match", buf);
    }

    // Variants of err_nomatch for calling from JIT code

    public static void err_nomatch0() {
	err_nomatch(null, 0, 0);
    }

    public static void err_nomatch1(Value x) {
	err_nomatch(new Value[] { x }, 0, 1);
    }

    public static void err_nomatch2(Value x, Value y) {
	err_nomatch(new Value[] { x, y }, 0, 2);
    }

    public static void err_nomatch3(Value x, Value y, Value z) {
	err_nomatch(new Value[] { x, y, z }, 0, 3);
    }

    public static void err_nomatch4(Value x, Value y, Value z,
				    Value u) {
	err_nomatch(new Value[] { x, y, z, u }, 0, 4);
    }

    public static void err_nomatch5(Value x, Value y, Value z,
				    Value u, Value v) {
	err_nomatch(new Value[] { x, y, z, u, v }, 0, 5);
    }

    public static void err_nomatch6(Value x, Value y, Value z,
				    Value u, Value v, Value w) {
	err_nomatch(new Value[] { x, y, z, u, v, w }, 0, 6);
    }

    /** Complain about an undefined name */
    public static void err_notdef(Name x) {
	error("#undef", x);
    }

    /** Complain about a non-boolean guard or 'if' condition */
    public static void err_boolcond() {
	error("#condbool");
    }

    /** Complain about matching against a constructor with the
     *  wrong number of argument patterns */
    public static void err_patnargs(String name) {
	error("#patnargs", name);
    }

    /** Report division by zero */
    public static void err_divzero() {
        error("#divzero");
    }

    /** Complain about failure of head or tail */
    public static void list_fail(Value xs, String msg) {
	error(msg, (xs == Value.nil ? "the empty list" : "a non-list"));
    }    

    @PRIMITIVE
    public static Value _error(Primitive prim, String tag, Value args) {
	Evaluator.error(tag, (Object []) prim.toArray(args));
	return null;
    }

    @PRIMITIVE
    public static void _limit(int time, int steps, int conses) {
	Evaluator.setLimits(time, steps, conses);
    }

    /** Interface for an agent that can trace back the execution stack. */
    public interface Backtrace {
	/** Get execution context as two strings */
	public String[] context(String me);

	/** Initialise stack */
	public void initStack();

	/** Set stack root: the last system routine before user code */
	public void setRoot(Value root);
    }
}
