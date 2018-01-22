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
import funbase.Value;
import funbase.Value.WrongKindException;

import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

public class JitTranslator implements FunCode.Jit {
    private FunCode funcode;
    private int arity;
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

    private final static int 
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
    private Map<Integer, Label> labdict = new HashMap<>();
    
    /** Make a label attached to an address in the FunCode */
    private final Label makeLabel(int addr) {
	Label lab = labdict.get(addr);

	if (lab == null) {
	    lab = new Label();
	    labdict.put(addr, lab);
	}

	return lab;
    }

    /** Test if a FunCode address has any labels. */
    private boolean isLabelled(int addr) {
        return (labdict.get(addr) != null);
    }

    /** Dictionary of inliners for primitives */
    private Map<String, FuncRule> rulestore =
	new HashMap<String, FuncRule>();

    protected void register(Inliner rule) {
        // Ignore all but the first inliner, so hand-built inliners can
        // override automatic ones added later.
        if (rulestore.get(rule.name) == null)
            rulestore.put(rule.name, rule);
    }

    /** Stack of function rules for calls in progress. */
    private Stack<FuncRule> funstack = new Stack<FuncRule>();

    public abstract class FuncRule {
        /** Compile the code before first argument */
        public void init(int i) { }

	/** Return the preferred kind for an argument */
	public Species argkind(int i) { return Species.VALUE; }

	/** Compile code to store an argument */
	public void putArg(int i, Species k) {
	    code.widen(k);
	}

	/** Compile code for the call */
	public abstract Species call();

	/** Compile code for a call followed by JFALSE */
	public void jcall(Label lab) {
            Species k = call();
            if (k != Species.BOOL) {
                code.widen(k);
                code.access("asBoolean", fun__B_t, code.new Crash("boolcond"));
            }
            code.gen(IFEQ, lab);
        }

        /** Make a closure */
        public void closure() {
            throw new Error("closure");
        }
    }

    public abstract class Inliner extends FuncRule {
        public final String name;

        public Inliner(String name) { this.name = name; }

        public void putArg(int i, Species k) {
            convert(name, k, argkind(i));
        }
    }

    private class ArgCollector extends FuncRule {
        public final int n;

        public ArgCollector(int n) { this.n = n; }

        public Species call() { 
	    code.gen(INVOKEVIRTUAL, function_cl, "apply"+n, applyn_t[n]);
            return Species.VALUE;
        }

        public void closure() {
            code.gen(INVOKEVIRTUAL, funcode_cl, "makeClosure"+n, applyn_t[n-1]);
        }
    }

    private class BigCollector extends ArgCollector {
        public BigCollector(int n) { super(n); }
        
        public void init(int i) {
	    code.gen(CONST, n);
	    code.gen(ANEWARRAY, value_cl);
            code.gen(DUP);
            code.gen(CONST, i);
        }

        public void putArg(int i, Species k) {
	    code.widen(k);
            code.gen(AASTORE);
            if (i+1 < n) {
                code.gen(DUP);
                code.gen(CONST, i+1);
            }
        }

        public Species call() {
	    code.gen(CONST, n);
	    code.gen(INVOKEVIRTUAL, function_cl, "apply", apply_t);
            return Species.VALUE;
        }

        public void closure() {
            code.gen(INVOKEVIRTUAL, funcode_cl, "makeClosure", fun_A_V_t);
        }
    }

    private FuncRule makeCollector(int n) {
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
	if (x.glodef != null) 
	    // Value v = x.glodef;
            code.gen(GETFIELD, name_cl, "glodef", value_t);
	else {
	    code.gen(DUP);
	    code.gen(ASTORE, _temp);
	    // Value v = x.glodef;
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
    private final void trapCast(String ty) {
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

    private void init() {
	labdict.clear(); funstack.clear();
	trap = null; cache = -1;
    }	

    /** Convert a value on the stack from species k1 to species k2.  
        May fail, naming primitive |name| in the message. */
    protected void convert(String name, Species k1, Species k2) {
	if (k1 == k2) return;

        if (k1 == Species.INT && k2 == Species.NUMBER) {
            code.gen(I2D);
            return;
        }

        if (k1 == Species.NUMBER && k2 == Species.INT) {
            code.gen(INVOKESTATIC, math_cl, "round", fun_D_L_t);
            code.gen(L2I);
            return;
        }

	// All other useful conversions can go via Value.  Conversions 
        // between different kinds like NUMBER and BOOL will always fail, 
        // but we generate the failing code without complaint.

        code.widen(k1);
        code.narrow(k2, name);
    }

    /** Convert i'th argument to suit function being called. */
    private void convertArg(int i, Species k) {
	FuncRule gen = funstack.peek();
	gen.putArg(i, k);
    }

    private boolean match(int ip, FunCode.Opcode op) {
        return (funcode.code[ip] == op.ordinal() && ! isLabelled(ip));
    }

    private int translate(int ip) {
        FunCode.Opcode op = FunCode.decode[funcode.code[ip++]];
        int rand = 0;
        FuncRule c;
        Species k;

        if (op.nrands > 0) rand = funcode.code[ip++];

	switch (op) {
	    case GLOBAL:  
                if (match(ip, Opcode.PREP)) {
                    // Calling a global function
                    Name f = (Name) funcode.consts[rand];
                    Value v = f.getGlodef();

                    if (f.isFrozen() && v != null 
                        && v instanceof Value.FunValue) {
                        Function fun = ((Value.FunValue) v).subr;
                        // Calling a known function
                        if ((fun instanceof Primitive) 
                            && fun.arity == funcode.code[ip+1]) {
                            // Calling a primitive with the correct arity
                            Primitive p = (Primitive) fun;
                            FuncRule z = rulestore.get(p.name);
                            if (z != null) {
                                // Calling a primitive we know how to inline
                                funstack.push(z);
                                return ip+2;
                            }
                        }
                    }
                }

                getGlobal(rand); 
                break;

	    case LOCAL:   
                code.gen(ALOAD, nextcache = _frame+rand);
                break;

	    case BIND:    
                code.gen(ASTORE, _frame+rand); 
                break;

            case PUSH:
                code.gen(CONST, rand);
                if (match(ip, Opcode.PUTARG)) {
                    c = funstack.peek();
                    c.putArg(funcode.code[ip+1], Species.INT);
                    return ip+2;
                }

                code.widen(Species.INT);
                break;

	    case QUOTE:   
                getSlot("consts", rand); 
                break;

	    case FVAR:    
                if (rand == 0 && match(ip, Opcode.PREP)) {
                    // A recursive call
                    code.gen(ALOAD, _this);
                    c = makeCollector(funcode.code[ip+1]);
                    c.init(0);
                    funstack.push(c);
                    return ip+2;
                }

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
                code.gen(INVOKESTATIC, consval_cl, "instance", fun_VV_C_t);
                break;

	    case CLOSURE: 
                c = funstack.pop();
                c.closure();
                break;

	    case TRAP:    
                nextcache = cache; 
                trap = makeLabel(rand); 
                break;

	    case FAIL:    
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
                funstack.push(c);
                break;

	    case PUTARG:  
                convertArg(rand, Species.VALUE);
                break;

            case FRAME:	  
                code.gen(CHECKCAST, funcode_cl);
                c = makeCollector(rand);
                c.init(1);
                funstack.push(c);
                break;

	    case CALL:    
                c = funstack.pop();

                if (match(ip, Opcode.JFALSE)) {
                    // Jump on result of call
                    c.jcall(makeLabel(funcode.code[ip+1]));
                    return ip+2;
                }

                if (match(ip, Opcode.PUTARG)) {
                    // One call as an argument to another
                    k = c.call();
                    convertArg(funcode.code[ip+1], k);
                    return ip+2;
                }

                k = c.call();
                code.widen(k);
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

        return ip;
    }

    private void process(FunCode funcode) {
	init();
    	start(funcode);

    	for (int ip = 0; ip < funcode.code.length; ) {
            // Place any label for this address
	    Label lab = labdict.get(ip);
	    if (lab != null) {
		code.label(lab);
		cache = -1;
	    }

	    nextcache = -1;
            ip = translate(ip);
	    cache = nextcache;
	}

	code.finish();
    }

    /** Translate a function body into JVM code */
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

    protected Primitive.Factory makePrimitiveFactory() {
        return new JitPrimFactory();
    }

    public Primitive.Factory primitiveFactory() {
        // Construct the factory lazily to avoid calls to overridden
        // methods from the constructor.
        if (factory == null)
            factory = makePrimitiveFactory();

        return factory;
    }

    private StackTracer backtrace = new StackTracer();

    public Evaluator.Backtrace backtrace() {
        return backtrace;
    }
}
