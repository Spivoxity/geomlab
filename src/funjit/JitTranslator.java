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
import funbase.Function;
import funbase.Name;
import funbase.Value;
import funbase.Evaluator;
import funbase.Value.FunValue;
import funbase.Function.Closure;

import static funjit.Opcodes.*;
import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

public class JitTranslator implements FunCode.Jit {
    private String className;
    private ClassFile cf;
    protected Method code;

    /** Argument count for function being compiled */
    private int arity;

    /** Constant pool */
    protected Value consts[];

    private Label trap;

    private Map<Integer, Label> labdict = new HashMap<Integer, Label>();
    
    private Label makeLabel(int addr) {
	Label lab = labdict.get(addr);

	if (lab == null) {
	    lab = new Label();
	    labdict.put(addr, lab);
	}

	return lab;
    }

    /* Stack layout:

         0: this
	 1: arg 0	OR  1: args
	 2: arg 1	    2: base
	 ...                3: nargs
         n: arg n-1         4: temp
       n+1: temp  	    5: local 0
       n+2: local 0         6: local 1
       n+3: local 1         ...
         ...

    */

    private final static int 
	_this = 0, _args = 1, _base = 2, _nargs = 3;

    protected int _temp, _frame;
    
    private Label loop;

    private void start(FunCode funcode) {
	String parent;
	Type constype;
	className = gensym(funcode.name);
	arity = funcode.arity;
	consts = funcode.consts;
	
	if (arity <= 3) {
	    parent = jitsmall_cl + arity;
	    constype = fun_S_t;
	    _temp = _args+arity; 
	    _frame = _args+arity+1;
	} else {
	    parent = jitfun_cl;
	    constype = fun_SI_t;
	    _temp = 4; 
	    _frame = 5;
	}

	ClassFile.debug = 0;	// Don't trace boilerplate code
	cf = new ClassFile(ACC_PUBLIC + ACC_SUPER, className, parent);

	// public Gnnnn() { super(name, arity); }
	code = cf.addMethod(ACC_PUBLIC, "<init>", fun_t);
	code.gen(ALOAD, 0);
	code.gen(CONST, funcode.name);
	if (arity > 3) code.gen(CONST, arity);
	code.gen(INVOKESPECIAL, parent, "<init>", constype);
	code.gen(RETURN);

	if (arity <= 3)
	    // public Value apply<n>(Value x_1, ..., x_n)
	    code = cf.addMethod(ACC_PUBLIC, "apply" + arity, applyn_t[arity]);
	else
	    // public Value apply(Value args[], int base, int nargs)
	    code = cf.addMethod(ACC_PUBLIC, "apply", apply_t);
	
	// if (--Evaluator.quantum <= 0) Evaluator.checkpoint()
	Label lab3 = new Label();
	code.gen(GETSTATIC, evaluator_cl, "quantum", int_t);
	code.gen(CONST, 1);
	code.gen(ISUB);
	code.gen(DUP);
	code.gen(PUTSTATIC, evaluator_cl, "quantum", int_t);
	code.gen(IFGT, lab3);
	code.gen(INVOKESTATIC, evaluator_cl, "checkpoint", fun_t);
	code.label(lab3);

	if (arity > 3) {
	    // if (nargs != <arity>) 
	    //     ErrContext.err_nargs(this.name, nargs, <arity>)
	    Label lab = new Label();
	    code.gen(ILOAD, _nargs);
	    code.gen(CONST, arity);
	    code.gen(IF_ICMPEQ, lab);
	    code.gen(ALOAD, _this);
	    code.gen(GETFIELD, jitfun_cl, "name", string_t);
	    code.gen(ILOAD, _nargs);
	    code.gen(CONST, arity);
	    code.gen(INVOKESTATIC, evaluator_cl, "err_nargs", fun_SII_t);
	    code.label(lab);
	}

	if (Evaluator.debug > 3) ClassFile.debug = 1;

	loop = new Label();
	code.label(loop);
    }

    private void genArg(int n) {
	// stack[sp++] = args[base+n]
	if (arity <= 3)
	    code.gen(ALOAD, _args+n);
	else {
	    code.gen(ALOAD, _args);
	    code.gen(ILOAD, _base);
	    if (n > 0) {
		code.gen(CONST, n); code.gen(IADD);
	    }
	    code.gen(AALOAD);
	}
    }	

    private void genFVar(int n) {
	// stack[sp++] = fvars[n];
	code.gen(ALOAD, _this);
	code.gen(GETFIELD, jitfun_cl, "fvars", valarray_t);
	code.gen(CONST, n);
	code.gen(AALOAD);
    }

    private void genQuote(int n) {
	// stack[sp++] = consts[n];
	code.gen(ALOAD, _this);
	code.gen(GETFIELD, jitfun_cl, "consts", valarray_t);
	code.gen(CONST, n);
	code.gen(AALOAD);
    }

    /** Special method for QUOTE / PUTARG sequence.
     * 
     *  A hook for inlining primitives. */
    protected void genQuotePutarg(int n, int i) {
	genQuote(n);
	genPutarg(i, Kind.VALUE);
    }

    private void genCons() {
	// stack[sp] = Value.cons(stack[sp], stack[sp+1]);
	code.gen(NEW, consval_cl);
	code.gen(DUP_X2); code.gen(DUP_X2); genPop();
	code.gen(INVOKESPECIAL, consval_cl, "<init>", fun_VV_t);
    }

    /** Translate a PREP instruction.
     * 
     *  A hook for inlining primitives.
     */
    protected void genPrep(int nargs) {
	cast_function();
    	code.gen(GETFIELD, funval_cl, "subr", function_t);
    }

    /** Translate a GLOBAL / PREP sequence.
     * 
     *  A hook for inlining of primitives.
     */
    protected void genGlobalPrep(int glob, int nargs) {
	genGlobal(glob);
	genPrep(nargs);
    }

    private void genFVarPrep(int n, int nargs) {
	if (n == 0)
	    genSelf();
	else {
	    genFVar(n);
	    genPrep(nargs);
	}
    }

    /** Hook for pushing self for recursive call */
    protected void genSelf() {
	code.gen(ALOAD, _this);
    }

    /** Translate a PUTARG instruction.
     * 
     *  A hook for inlining of primitives.
     */
    protected void genPutarg(int i, Kind k) {
	convValue(k);
    }

    /** Make a Value array a[size] and fill in a[base..size) from the stack */
    private void makeArray(int base, int size) {
	// abuf = new Value[size];
	code.gen(CONST, size); code.gen(ANEWARRAY, value_cl);
	for (int i = size-1; i >= base; i--) {
	    // abuf[i] = stack[--sp];
	    code.gen(DUP_X1); code.gen(SWAP); code.gen(CONST, i);
	    code.gen(SWAP); code.gen(AASTORE);
	}
    }

    public enum Kind { VALUE, NUMBER, BOOL }

    /** Translate a CALL instruction.
     * 
     *  A hook for inlining.
     */
    protected Kind genCall(int nargs) {
	if (nargs <= 3) {
	    code.gen(INVOKEVIRTUAL, function_cl, 
		     "apply" + nargs, applyn_t[nargs]);
	} else {
	    makeArray(0, nargs);
	    code.gen(CONST, 0); code.gen(CONST, nargs);
	    code.gen(INVOKEVIRTUAL, function_cl, "apply", apply_t);
    	}

	return Kind.VALUE;
    }

    /** Convert a value on the stack from kind k to kind VALUE. */
    public void convValue(Kind k) {
	switch (k) {
	    case VALUE:
		break;
	    case NUMBER:
		code.gen(INVOKESTATIC, numval_cl, "getInstance", fun_D_V_t);
		break;
	    case BOOL:
		code.gen(INVOKESTATIC, boolval_cl, "getInstance", fun_B_V_t);
		break;
	}
    }

    /** Convert a value on the stack from kind k1 to kind k2 */
    public void convert(String name, Kind k1, Kind k2) {
	if (k1 == k2) return;
	
	// All useful conversions can go via Value
	convValue(k1); 	
	
	switch (k2) {
	    case VALUE: 
		break;
	    case NUMBER: 
		castarg(name, numval_cl, "numeric");
		code.gen(GETFIELD, numval_cl, "val", double_t); 	
		break;
	    case BOOL: 	
		castarg(name, boolval_cl, "boolean");
		code.gen(GETFIELD, boolval_cl, "val", bool_t); 
		break;
	}
    }

    private void genTCall(int nargs) {
	assert(nargs == arity);

	if (nargs <= 3) {
	    for (int n = nargs-1; n >= 0; n--)
		code.gen(ASTORE, _args+n);
	} else {
	    for (int n = nargs-1; n >= 0; n--) {
		code.gen(ALOAD, _args);
		code.gen(SWAP);
		code.gen(ILOAD, _base);
		if (n > 0) {
		    code.gen(CONST, n); code.gen(IADD);
		}
		code.gen(SWAP);
		code.gen(AASTORE);
	    }
	}

	code.gen(GOTO, loop);
    }

    private void genGlobal(int n) {
    	Label lab1 = new Label();
    	Name x = (Name) consts[n];

    	// Name x = (Name) consts[n];
	code.gen(ALOAD, _this);
	code.gen(GETFIELD, jitfun_cl, "consts", valarray_t);
	code.gen(CONST, n);
	code.gen(AALOAD);
    	code.gen(CHECKCAST, name_cl);

	if (x.isFrozen())
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

    private void genJFalse(int addr, Kind k) {
	if (k != Kind.BOOL) {
	    convValue(k);
	    cast(boolval_cl,
		 new Handler("*jfalse", "boolean") {
		    @Override
		    public void compile() {
			code.gen(INVOKESTATIC, evaluator_cl, 
				 "err_boolcond", fun_t);
		    }
		 });
	    code.gen(GETFIELD, boolval_cl, "val", bool_t);
	}
    	code.gen(IFEQ, makeLabel(addr));
    }	

    private void genFail() {
    	// ErrContext.err_nomatch(args, base, arity);
	switch (arity) {
	    case 0:
		code.gen(ACONST_NULL);
		code.gen(CONST, 0);
		code.gen(CONST, 0);
		code.gen(INVOKESTATIC, evaluator_cl, "err_nomatch", fun_AII_t);
		break;
	    case 1:
		code.gen(ALOAD, _args+0);
		code.gen(INVOKESTATIC, evaluator_cl, "err_nomatch1", fun_V_t);
		break;
	    case 2:
		code.gen(ALOAD, _args+0);
		code.gen(ALOAD, _args+1);
		code.gen(INVOKESTATIC, evaluator_cl, "err_nomatch2", fun_VV_t);
		break;
	    case 3:
		code.gen(ALOAD, _args+0);
		code.gen(ALOAD, _args+1);
		code.gen(ALOAD, _args+2);
		code.gen(INVOKESTATIC, evaluator_cl, "err_nomatch3", fun_VVV_t);
		break;
	    default:
		code.gen(ALOAD, _args);
		code.gen(ILOAD, _base);
		code.gen(CONST, arity);
		code.gen(INVOKESTATIC, evaluator_cl, "err_nomatch", fun_AII_t);
	}

    	// return null;
    	code.gen(ACONST_NULL);
    	code.gen(ARETURN);
    }

    private void genJump(int rand) {
	code.gen(GOTO, makeLabel(rand));
    }

    private void genTrap(int rand) {
	trap = makeLabel(rand);
    }

    private void genClosure(int rand) {
	makeArray(1, rand+1);
	code.gen(INVOKEVIRTUAL, value_cl, "makeClosure", fun_A_V_t);
    }

    private void genNil() {
	// stack[sp++] = Value.nil
	code.gen(GETSTATIC, value_cl, "nil", value_t);
    }

    private void genPop() {
	code.gen(POP);
    }

    private void genBind(int rand) {
	// frame[n] = stack[--sp];
	code.gen(ASTORE, _frame+rand);
    }

    private void genLocal(int rand) {
	// stack[sp++] = frame[n];
	code.gen(ALOAD, _frame+rand);
    }
    
    private void genMNil() {
    	// v = stack[--sp];
    	// if (! v.isNilValue()) goto trap;
    	code.gen(INSTANCEOF, nilval_cl);
    	code.gen(IFEQ, trap);
    }

    private void genMCons() {
    	// Value v = stack[--sp];
    	code.gen(DUP); 
    	code.gen(ASTORE, _temp);

    	// if (! (v instanceof Value.ConsValue)) goto trap
    	code.gen(INSTANCEOF, consval_cl); 
    	code.gen(IFEQ, trap);

    	// v = (Value.ConsValue) v;
    	code.gen(ALOAD, _temp); 
    	code.gen(CHECKCAST, consval_cl);
    	code.gen(DUP); 
    	// v.getTail();
    	code.gen(GETFIELD, consval_cl, "tail", value_t);
    	code.gen(SWAP);
    	// v.getHead();
    	code.gen(GETFIELD, consval_cl, "head", value_t);
    }

    private void genMPlus() {
    	// sp -= 2; temp = stack[sp].matchPlus(stack[sp+1]);
    	code.gen(INVOKEVIRTUAL, value_cl, "matchPlus", fun_V_V_t);
    	code.gen(DUP);
    	code.gen(ASTORE, _temp);

    	// if (temp == null) goto trap;
    	code.gen(IFNULL, trap);
    	
    	// stack[sp++] = temp;
    	code.gen(ALOAD, _temp);
    }

    private void genMEq() {
    	// v = stack[--sp];
    	// if (! v.equals(stack[--sp])) goto trap;
    	code.gen(INVOKEVIRTUAL, object_cl, "equals", fun_O_B_t);
    	code.gen(IFEQ, trap);
    }

    private void genMPrim(int n) {
    	// Stack: obj, cons
	cast_function();
	code.gen(GETFIELD, funval_cl, "subr", function_t);
    	code.gen(SWAP);

    	// Stack: cons.subr, obj
    	// temp = cons.subr.pattMatch(obj, n)
    	code.gen(CONST, n);
    	code.gen(INVOKEVIRTUAL, function_cl, "pattMatch", fun_VI_A_t);
    	code.gen(DUP);
    	code.gen(ASTORE, _temp);
    	
    	// if (temp == null) goto trap
    	code.gen(IFNULL, trap);
    	
    	for (int i = 0; i < n; i++) {
    	    code.gen(ALOAD, _temp); 
    	    code.gen(CONST, i); 
    	    code.gen(AALOAD);
    	}
    }

    protected void init() {
	labdict.clear(); handlers.clear();
	trap = null;
    }	

    private byte[] process(FunCode funcode) {
	init();
    	start(funcode);

    	for (int ip = 0; ip < funcode.instrs.length; ip++) {
    	    FunCode.Opcode op = funcode.instrs[ip];
    	    int rand = funcode.rands[ip];
    	    
	    Label lab = labdict.get(ip);
	    if (lab != null) code.label(lab);

	    switch (op) {
		case GLOBAL:  
		    if (labdict.get(ip+1) != null
			|| funcode.instrs[ip+1] != FunCode.Opcode.PREP)
			genGlobal(rand);
		    else {
			genGlobalPrep(rand, funcode.rands[ip+1]);
			ip++;
		    }
		    break;

		case FVAR:
		    if (labdict.get(ip+1) != null
			|| funcode.instrs[ip+1] != FunCode.Opcode.PREP)
			genFVar(rand);
		    else {
			genFVarPrep(rand, funcode.rands[ip+1]);
			ip++;
		    }
		    break;

		case CALL: {
		    Kind k = genCall(rand);

		    if (labdict.get(ip+1) != null) {
			convValue(k); break;
		    }

		    switch (funcode.instrs[ip+1]) {
			case PUTARG:
			    genPutarg(funcode.rands[ip+1], k);
			    ip++;
			    break;
			case JFALSE:
			    genJFalse(funcode.rands[ip+1], k);
			    ip++;
			    break;
			default:
			    convValue(k);
			    break;
		    }
		    break;
		}

		case QUOTE:
		    if (labdict.get(ip+1) != null
			|| funcode.instrs[ip+1] != FunCode.Opcode.PUTARG)
			genQuote(rand); 
		    else {
			genQuotePutarg(rand, funcode.rands[ip+1]);
			ip++;
		    }
		    break;
		    
		case LOCAL:   genLocal(rand); break;
		case ARG:     genArg(rand); break;
		case BIND:    genBind(rand); break;
		case POP:     genPop(); break;
		case NIL:     genNil(); break;
		case CONS:    genCons(); break;
		case CLOSURE: genClosure(rand); break;
		case TRAP:    genTrap(rand); break;
		case FAIL:    genFail(); break;
		case JFALSE:  genJFalse(rand, Kind.VALUE); break;
		case JUMP:    genJump(rand); break;
		case PREP:    genPrep(rand); break;
		case PUTARG:  genPutarg(rand, Kind.VALUE); break;
		case TCALL:   genTCall(rand); break;
		case RETURN:  // return stack[--sp];
		    code.gen(ARETURN); break;
		case MEQ:     genMEq(); break;
		case MPRIM:   genMPrim(rand); break;
		case MCONS:   genMCons(); break;
		case MNIL:    genMNil(); break;
		case MPLUS:   genMPlus(); break;
		default:
		    throw new Error("Bad opcode " + op);
	    }
	}

	compileHandlers();

	return cf.toByteArray();
    }

    protected abstract class Handler {
	public final String prim;
	public final String failure;
	protected Label label = null;

	public Handler(String prim, String failure) {
	    this.prim = prim;
	    this.failure = failure;
	}

	public abstract void compile();

	@Override
	public boolean equals(Object other) {
	    Handler that = (Handler) other;
	    return (this.getClass() == that.getClass()
		    && this.prim.equals(that.prim) 
		    && this.failure.equals(that.failure));
	}

	@Override
	public int hashCode() {
	    return 5 * prim.hashCode() + failure.hashCode();
	}
    }
	
    private Map<Handler, Handler> handlers = new HashMap<Handler, Handler>();

    private Label makeHandler(Handler handler) {
	Handler handler1 = handlers.get(handler);
	if (handler1 != null)
	    return handler1.label;
	handler.label = new Label();
	handlers.put(handler, handler);
	return handler.label;
    }

    private void compileHandlers() {
	for (Handler handler : handlers.values()) {
	    code.label(handler.label);
	    genPop();
	    handler.compile();
	    // return null;
	    code.gen(ACONST_NULL);
	    code.gen(ARETURN);
	}
    }

    private class Expect extends Handler {
	public Expect(String prim, String ty) {
	    super(prim, ty);
	}

	@Override
	public void compile() {
	    // ErrContext.expect(prim, failure);
	    code.gen(CONST, prim);
	    code.gen(CONST, failure);
	    code.gen(INVOKESTATIC, evaluator_cl, "expect", fun_SS_t);
	}
    }

    protected void castarg(String prim, String cl, String tyname) {
	cast(cl, new Expect(prim, tyname));
    }

    protected void cast_function() {
	cast(funval_cl, new Handler("*apply", "function") {
		@Override
		public void compile() {
		    code.gen(INVOKESTATIC, evaluator_cl, "err_apply", fun_t);
		}
	    });
    }

    protected void cast(String cl, Handler handler) {
	Label start = new Label(), end = new Label();
	code.tryCatchBlock(start, end, makeHandler(handler), classcast_cl);
	code.label(start);
	code.gen(CHECKCAST, cl);
	code.label(end);
    }


    private static int gcount = 0;

    private static String gensym(String name) {
	return String.format("G%04d_%s", ++gcount, name);
    }

    private static class MyClassLoader extends ClassLoader {
	private String name;

	public MyClassLoader(String name) { 
	    /* Don't fall back on the system class loader, but instead use
	       the class loader that loaded us: this is needed for Web Start */
	    super(MyClassLoader.class.getClassLoader());
	    this.name = name;
	}
	
	public Class<?> defineClass(byte[] b) {
	    // System.out.println("Defining class " + name);
	    return defineClass(name, b, 0, b.length);
	}

	@Override
	public void finalize() {
	    if (Evaluator.debug > 2)
		System.out.printf("Discarding class %s\n", name);
	}
    }

    private Map<String, FunCode> class_table =
	new WeakHashMap<String, FunCode>();

    private FunCode root = null;

    /** Translate a function body into JVM code */
    @Override
    public Function.Factory translate(FunCode funcode) {
	if (Evaluator.debug > 2) {
	    System.out.printf("JIT: %s ", funcode.name);
	    System.out.flush();
	}

	byte code[] = process(funcode);

	if (Evaluator.debug > 2)
	    System.out.printf("(%s, %d bytes)\n", className, code.length);

	if (Evaluator.debug > 4) {
	    try {
		java.io.OutputStream dump = 
		    new java.io.FileOutputStream(className + ".class");
		dump.write(code);
		dump.close();
	    }
	    catch (java.io.IOException _) { }
	}

	MyClassLoader loader = new MyClassLoader(className);
	Class<?> bodyclass = loader.defineClass(code);
	class_table.put(className, funcode);

	try {
	    JitFunction body = (JitFunction) bodyclass.newInstance();
	    body.init(funcode);
	    return body;
	}
	catch (Exception e) {
	    throw new Error(e);
	}
    }

    @Override
    public String[] getContext(String me) {
	StackTraceElement stack[] = Thread.currentThread().getStackTrace();
	String caller = null, callee = me;

	for (int i = 0; i < stack.length; i++) {
	    FunCode f = class_table.get(stack[i].getClassName());
	    if (f == null) continue;

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

    public void setRoot(Value root) {
	if (root instanceof FunValue) {
	    Function f = ((FunValue) root).subr;
	    if (f instanceof Closure)
		this.root = ((Closure) f).getCode();
	}
    }
}
