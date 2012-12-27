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
    protected boolean runFlag = true;
    private int steps = 0;
    private int conses = 0;

    public static int debug = 0;
    /* Debug levels:
       1 -- see ASTs before code gen
       2 -- see funcode
       3 -- see JIT statistics
       4 -- see JVM code 
       5 -- save JVM code as class files */

    public static int quantum = 100;

    protected static int timeLimit = 30000;
    protected static int stepLimit = 1000000000;
    protected static int consLimit = 10000000;

    private static ThreadLocal<Evaluator> threadState = 
	new ThreadLocal<Evaluator>();

    public static Value execute(Value fun, Value... args) {
	final Evaluator ev = new Evaluator();
	threadState.set(ev);

	Value result = null;
	Thread timer = null;
	
	if (timeLimit > 0) {
	    timer = new Thread() {
		@Override
		public synchronized void run() {
		    try {
			wait(timeLimit);
			ev.runFlag = false;
		    }
		    catch (InterruptedException e) { /* finish */ }
		}
	    };
	    timer.start();
	}
	
	try {
	    result = fun.apply(args, ErrContext.nullContext);
	    checkpoint();
	}
	catch (StackOverflowError e) {
	    throw new EvalException("recursion went too deep", "#stack");
	}
	finally {
	    if (timer != null) timer.interrupt();
	}

	return result;
    }

    public static void checkpoint() {
	Evaluator ev = threadState.get();
	if (ev == null) return;
	ev.steps += (100 - quantum);
	// if (! ev.runFlag) timeout("long");
	if (stepLimit > 0 && ev.steps > stepLimit)
	    timeout("many steps");
	quantum = 100;
	Thread.yield();
    }
    
    private static void timeout(String resource) {
	throw new EvalException("sorry, that took too " + resource, "#time");
    }
    
    public static void countCons() { 
	Evaluator ev = threadState.get();
	if (ev == null) return;
	ev.conses++; 
	if (consLimit > 0 && ev.conses > consLimit) 
	    timeout("much memory");
    }
    
    public static void setLimits(int timeLimit, int stepLimit, int consLimit) {
	Evaluator.timeLimit = timeLimit;
	Evaluator.stepLimit = stepLimit;
	Evaluator.consLimit = consLimit;
    }
    
    public static Value apply(Value fun, Value args[]) {
	return fun.apply(args, ErrContext.initContext);
    }

    public static void printStats(PrintWriter log) {
	Evaluator ev = threadState.get();
        log.format("(%d %s, %d %s)\n", 
		   ev.steps, (ev.steps == 1 ? "step" : "steps"), 
		   ev.conses, (ev.conses == 1 ? "cons" : "conses"));
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
}
