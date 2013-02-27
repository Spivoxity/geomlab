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

    public static final int Q = 10000;
    public static int quantum = Q;

    protected static int timeLimit = 30000;
    protected static int stepLimit = 0; // 2000000000;
    protected static int consLimit = 10000000;

    private static class ExecThread extends Thread {
	public Function fun;
	public Value args[];
	public Value result;
	public RuntimeException excep = null;

	private static int thrcount = 0;

	public ExecThread(Function fun, Value args[]) {
	    super(null, null, "exec" + thrcount++, 16*1024*1024);
	    this.fun = fun; this.args = args;
	}

	private void body() {
	    try {
		result = fun.apply(args, 0, args.length);
		checkpoint();
	    }
	    catch (StackOverflowError e) {
		throw new EvalException("recursion went too deep", "#stack");
	    }
	}

	public void run() {
	    try {
		body();
	    }
	    catch (RuntimeException e) {
		excep = e;
	    }
	}
    }

    public static Value execute(Function fun, Value... args) {
	runFlag = true; steps = conses = 0; timer = null;
	FunCode.initStack();
	
	ExecThread exec = new ExecThread(fun, args);

	try { 
	    exec.start();
	    exec.join(); 
	} 
	catch (InterruptedException e) {
	    throw new EvalException("Interrupted!");
	}
	finally {
	    if (timer != null) timer.interrupt();
	}

	if (exec.excep != null) throw exec.excep;
	return exec.result;
    }

    public static void startTimer() {
	if (timeLimit > 0) {
	    timer = new Thread() {
		@Override
		public synchronized void run() {
		    try {
			wait(timeLimit);
			runFlag = false;
		    }
		    catch (InterruptedException e) { }
		}
	    };
	    timer.start();
	}
    }

    public static void checkpoint() {
	steps += (Q - quantum);
	if (stepLimit > 0 && steps > stepLimit) timeout("many steps");
	if (! runFlag) timeout("long");
	quantum = Q;
	Thread.yield();
    }
    
    private static void timeout(String resource) {
	throw new EvalException("sorry, that took too " + resource, "#time");
    }
    
    public static void countCons() { 
	conses++; 
	if (consLimit > 0 && conses > consLimit) timeout("much memory");
    }
    
    public static void setLimits(int timeLimit, int stepLimit, int consLimit) {
	Evaluator.timeLimit = timeLimit;
	Evaluator.stepLimit = stepLimit;
	Evaluator.consLimit = consLimit;
    }
    
    public static void printStats(PrintWriter log) {
        log.format("(%d %s, %d %s)\n", 
		   steps, (steps == 1 ? "step" : "steps"), 
		   conses, (conses == 1 ? "cons" : "conses"));
    }

    /** An exception raised because of a run-time error */
    public static class EvalException extends RuntimeException {
	private String errtag;
	
	public EvalException(String message, String errtag) {
	    super(message);
	    this.errtag = errtag;
	}

	public EvalException(String message) {
	    this(message, "#nohelp");
	}
	
	public String getErrtag() { return errtag; }
    }

    private static String format(String msg, String name) {
	if (name != null)
	    return msg + " in function '" + name + "'";
	else
	    return msg;
    }

    private static String message(String msg) {
	String context[] = FunCode.getContext(null);
	return format(msg, context[0]);
    }

    public static void error(String msg) {
	throw new EvalException(message(msg));
    }

    public static void error(String msg, String help) {
	throw new EvalException(message(msg), help);
    }

    public static void expect(String name, String expected) {
	String context[] = FunCode.getContext(name);
	String vowels = "aeiou";
	String a = (vowels.indexOf(expected.charAt(0)) >= 0 ? "an" : "a");
	throw new EvalException
	    (format("'" + context[1] + "' expects " + a + " "
		    + expected + " argument", context[0]), "#type");
    }

    /** Complain about calling a non-function */
    public static void err_apply() {
	error("applying a non-function", "#apply");
    }

    /** Complain about pattern-matching with a non-constructor */
    public static void err_match() {
	error("matching must use a constructor", "#constr");
    }

    /** Complain when the wrong number of arguments are provided */
    public static void err_nargs(String name, int nargs, int arity) {
	error("function " + name + " called with " + nargs
	      + (nargs == 1 ? " argument" : " arguments")
	      + " but needs " + arity, "#numargs");
    }

    /** Complain when no pattern matches in a function definition */
    public static void err_nomatch(Value args[], int base, int arity) {
	StringBuilder buf = new StringBuilder();
	if (arity > 0) {
	    buf.append(args[base+0]);
	    for (int i = 1; i < arity; i++)
		buf.append(", " + args[base+i]);
	}

	error("no pattern matches "
	      + (arity == 1 ? "argument" : "arguments")
	      + " (" + buf + ")", "#match");
    }

    public static void err_nomatch1(Value arg1) {
	err_nomatch(new Value[] { arg1 }, 0, 1);
    }

    public static void err_nomatch2(Value arg1, Value arg2) {
	err_nomatch(new Value[] { arg1, arg2 }, 0, 2);
    }

    public static void err_nomatch3(Value arg1, Value arg2, Value arg3) {
	err_nomatch(new Value[] { arg1, arg2, arg3 }, 0, 3);
    }

    /** Complain about an undefined name */
    public static void err_notdef(Name x) {
	error("'" + x + "' is not defined", "#undef");
    }

    /** Complain about a non-boolean guard or 'if' condition */
    public static void err_boolcond() {
	error("boolean expected as condition", "#condbool");
    }

    /** Complain about matching against a constructor with the
     *  wrong number of argument patterns */
    public static void err_patnargs(String name) {
	error("matching constructor '" + name 
	      + "' with wrong number of arguments", "#patnargs");
    }

    public static void list_fail(Value xs, String msg) {
	error("taking " + msg + " of " 
	      + (xs instanceof Value.NilValue ? "the empty list" 
		 : "a non-list"),
	      "#" + msg);
    }    
}
