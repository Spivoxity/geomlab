/*
 * RuleTranslator.java
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

import funbase.FunCode.Opcode;

import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

/** A translator that can use rules to optimise special cases */
public class RuleTranslator extends JitTranslator {
    // The normal code generation process can be overridden by a
    // dynamic mechanism that uses a collection of `hooks', each
    // matching a fixed sequence of opcodes.  Each hook returns true
    // to indicate that it has compiled code for the sequence, or
    // false if other hooks or the default implementation of the
    // instructions should be tried.  (The default implementations
    // themselves could be expressed as rules, at the expense of a lot
    // of boilerplate.)

    public RuleTranslator() {
	/* Treat FVAR 0 / PREP nargs specially */
	addHook(new CodeHook(Opcode.FVAR, Opcode.PREP) {
	    @Override
            public boolean compile(int rands[], int ip) {
		if (rands[ip] == 0) {
                    code.gen(ALOAD, _this);
                    ArgCollector c = makeCollector(rands[ip+1]);
                    c.init(0);
                    nstack.push(c);
                    return true;
                }
                return false;
	    }
	});
    }

    /** A rule that matches a sequence of opcodes */
    public abstract class CodeHook {
	/** The opcode sequence that the rule matches */
	private final Opcode pattern[];

	public CodeHook(Opcode... pattern) { 
            this.pattern = pattern; 
        }

	/** Compile code for the sequence and return true if successful */
	public abstract boolean compile(int rands[], int ip);

	/** Check if the pattern matches and if so invoke compile. */
	public int fire(int ip) {
	    if (funcode.instrs[ip] != pattern[0])
		return 0;

	    for (int i = 1; i < pattern.length; i++) {
		if (isLabelled(ip+i) || funcode.instrs[ip+i] != pattern[i])
		    return 0;
	    }

            if (compile(funcode.rands, ip)) 
                return pattern.length;

            return 0;
	}
    }

    /** A table giving for each opcode the rules that start with it */
    private EnumMap<Opcode, List<CodeHook>> hooks = 
	new EnumMap<Opcode, List<CodeHook>>(Opcode.class);

    protected final void addHook(CodeHook hook) {
	Opcode op = hook.pattern[0];
	List<CodeHook> list = hooks.get(op);
	if (list == null) {
	    list = new LinkedList<CodeHook>(); hooks.put(op, list);
	}
	list.add(0, hook);
    }

    @Override
    protected int translate(int ip) {
        Opcode op = funcode.instrs[ip];

        // Try to compile using specialised hooks
        List<CodeHook> hooklist = hooks.get(op);
        int done = 0;
        if (hooklist != null) {
            for (CodeHook hook: hooklist) {
                done = hook.fire(ip);
                if (done > 0) break;
            }
        }

        if (done > 0) return done;

        return super.translate(ip);
    }
}
