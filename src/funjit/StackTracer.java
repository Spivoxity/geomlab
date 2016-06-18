/*
 * StackTracer.java
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

package funjit;

import java.util.*;

import java.lang.ref.WeakReference;

import funbase.FunCode;
import funbase.Evaluator;
import funbase.Value;
import funbase.Function;
import funbase.Value.FunValue;
import funbase.Function.Closure;

public class StackTracer implements Evaluator.Backtrace {
    /** Table for interpreting stack traces */
    private Map<String, WeakReference<FunCode>> classTable = 
	new HashMap<String, WeakReference<FunCode>>();
        /* The table will fill up with junk over time, but the weak references
	   will at least prevent retention of stale FunCode objects */

    private FunCode root = null;

    public void put(String name, FunCode code) {
	classTable.put(name, new WeakReference<FunCode>(code));
    }

    @Override 
    public String[] getContext(String me) {
	Thread thread = Thread.currentThread();
	StackTraceElement stack[] = thread.getStackTrace();
	String caller = null, callee = me;

	for (int i = 0; i < stack.length; i++) {
	    WeakReference<FunCode> fr = 
		classTable.get(stack[i].getClassName());
	    if (fr == null) continue;
	    FunCode f = fr.get();
	    if (f == null) 
                throw new Error("stack map entry disappeared");

	    if (f == root) break;

	    if (f.frozen) 
		callee = f.name;
	    else {
		caller = f.name;
		break;
	    }
	}

	return new String[] { caller, callee };
    }

    @Override
    public void initStack() { }

    @Override
    public void setRoot(Value root) {
	if (root instanceof FunValue) {
	    Function f = ((FunValue) root).subr;
	    if (f instanceof Closure)
		this.root = ((Closure) f).getCode();
	}
    }
}
