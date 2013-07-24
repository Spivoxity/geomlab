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

import java.lang.ref.WeakReference;

import funbase.FunCode;
import funbase.Function;
import funbase.Name;
import funbase.Value;
import funbase.Evaluator;
import funbase.Value.FunValue;
import funbase.Function.Closure;
import funbase.FunCode.Opcode;

import static funjit.Opcodes.*;
import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

public class JitTranslator implements FunCode.Jit {
    protected FunCode funcode;

    private String className;
    private ClassFile clfile;
    protected Method code;

    /* Stack layout:

         0: this
	 1: arg 0	OR  1: args
	 2: arg 1	    2: nargs
	 ...                3: temp
         n: arg n-1         4: local 0
       n+1: temp  	    5: local 1
       n+2: local 0         ...
       n+3: local 1         
         ...                              */

    private final static int MANY = 7;

    private final static int 
	_this = 0, _args = 1, _nargs = 2;

    protected int _temp, _frame;
    
    private int cache, nextcache;

    /** Stack of function arities to help with assembling arguments */
    private Stack<Integer> nstack = new Stack<Integer>();

    private Label loop;
    private Label trap;

    private Map<Integer, Label> labdict = new HashMap<Integer, Label>();
    
    protected final Label makeLabel(int addr) {
	Label lab = labdict.get(addr);

	if (lab == null) {
	    lab = new Label();
	    labdict.put(addr, lab);
	}

	return lab;
    }

    private void start(FunCode funcode) {
	this.funcode = funcode;
	className = gensym(funcode.name);
	
	int arity = funcode.arity;

	ClassFile.debug = 0;	// Don't trace boilerplate code

	if (arity < MANY) {
	    // class Gnnnn extends JitFunction<n> {
	    clfile = new ClassFile(ACC_PUBLIC+ACC_SUPER, className, 
				   jitsmall_cl+arity);

	    // public Gnnnn { super(name); }
	    code = clfile.addMethod(ACC_PUBLIC, "<init>", fun_t);
	    code.gen(ALOAD, 0);
	    code.gen(CONST, funcode.name);
	    code.gen(INVOKESPECIAL, jitsmall_cl+arity, "<init>", fun_S_t);
	    code.gen(RETURN);

	    // public Value apply<n>(Value x_1, ..., x_n) {
	    code = clfile.addMethod(ACC_PUBLIC, "apply"+arity, 
				    applyn_t[arity]);
	    checkpoint();

	    _temp = arity+1; 
	    _frame = arity+2;
	} else {
	    // class Gnnnn extends JitFunction {
	    clfile = new ClassFile(ACC_PUBLIC+ACC_SUPER, className, 
				   jitlarge_cl);

	    // public Gnnnn { super(name, arity); }
	    code = clfile.addMethod(ACC_PUBLIC, "<init>", fun_t);
	    code.gen(ALOAD, 0);
	    code.gen(CONST, funcode.name);
	    code.gen(CONST, arity);
	    code.gen(INVOKESPECIAL, jitlarge_cl, "<init>", fun_SI_t);
	    code.gen(RETURN);

	    // public Value apply(Value args[], int nargs)
	    code = clfile.addMethod(ACC_PUBLIC, "apply", apply_t);
	    checkpoint();

	    // if (nargs != arity) 
	    //     ErrContext.err_nargs(this.name, nargs, arity)
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

	    _temp = 3; 
	    _frame = 4;
	}

	if (Evaluator.debug > 3) ClassFile.debug = 1;

	loop = new Label();
	code.label(loop);
    }

    private void checkpoint() {
	// if (--Evaluator.quantum <= 0) Evaluator.checkpoint()
	Label lab = new Label();
	code.gen(GETSTATIC, evaluator_cl, "quantum", int_t);
	code.gen(CONST, 1);
	code.gen(ISUB);
	code.gen(DUP);
	code.gen(PUTSTATIC, evaluator_cl, "quantum", int_t);
	code.gen(IFGT, lab);
	code.gen(INVOKESTATIC, evaluator_cl, "checkpoint", fun_t);
	code.label(lab);
    }

    private void pushLocal(int n) {
	code.gen(ALOAD, n);
	nextcache = n;
    }

    private void genArg(int n) {
	// stack[sp++] = args[n]
	if (funcode.arity < MANY)
	    pushLocal(_args+n);
	else {
	    code.gen(ALOAD, _args);
	    code.gen(CONST, n);
	    code.gen(AALOAD);
	}
    }

    private void genField(String name, int index) {
	code.gen(ALOAD, _this);
	code.gen(GETFIELD, jitfun_cl, name, valarray_t);
	code.gen(CONST, index);
	code.gen(AALOAD);
    }

    private void genPrecons() {
	code.gen(NEW, consval_cl);
	code.gen(DUP);
    }

    private void genCons() {
	code.gen(INVOKESPECIAL, consval_cl, "<init>", fun_VV_t);
    }

    /** Translate a PREP instruction. */
    private void genPrep(int n) {
    	code.gen(GETFIELD, value_cl, "subr", function_t);
	prepArgs(n);
    }

    private void prepArgs(int n) {
	nstack.push(n);
	if (n >= MANY) {
	    code.gen(CONST, n);
	    code.gen(ANEWARRAY, value_cl);
	    code.gen(DUP);
	    code.gen(CONST, 0);
	}
    }

    /** Translate a PUTARG instruction. */
    private void genPutarg(int i) {
	int n = nstack.peek();
	if (n < MANY) return;

	code.gen(AASTORE);
	if (i+1 < n) {
	    code.gen(DUP);
	    code.gen(CONST, i+1);
	}
    }

    /** Translate a CALL instruction. */
    protected final void genCall(int n) {
	nstack.pop();
	if (n < MANY)
	    code.gen(INVOKEVIRTUAL, function_cl, "apply"+n, applyn_t[n]);
	else {
	    code.gen(CONST, n);
	    code.gen(INVOKEVIRTUAL, function_cl, "apply", apply_t);
    	}
    }

    /** Translate a TCALL instruction into a jump */
    private void genTCall(int n) {
	// No PREP or PUTARG instructions are generated for a tail call.
	assert(n == funcode.arity);

	for (int i = n-1; i >= 0; i--) {
	    if (n < MANY)
		code.gen(ASTORE, _args+i);
	    else {
		// args[i] = stack[--sp];
		code.gen(ALOAD, _args);		// array, v_i, v_i+1, ...
		code.gen(SWAP); 		// v_i, array, v_i+1, ...
		code.gen(CONST, i);		// i, v_i, array, v_i+1, ...
		code.gen(SWAP); 		// v_i, i, array, v_i+1, ...
		code.gen(AASTORE);		// v_i+1, ...
	    }
	}

	code.gen(GOTO, loop);
    }

    /** Translate a GLOBAL instruction */
    private void genGlobal(int n) {
    	Label lab1 = new Label();
    	Name x = (Name) funcode.consts[n];

    	// Name x = (Name) consts[n];
	genField("consts", n);
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

    /** Translate a JFALSE instruction */
    protected final void genJFalse(int addr) {
	access("asBoolean", fun__B_t, new Crash("boolcond"));
	code.gen(IFEQ, makeLabel(addr));
    }	

    private void genFail() {
	int n = funcode.arity;

    	// ErrContext.err_nomatch(args, 0, arity);
	if (n < MANY) {
	    for (int i = 0; i < n; i++)
		code.gen(ALOAD, _args+i);
	    code.gen(INVOKESTATIC, evaluator_cl, "err_nomatch"+n, failn_t[n]);
	}
	else {
	    code.gen(ALOAD, _args);
	    code.gen(CONST, 0);
	    code.gen(CONST, n);
	    code.gen(INVOKESTATIC, evaluator_cl, "err_nomatch", fun_AII_t);
	}

    	// return null;
    	code.gen(ACONST_NULL);
    	code.gen(ARETURN);
    }

    private void genCloprep(int n) {
	nstack.push(n);
	code.gen(CHECKCAST, funcode_cl);
	code.gen(CONST, n+1);
	code.gen(ANEWARRAY, value_cl);
	if (n > 0) {
	    code.gen(DUP);
	    code.gen(CONST, 1);
	}
    }

    private void genPutfvar(int i) {
	int n = nstack.peek();

	code.gen(AASTORE);
	if (i < n) {
	    code.gen(DUP);
	    code.gen(CONST, i+1);
	}
    }

    private void genClosure(int n) {
	nstack.pop();
	code.gen(INVOKEVIRTUAL, funcode_cl, "makeClosure", fun_A_V_t);
    }
    
    private void genMNil() {
    	// v = stack[--sp];
    	// if (! v.isNilValue()) goto trap;
    	code.gen(INSTANCEOF, nilval_cl);
    	code.gen(IFEQ, trap);
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

    private void genMCons() {
	trapCast(consval_cl);
    	code.gen(DUP); 
    	code.gen(GETFIELD, consval_cl, "head", value_t);
    }

    private void genGettail() {
    	code.gen(GETFIELD, consval_cl, "tail", value_t);
    }

    private void genMPlus(int n) {
	trapCast(numval_cl);
	genField("consts", n);
    	code.gen(INVOKEVIRTUAL, numval_cl, "matchPlus", fun_V_V_t);
    	code.gen(DUP);
    	code.gen(ASTORE, _temp);
    	code.gen(IFNULL, trap);
    	pushLocal(_temp);
    }

    private void genMEq() {
    	// v = stack[--sp];
    	// if (! v.equals(stack[--sp])) goto trap;
    	code.gen(INVOKEVIRTUAL, object_cl, "equals", fun_O_B_t);
    	code.gen(IFEQ, trap);
    }

    /** Match a primitive as constructor */
    private void genMPrim(int n) {
    	// temp = ((Function) cons).subr.pattMatch(obj, n)
	cast(funval_cl, new Crash("pattmatch"));
	code.gen(GETFIELD, funval_cl, "subr", function_t);
    	code.gen(SWAP);				// obj, subr
    	code.gen(CONST, n);			// n, obj, subr
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

    /** Default translation of each opcode, if not overridden by rules */
    private void translate(Opcode op, int rand) {
	switch (op) {
	    case GLOBAL:  genGlobal(rand); break;
	    case LOCAL:   pushLocal(_frame+rand); break;
	    case BIND:    code.gen(ASTORE, _frame+rand); break;
	    case QUOTE:   genField("consts", rand); break;
	    case FVAR:    genField("fvars", rand); break;
	    case ARG:     genArg(rand); break;
	    case POP:     code.gen(POP); break;
	    case RETURN:  code.gen(ARETURN); break;
	    case NIL:     code.gen(GETSTATIC, value_cl, "nil", value_t); break;
	    case PRECONS: genPrecons(); break;
	    case CONS:    genCons(); break;
	    case CLOSURE: genClosure(rand); break;
	    case TRAP:    nextcache = cache; trap = makeLabel(rand); break;
	    case FAIL:    genFail(); break;
	    case JFALSE:  genJFalse(rand); break;
	    case JUMP:    code.gen(GOTO, makeLabel(rand)); break;
	    case PREP:    genPrep(rand); break;
	    case PUTARG:  genPutarg(rand); break;
	    case CLOPREP: genCloprep(rand); break;
	    case PUTFVAR: genPutfvar(rand); break;
	    case CALL:    genCall(rand); break;
	    case TCALL:   genTCall(rand); break;
	    case MEQ:     genMEq(); break;
	    case MPRIM:   genMPrim(rand); break;
	    case MCONS:   genMCons(); break;
	    case GETTAIL: genGettail(); break;
	    case MNIL:    genMNil(); break;
	    case MPLUS:   genMPlus(rand); break;
	    default:
		throw new Error("Bad opcode " + op);
	}
    }

    protected void init() {
	labdict.clear(); handlers.clear(); nstack.clear();
	trap = null; cache = -1;
    }	

    // The normal code generation process can be overridden by a
    // dynamic mechanism that uses a collection of `hooks', each
    // matching a fixed sequence of opcodes.  Each hook returns true
    // to indicate that it has compiled code for the sequence, or
    // false if other hooks or the deafult implementation of the
    // instructions should be tried.  (The default implementations
    // themselves could be expressed as rules, at the expense of a lot
    // of boilerplate.)

    /** A rule that matches a sequence of opcodes */
    public abstract class CodeHook {
	/** The opcode sequence that the rule matches */
	private final Opcode pattern[];

	public CodeHook(Opcode... pattern) { this.pattern = pattern; }

	/** Compile code for the sequence and return how many
	    opcodes were handled, or return zero. */
	public abstract int compile(int ip);

	/** Check if the pattern matches and if so invoke compile. */
	private int fire(int ip) {
	    if (funcode.instrs[ip] != pattern[0])
		return 0;

	    for (int i = 1; i < pattern.length; i++) {
		if (labdict.get(ip+1) != null 
		    || funcode.instrs[ip+i] != pattern[i])
		    return 0;
	    }

	    return compile(ip);
	}
    }

    /** A code hook for one opcode */
    public abstract class CodeHook1 extends CodeHook {
	public CodeHook1(Opcode op) { super(new Opcode[] { op }); }

	public abstract int compile1(int rand);

	@Override 
	public int compile(int ip) { 
	    return compile1(funcode.rands[ip]); 
	}
    }

    /** A code hook for two opcodes */
    public abstract class CodeHook2 extends CodeHook {
	public CodeHook2(Opcode op1, Opcode op2) { 
	    super(new Opcode[] { op1, op2 }); 
	}

	public abstract int compile2(int rand1, int rand2);

	@Override 
	public int compile(int ip) { 
	    return compile2(funcode.rands[ip], funcode.rands[ip+1]); 
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

    public JitTranslator() {
	/* Treat FVAR 0 / PREP nargs specially */
	addHook(new CodeHook2(Opcode.FVAR, Opcode.PREP) {
	    @Override
	    public int compile2(int n, int nargs) {
		if (n != 0) return 0;
		code.gen(ALOAD, _this);
		prepArgs(nargs);
		return 2;
	    }
	});

	// Minor optimisation of pattern matching code

	/* MCONS that throws away the head */
	addHook(new CodeHook(Opcode.MCONS, Opcode.POP) {
	    @Override
	    public int compile(int ip) {
		trapCast(consval_cl);
		return 2;
	    }
	});

	/* GETTAIL that throws away the tail */
	addHook(new CodeHook(Opcode.GETTAIL, Opcode.POP) {
	    @Override
	    public int compile(int ip) {
		code.gen(POP);
		return 2;
	    }
	});
    }

    private byte[] process(FunCode funcode) {
	init();
    	start(funcode);

    	for (int ip = 0; ip < funcode.instrs.length; ) {
    	    FunCode.Opcode op = funcode.instrs[ip];
    	    int rand = funcode.rands[ip];
    	    
	    Label lab = labdict.get(ip);
	    if (lab != null) {
		code.label(lab);
		cache = -1;
	    }

	    nextcache = -1;

	    int done = 0;

	    // Try to compile using specialised hooks
	    List<CodeHook> hooklist = hooks.get(op);
	    if (hooklist != null) {
		for (CodeHook hook: hooklist) {
		    done = hook.fire(ip);
		    if (done > 0) break;
		}
	    }

	    if (done > 0)
		ip += done;
	    else {
		translate(op, rand);
		ip++;
	    }

	    cache = nextcache;
	}

	compileHandlers();
	return clfile.toByteArray();
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
	
    private Map<Handler, Handler> handlers = 
	new HashMap<Handler, Handler>();

    private Label makeHandler(Handler handler) {
	Handler handler1 = handlers.get(handler);
	if (handler1 != null) return handler1.label;
	handler.label = new Label();
	handlers.put(handler, handler);
	return handler.label;
    }

    private void compileHandlers() {
	for (Handler handler : handlers.values()) {
	    code.label(handler.label);
	    code.gen(POP);
	    handler.compile();
	    code.gen(ACONST_NULL);
	    code.gen(ARETURN);
	}
    }

    private class Crash extends Handler {
	public Crash(String method) {
	    super("*crash", method);
	}

	@Override 
	public void compile() {
	    code.gen(INVOKESTATIC, evaluator_cl, "err_"+failure, fun_t);
	}
    }

    protected final void cast(String cl, Handler handler) {
	Label start = new Label(), end = new Label();
	code.tryCatchBlock(start, end, makeHandler(handler), classcast_cl);
	code.label(start);
	code.gen(CHECKCAST, cl);
	code.label(end);
    }

    protected final void access(String method, Type type, Handler handler) {
	Label start = new Label(), end = new Label();
	code.tryCatchBlock(start, end, makeHandler(handler), wrongkind_cl);
	code.label(start);
	code.gen(INVOKEVIRTUAL, value_cl, method, type);
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
	    return defineClass(name, b, 0, b.length);
	}

	@Override 
	public void finalize() {
	    if (Evaluator.debug > 2)
		System.out.printf("Discarding class %s\n", name);
	}
    }

    /** Table for intepreting stack traces */
    private Map<String, WeakReference<FunCode>> classTable = 
	new HashMap<String, WeakReference<FunCode>>();
        /* The table will fill up with junk over time, but the weak references
	   will at least prevent retention of stale FunCode objects */

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
	classTable.put(className, new WeakReference<FunCode>(funcode));

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
	Thread thread = Thread.currentThread();
	StackTraceElement stack[] = thread.getStackTrace();
	String caller = null, callee = me;

	for (int i = 0; i < stack.length; i++) {
	    WeakReference<FunCode> fr = 
		classTable.get(stack[i].getClassName());
	    if (fr == null) continue;
	    FunCode f = fr.get();
	    if (f == null) throw new Error("stack map entry disappeared");

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
