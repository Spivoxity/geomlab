/*
 * InlineTranslator.java
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

import funbase.Primitive;
import funbase.Value;
import funbase.Name;
import funbase.Function;
import funbase.FunCode.Opcode;
import static funjit.Opcodes.*;
import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

import java.util.*;

/** A JIT translator with inlining of some primitives */
public class InlineTranslator extends JitTranslator {
    /** Extra class names */
    private static final String
	cell_cl = "plugins/Cell",
	color_cl = "plugins/ColorValue";

    /** Stack to track inlinable primitives in a nest of calls */
    private Stack<Inliner> funstack = new Stack<Inliner>();
    
    /** Dictionary mapping primitive names to their inline
     *  code generators */
    private Map<String, Inliner> primdict = 
	new HashMap<String, Inliner>();

    /** Register an inline code generator */
    private void register(Inliner c) {
	primdict.put(c.name, c);
    }

    public InlineTranslator() {
	// Add hooks to the parent to spot specific code sequences,
	// general ones before specific ones

	/* Hook to watch for other PUTARG instructions */
	addHook(new CodeHook1(Opcode.PUTARG) {
	    public boolean compile1(int i) {
		convertArg(i, Kind.VALUE);
		return true;
	    }
	});

	/* Hook for other CALL instructions */
	addHook(new CodeHook1(Opcode.CALL) {
	    public boolean compile1(int nargs) {
		Inliner gen = funstack.pop(); // Pop anyway
		if (gen == null) return false;
		convValue(gen.call());
		return true;
	    }
	});

	addHook(new CodeHook2(Opcode.GLOBAL, Opcode.PREP) {
	    public boolean compile2(int n, int nargs) {
		return doGlobalPrep(n, nargs);
	    }
	});

	addHook(new CodeHook2(Opcode.QUOTE, Opcode.PUTARG) {
	    public boolean compile2(int n, int i) {
		return doQuotePutarg(n, i);
	    }
	});

	/* Hook to watch for CALL / PUTARG */
	addHook(new CodeHook2(Opcode.CALL, Opcode.PUTARG) {
	    public boolean compile2(int nargs, int i) {
		Inliner gen = funstack.peek();
		if (gen == null) return false;
		funstack.pop();
		Kind k = gen.call();
		convertArg(i, k);
		return true;
	    }
	});

	addHook(new CodeHook2(Opcode.CALL, Opcode.JFALSE) {
	    public boolean compile2(int nargs, int addr) {
		Inliner gen = funstack.peek();
		if (gen == null) return false;
		funstack.pop();
		gen.jcall(addr);
		return true;
	    }
	});
	
	/* Notice FVAR 0 / PREP, but leave it to the existing rule to
	   translate it */
	addHook(new CodeHook2(Opcode.FVAR, Opcode.PREP) {
	    public boolean compile2(int n, int nargs) {
		if (n == 0) funstack.push(null);
		return false;
	    }
	});

	/* Also notice other PREP instructions */
	addHook(new CodeHook1(Opcode.PREP) {
	    public boolean compile1(int nargs) {
		funstack.push(null);
		return false;
	    }
	});

	register(new InlineEquals("=", true));
	register(new InlineEquals("<>", false));
	register(new InlineOp("+", DADD));
	register(new InlineOp("-", DSUB));
	register(new InlineOp("*", DMUL));
	register(new InlineOp("uminus", DNEG));
	register(new InlineComp("<", DCMPG, IFGE));
	register(new InlineComp("<=", DCMPG, IFGT));
	register(new InlineComp(">", DCMPL, IFLE));
	register(new InlineComp(">=", DCMPG, IFLT));
	register(new InlineListSel("head"));
	register(new InlineListSel("tail"));
	register(new InlineSelect("rpart", color_cl, "colour", Kind.NUMBER));
	register(new InlineSelect("gpart", color_cl, "colour", Kind.NUMBER));
	register(new InlineSelect("bpart", color_cl, "colour", Kind.NUMBER));

	register(new SimpleInliner("rgb", Kind.NUMBER) {
	    @Override public Kind call() {
		code.gen(INVOKESTATIC, color_cl, "getInstance",  fun_DDD_V_t);
		return Kind.VALUE;
	    }
	});

	register(new SimpleInliner("new") {
	    @Override public Kind call() {
		code.gen(NEW, cell_cl);
		code.gen(DUP_X1);
		code.gen(SWAP);
		code.gen(INVOKESPECIAL, cell_cl, "<init>", fun_V_t);
		return Kind.VALUE;
	    }
	});

	register(new InlineSelect("!", cell_cl, "cell", "contents", 
				  Kind.VALUE));

	register(new SimpleInliner(":=") {
	    @Override public Kind call() {
		// cell, val
		code.gen(SWAP);
		castarg(":=", cell_cl, "cell");
		code.gen(SWAP);
		code.gen(DUP_X1);
		// val, cell, val
		code.gen(PUTFIELD, cell_cl, "contents", value_t);
		return Kind.VALUE;
	    }
	});
    }

    private enum Kind { VALUE, NUMBER, BOOL }

    /** Convert a value on the stack from kind k to kind VALUE. */
    private void convValue(Kind k) {
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
    private void convert(String name, Kind k1, Kind k2) {
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

    /** Hook to watch for GLOB / PREP pairs */
    private boolean doGlobalPrep(int glob, int nargs) {
	Name f = (Name) funcode.consts[glob];

	if (f.isFrozen() && f.glodef != null 
	    && f.glodef instanceof Value.FunValue) {
	    Function fun = ((Value.FunValue) f.glodef).subr;
	    if ((fun instanceof Primitive) && fun.arity == nargs) {
		Primitive p = (Primitive) fun;
		Inliner c = primdict.get(p.name);
		if (c != null) {
		    funstack.push(c);
		    return true;
		}
	    }
	}

	return false;
    }

    /** Hook to watch for QUOTE / PUTARG sequences */
    private boolean doQuotePutarg(int n, int i) {    
	Inliner gen  = funstack.peek();
	if (gen == null) return false;

	Kind k = gen.argkind(i);
	Value v = funcode.consts[n];

	// Put numeric constants in the JVM constant pool
	if (k == Kind.NUMBER && (v instanceof Value.NumValue)) {
	    Value.NumValue x = (Value.NumValue) v;
	    code.gen(CONST, x.val);
	    return true;
	}

	return false;
    }

    /** Convert i'th argument to suit function being called */
    private void convertArg(int i, Kind k) {
	Inliner gen = funstack.peek();

	if (gen != null)
	    convert(gen.name, k, gen.argkind(i));
	else
	    convValue(k);
    }

    /** Prepare for new function */
    @Override protected void init() {
	super.init();
	funstack.clear();
    }

    /** A code generator for a specific primitive */
    private abstract class Inliner {
	public final String name;

	public Inliner(String name) {
	    this.name = name;
	}

	/** Determine the kind for the i'th argument */
	public abstract Kind argkind(int i);

	/** Compile code for a call */
	public abstract Kind call();

	/** Compile code for a call followed by JFALSE */
	public void jcall(int addr) {
	    Kind k = call();
	    if (k == Kind.BOOL)
		code.gen(IFEQ, makeLabel(addr));
	    else {
		convValue(k);
		genJFalse(addr);
	    }
	}
    }

    /** An inline code generator with all args of the same kind */
    private abstract class SimpleInliner extends Inliner {
	private Kind argkind;

	public SimpleInliner(String name) {
	    this(name, Kind.VALUE);
	}

	public SimpleInliner(String name, Kind argkind) {
	    super(name);
	    this.argkind = argkind;
	}

	@Override public Kind argkind(int i) { return argkind; }
    }

    /** Inliner for = and <> */
    public class InlineEquals extends SimpleInliner {
	private boolean sense;

	public InlineEquals(String name, boolean sense) {
	    super(name);
	    this.sense = sense;
	}

	@Override public Kind call() {
	    code.gen(INVOKEVIRTUAL, object_cl, "equals", fun_O_B_t);
	    if (! sense) {
		code.gen(CONST, 1); code.gen(IXOR);
	    }
	    return Kind.BOOL;
	}
    }

    /** Inliner for numeric operations */
    public class InlineOp extends SimpleInliner {
	private Op op;

	public InlineOp(String name, Op op) {
	    super(name, Kind.NUMBER);
	    this.op = op;
	}

	@Override public Kind call() {
	    code.gen(op);
	    return Kind.NUMBER;
	}
    }

    /** Inliner for numeric comparisons */
    public class InlineComp extends SimpleInliner {
	private Op op1;		// Double comparison op DCMPL or DCMPG
	private Op op2;		// Conditional branch if condition false

	public InlineComp(String name, Op op1, Op op2) {
	    super(name, Kind.NUMBER);
	    this.op1 = op1; this.op2 = op2;
	}

	@Override public Kind call() {
	    Label lab = new Label(), lab2 = new Label();    
	    code.gen(op1);
	    code.gen(op2, lab);
	    code.gen(CONST, 1);
	    code.gen(GOTO, lab2);
	    code.label(lab);
	    code.gen(CONST, 0);
	    code.label(lab2);
	    return Kind.BOOL;
	}

	@Override public void jcall(int addr) {
	    code.gen(op1);
	    code.gen(op2, makeLabel(addr));
	}
    }

    /** Inliner for head and tail */
    public class InlineListSel extends SimpleInliner {
	public InlineListSel(String name) {
	    super(name);
	}

	@Override public Kind call() {
	    code.gen(DUP);
	    code.gen(ASTORE, _temp);
	    cast(consval_cl, new Handler(name, "list") {
		    @Override public void compile() {
			// Evaluator.list_fail(<msg>);
			code.gen(ALOAD, _temp);
			code.gen(CONST, prim);
			code.gen(INVOKESTATIC, evaluator_cl, "list_fail", 
				 fun_VS_t);
		    }
		});
	    code.gen(GETFIELD, consval_cl, name, value_t);
	    return Kind.VALUE;
	}
    }	

    /** Inliner for generic field selection function */
    public class InlineSelect extends SimpleInliner {
	private final String cl, cl_name, field;
	private final Kind kind;

	public InlineSelect(String name, String cl, String cl_name,
			    Kind kind) {
	    this(name, cl, cl_name, name, kind);
	}

	public InlineSelect(String name, String cl, String cl_name,
			    String field, Kind kind) {
	    super(name);
	    this.cl = cl; this.cl_name = cl_name; 
	    this.field = field; this.kind = kind;
	}

	@Override public Kind call() {
	    Type ty;

	    switch (kind) {
		case VALUE: ty = value_t; break;
		case NUMBER: ty = double_t; break;
		case BOOL: ty = bool_t; break;
		default: throw new Error("Bad kind in InlineSelect");
	    }

	    castarg(name, cl, cl_name);
	    code.gen(GETFIELD, cl, field, ty);
	    return kind;
	}
    }
}
