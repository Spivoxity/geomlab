/*
 * JitTranslator.java
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

import funbase.FunCode;
import funbase.FunCode.Opcode;
import funbase.Function;
import funbase.Primitive;
import funbase.Evaluator;
import funbase.Name;

import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

public class JitTranslator implements FunCode.Jit {
    protected FunCode funcode;
    protected int arity;
    protected FunctionClass code;

    /* Stack layout:

       arity <= 6           arity >= 7
       ----------           ----------
         0: this        OR  0: this
	 1: arg 0	    1: args
	 2: arg 1	    2: temp
	 ...                3: local 0
         n: arg n-1         4: local 1
       n+1: temp  	    5: 
       n+2: local 0         ...
       n+3: local 1         
         ...                              */

    public final static int MANY = 7;

    protected final static int 
	_this = 0, _args = 1;

    protected int _temp, _frame;
    
    private int cache, nextcache;

    private Label loop;
    private Label trap;

    private void start(FunCode funcode) {
	this.funcode = funcode;
	this.arity = funcode.arity;

	ClassFile.debug = 0;	// Don't trace boilerplate code
        code = new FunctionClass(funcode.name, funcode.arity,
                                 jitsmall_cl, jitlarge_cl);
	if (Evaluator.debug > 3) ClassFile.debug = 1;

        _temp = (arity < MANY ? arity+1 : 2); 
        _frame = _temp+1;

	loop = new Label();
	code.label(loop);
	code.checkpoint();
    }

    /** Table mapping addresses in the funcode to labels in the JVM code */
    private Map<Integer, Label> labdict = 
        new HashMap<Integer, Label>();
    
    protected final Label makeLabel(int addr) {
	Label lab = labdict.get(addr);

	if (lab == null) {
	    lab = new Label();
	    labdict.put(addr, lab);
	}

	return lab;
    }

    protected boolean isLabelled(int addr) {
        return (labdict.get(addr) != null);
    }

    /** Stack of argument collectors for calls in progress. */
    protected Stack<ArgCollector> nstack = new Stack<>();

    protected class ArgCollector {
        public final int n;

        public ArgCollector(int n) { this.n = n; }

        public void init(int i) { }

        public void arg(int i) { }

        public void call() { 
	    code.gen(INVOKEVIRTUAL, function_cl, "apply"+n, applyn_t[n]);
        }

        public void closure() {
            code.gen(INVOKEVIRTUAL, funcode_cl, "makeClosure"+n, applyn_t[n-1]);
        }
    }

    protected class BigCollector extends ArgCollector {
        public BigCollector(int n) { super(n); }
        
        public void init(int i) {
	    code.gen(CONST, n);
	    code.gen(ANEWARRAY, value_cl);
            code.gen(DUP);
            code.gen(CONST, i);
        }

        public void arg(int i) {
            code.gen(AASTORE);
            if (i+1 < n) {
                code.gen(DUP);
                code.gen(CONST, i+1);
            }
        }

        public void call() {
	    code.gen(CONST, n);
	    code.gen(INVOKEVIRTUAL, function_cl, "apply", apply_t);
        }

        public void closure() {
            code.gen(INVOKEVIRTUAL, funcode_cl, "makeClosure", fun_A_V_t);
        }
    }

    protected ArgCollector makeCollector(int n) {
        if (n < MANY)
            return new ArgCollector(n);
        else
            return new BigCollector(n);
    }

    /** Fetch from consts or fvars */
    private void getSlot(String name, int index) {
	code.gen(ALOAD, _this);
	code.gen(GETFIELD, jitfun_cl, name, valarray_t);
	code.gen(CONST, index);
	code.gen(AALOAD);
    }

    /** Translate a GLOBAL instruction */
    private void getGlobal(int n) {
    	Label lab1 = new Label();
    	Name x = (Name) funcode.consts[n];

    	// Name x = (Name) consts[n];
	getSlot("consts", n);
    	code.gen(CHECKCAST, name_cl);

	// We assume that a global name defined now will not become undefined
	if (x.getGlodef() != null) 
	    // Value v = x.glodef;
            // code.gen(INVOKEVIRTUAL, name_cl, "getGlodef", fun__V_t);
            code.gen(GETFIELD, name_cl, "glodef", value_t);
	else {
	    code.gen(DUP);
	    code.gen(ASTORE, _temp);
	    // Value v = x.glodef;
            // code.gen(INVOKEVIRTUAL, name_cl, "getGlodef", fun__V_t);
	    code.gen(GETFIELD, name_cl, "glodef", value_t);
    	    code.gen(DUP);		
    	    // if (v == null) ErrContext.err_notdef(x);
    	    code.gen(IFNONNULL, lab1);
    	    code.gen(ALOAD, _temp);
    	    code.gen(INVOKESTATIC, evaluator_cl, "err_notdef", fun_N_t);
    	    code.label(lab1);
    	}
    }

    /** Cast value on stack and goto trap on failure: uses top cache */
    protected final void trapCast(String ty) {
	if (cache < 0) {
	    code.gen(DUP);
	    code.gen(ASTORE, _temp);
	    cache = _temp;
	}
	code.gen(INSTANCEOF, ty);
	code.gen(IFEQ, trap);
	code.gen(ALOAD, cache);
	code.gen(CHECKCAST, ty);
    }

    /** Default translation of each opcode, if not overridden by rules */
    protected void translate(Opcode op, int rand) {
        ArgCollector c;

	switch (op) {
	    case GLOBAL:  
                getGlobal(rand); 
                break;

	    case LOCAL:   
                code.gen(ALOAD, nextcache = _frame+rand);
                break;

	    case BIND:    
                code.gen(ASTORE, _frame+rand); 
                break;

	    case QUOTE:   
                getSlot("consts", rand); 
                break;

	    case FVAR:    
                getSlot("fvars", rand); 
                break;

	    case ARG:     
                if (arity < MANY)
                    code.gen(ALOAD, nextcache = _args+rand);
                else {
                    code.gen(ALOAD, _args);
                    code.gen(CONST, rand);
                    code.gen(AALOAD);
                }
                break;

	    case POP:     
                code.gen(POP); 
                break;

	    case RETURN:  
                code.gen(ARETURN); 
                break;

	    case NIL:     
                code.gen(GETSTATIC, value_cl, "nil", value_t); 
                break;

	    case CONS:    
                code.gen(INVOKESTATIC, consval_cl, "getInstance", fun_VV_C_t);
                break;

	    case CLOSURE: 
                c = nstack.pop();
                c.closure();
                break;

	    case TRAP:    
                nextcache = cache; 
                trap = makeLabel(rand); 
                break;

	    case FAIL:    
                // ErrContext.err_nomatch(args, 0, arity);
                if (arity < MANY) {
                    for (int i = 0; i < arity; i++)
                        code.gen(ALOAD, _args+i);
                    code.gen(INVOKESTATIC, evaluator_cl, 
                             "err_nomatch"+arity, failn_t[arity]);
                }
                else {
                    code.gen(ALOAD, _args);
                    code.gen(CONST, 0);
                    code.gen(CONST, arity);
                    code.gen(INVOKESTATIC, evaluator_cl, 
                             "err_nomatch", fun_AII_t);
                }

                // return null;
                code.gen(ACONST_NULL);
                code.gen(ARETURN);
                break;

	    case JFALSE:  
                code.access("asBoolean", fun__B_t, code.new Crash("boolcond"));
                code.gen(IFEQ, makeLabel(rand));
                break;

	    case JUMP:    
                code.gen(GOTO, makeLabel(rand)); 
                break;

	    case PREP:    
                code.gen(GETFIELD, value_cl, "subr", function_t);
                c = makeCollector(rand);
                c.init(0);
                nstack.push(c);
                break;

	    case PUTARG:  
                c = nstack.peek();
                c.arg(rand);
                break;

            case FRAME:	  
                code.gen(CHECKCAST, funcode_cl);
                c = makeCollector(rand);
                c.init(1);
                nstack.push(c);
                break;

	    case CALL:    
                c = nstack.pop();
                c.call();
                break;

	    case TCALL:   
                assert(rand == arity);
                for (int i = rand-1; i >= 0; i--) {
                    if (rand < MANY)
                        code.gen(ASTORE, _args+i);
                    else {
                        // args[i] = stack[--sp];
                        code.gen(ALOAD, _args); // array, v_i, v_i+1, ...
                        code.gen(SWAP); 	// v_i, array, v_i+1, ...
                        code.gen(CONST, i);	// i, v_i, array, v_i+1, ...
                        code.gen(SWAP); 	// v_i, i, array, v_i+1, ...
                        code.gen(AASTORE);	// v_i+1, ...
                    }
                }
                code.gen(GOTO, loop);
                break;

	    case MEQ:     
                code.gen(INVOKEVIRTUAL, object_cl, "equals", fun_O_B_t);
                code.gen(IFEQ, trap);
                break;

	    case MPRIM:   
                // temp = cons.subr.pattMatch(n, obj)
                code.gen(GETFIELD, value_cl, "subr", function_t);
                code.gen(SWAP);				// subr, obj
                code.gen(CONST, rand);			
                code.gen(SWAP);				// subr, n, obj
                code.gen(INVOKEVIRTUAL, function_cl, "pattMatch", fun_IV_A_t);
                code.gen(DUP);				
                code.gen(ASTORE, _temp);

                // if (temp == null) goto trap
                code.gen(IFNULL, trap);

                // Push arguments
                for (int i = 0; i < rand; i++) {
                    code.gen(ALOAD, _temp);
                    code.gen(CONST, i);
                    code.gen(AALOAD);
                }
                break;

	    case MCONS:
                trapCast(consval_cl);
                code.gen(DUP); 
                code.gen(GETFIELD, consval_cl, "head", value_t);
                break;

            case MPAIR:
                trapCast(pairval_cl);
                code.gen(DUP); 
                code.gen(GETFIELD, pairval_cl, "fst", value_t);
                break;

	    case GETTAIL: 
                code.gen(GETFIELD, consval_cl, "tail", value_t);
                break;

            case GETSND:  
                code.gen(GETFIELD, pairval_cl, "snd", value_t);
                break;

	    case MNIL:    
                code.gen(INSTANCEOF, nilval_cl);
                code.gen(IFEQ, trap);
                break;

	    case MPLUS:   
                trapCast(numval_cl);
                getSlot("consts", rand);
                code.gen(INVOKEVIRTUAL, numval_cl, "matchPlus", fun_V_V_t);
                code.gen(DUP);
                code.gen(ASTORE, _temp);
                code.gen(IFNULL, trap);
                code.gen(ALOAD, nextcache = _temp);
                break;

	    default:
		throw new Error("Bad opcode " + op);
	}
    }

    protected void init() {
	labdict.clear(); nstack.clear();
	trap = null; cache = -1;
    }	

    protected int translate(int ip) {
        FunCode.Opcode op = funcode.instrs[ip];
        int rand = funcode.rands[ip];
        translate(op, rand);
        return 1;
    }

    private void process(FunCode funcode) {
	init();
    	start(funcode);

    	for (int ip = 0; ip < funcode.instrs.length; ) {
	    Label lab = labdict.get(ip);
	    if (lab != null) {
		code.label(lab);
		cache = -1;
	    }
	    nextcache = -1;
            ip += translate(ip);
	    cache = nextcache;
	}

	code.finish();
    }

    /** Translate a function body into JVM code */
    @Override 
    public Function.Factory translate(FunCode funcode) {
	if (Evaluator.debug > 2) {
	    System.out.printf("JIT: %s ", funcode.name);
	    System.out.flush();
	}

	process(funcode);

	byte binary[] = code.toByteArray();

	if (Evaluator.debug > 2)
	    System.out.printf("(%s, %d bytes)\n", code.name, binary.length);

	backtrace.put(code.name, funcode);
	JitFunction body = 
	    (JitFunction) ByteClassLoader.instantiate(code.name, binary);
	body.init(funcode);
	return body;
    }

    private Primitive.Factory factory = null;

    // May be overridden in subclasses
    protected Primitive.Factory makePrimitiveFactory() {
        return new JitPrimFactory();
    }

    public Primitive.Factory getPrimitiveFactory() {
        // Construct the factory lazily to avoid calls to overridden
        // methods from the constructor.
        if (factory == null)
            factory = makePrimitiveFactory();

        return factory;
    }

    private StackTracer backtrace = new StackTracer();

    public Evaluator.Backtrace getBacktrace() {
        return backtrace;
    }
}
