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
import static funjit.Opcodes.*;
import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

import java.util.*;

/** A JIT translator with inlining of some primitives */
public class InlineTranslator extends JitTranslator {
    /** Extra class names */
    private static final String
	cell_cl = "plugins/Cell",
	colorval_cl = "plugins/ColorValue";

    /** Stack to track inlinable primitives in a nest of calls */
    private Stack<Inline> funstack = new Stack<Inline>();
    
    /** Dictionary mapping primitive names to their inline
     *  code generators */
    private Map<String, Inline> primdict = 
	new HashMap<String, Inline>();

    /** Register an inline code generator */
    private void register(Inline c) {
	primdict.put(c.name, c);
    }

    public InlineTranslator() {
	register(new InlineEquals("=", true));
	register(new InlineEquals("<>", false));
	register(new InlineOp("+", DADD));
	register(new InlineOp("-", DSUB));
	register(new InlineOp("*", DMUL));
	register(new InlineOp("uminus", DNEG));
	register(new InlineComp("<", DCMPG, IFLT));
	register(new InlineComp("<=", DCMPG, IFLE));
	register(new InlineComp(">", DCMPL, IFGT));
	register(new InlineComp(">=", DCMPG, IFGE));
	register(new InlineListSel("head"));
	register(new InlineListSel("tail"));
	register(new InlineSelect("rpart", colorval_cl, "colour", Kind.NUMBER));
	register(new InlineSelect("gpart", colorval_cl, "colour", Kind.NUMBER));
	register(new InlineSelect("gpart", colorval_cl, "colour", Kind.NUMBER));

	register(new SimpleInline("rgb", Kind.NUMBER) {
		@Override
		public Kind call() {
		    code.gen(INVOKESTATIC, colorval_cl, "getInstance", 
			     fun_DDD_V_t);
		    return Kind.VALUE;
		}
	    });

	register(new SimpleInline("new") {
		@Override
		public Kind call() {
		    code.gen(NEW, cell_cl);
		    code.gen(DUP_X1);
		    code.gen(SWAP);
		    code.gen(INVOKESPECIAL, cell_cl, "<init>", fun_V_t);
		    return Kind.VALUE;
		}
	    });

	register(new InlineSelect("!", cell_cl, "cell", "contents", 
				  Kind.VALUE));

	register(new SimpleInline(":=") {
		@Override
		public Kind call() {
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

    /** Hook into method that watches for QUOTE / PUTARG sequences */
    @Override
    protected void genQuotePutarg(int n, int i) {    
	Inline gen  = funstack.peek();

	if (gen != null) {
	    Kind k = gen.argkind(i);
	    Value v = consts[n];

	    if (k == Kind.NUMBER && (v instanceof Value.NumValue)) {
		Value.NumValue x = (Value.NumValue) v;
		code.gen(CONST, x.val);
		return;
	    }
	}

        super.genQuotePutarg(n, i);
    }

    /** Hook into method for PREP instruction */
    @Override
    protected void genPrep(int nargs) {
	super.genPrep(nargs);
    	funstack.push(null);
    }

    /** Hook into method to watch for GLOB / PREP pairs */
    @Override
    protected void genGlobalPrep(int glob, int nargs) {
	Name f = (Name) consts[glob];

	if (f.isFrozen() && f.glodef != null && f.glodef.isFunValue()) {
	    Function fun = ((Value.FunValue) f.glodef).subr;
	    if (fun instanceof Primitive) {
		Primitive p = (Primitive) fun;
		Inline c = primdict.get(p.name);
		if (c != null) {
		    funstack.push(c);
		    return;
		}
	    }
	}

	// An ordinary function: compile an out-of-line call
	super.genGlobalPrep(glob, nargs);
    }

    /** Hook into method for PUTARG instructions */
    @Override
    protected void genPutarg(int i, Kind k) {
	Inline gen = funstack.peek();

	if (gen != null) {
	    convert(gen.name, k, gen.argkind(i));
	    return;
	}

	super.genPutarg(i, k);
    }

    /** Hook into method for CALL instruction */
    @Override
    protected Kind genCall(int nargs) {
    	Inline gen = funstack.pop();

    	if (gen != null)
	    // Compile the function inline
    	    return gen.call();

	return super.genCall(nargs);
    }

    /** Prepare for new function */
    @Override
    protected void init() {
	super.init();
	funstack.clear();
    }

    /** A code generator for a specific primitive */
    private abstract class Inline {
	public final String name;

	public Inline(String name) {
	    this.name = name;
	}

	/** Determine the kind for the i'th argument */
	public abstract Kind argkind(int i);

	/** Compile code for a call */
	public abstract Kind call();
    }

    /** An inline code generator with all args of the same kind */
    private abstract class SimpleInline extends Inline {
	private Kind argkind;

	public SimpleInline(String name) {
	    this(name, Kind.VALUE);
	}

	public SimpleInline(String name, Kind argkind) {
	    super(name);
	    this.argkind = argkind;
	}

	@Override
	public Kind argkind(int i) { return argkind; }
    }

    /** Inliner for = and <> */
    public class InlineEquals extends SimpleInline {
	private boolean sense;

	public InlineEquals(String name, boolean sense) {
	    super(name);
	    this.sense = sense;
	}

	@Override
	public Kind call() {
	    code.gen(INVOKEVIRTUAL, object_cl, "equals", fun_O_B_t);
	    if (! sense) {
		code.gen(CONST, 1); code.gen(IXOR);
	    }
	    return Kind.BOOL;
	}
    }

    /** Inliner for numeric operations */
    public class InlineOp extends SimpleInline {
	private Op op;

	public InlineOp(String name, Op op) {
	    super(name, Kind.NUMBER);
	    this.op = op;
	}

	@Override
	public Kind call() {
	    code.gen(op);
	    return Kind.NUMBER;
	}
    }

    /** Inliner for numeric comparisons */
    public class InlineComp extends SimpleInline {
	private Op op1, op2;

	public InlineComp(String name, Op op1, Op op2) {
	    super(name, Kind.NUMBER);
	    this.op1 = op1; this.op2 = op2;
	}

	@Override
	public Kind call() {
	    Label lab = new Label(), lab2 = new Label();    
	    code.gen(op1);
	    code.gen(op2, lab);
	    code.gen(CONST, 0);
	    code.gen(GOTO, lab2);
	    code.label(lab);
	    code.gen(CONST, 1);
	    code.label(lab2);
	    return Kind.BOOL;
	}
    }

    /** Inliner for head and tail */
    public class InlineListSel extends SimpleInline {
	public InlineListSel(String name) {
	    super(name);
	}

	@Override
	public Kind call() {
	    code.gen(DUP);
	    code.gen(ASTORE, _temp);
	    cast(consval_cl, new Handler(name, "list") {
		    @Override
		    public void compile() {
			// cxt.list_fail(<msg>);
			code.gen(ALOAD, _cxt);
			code.gen(ALOAD, _temp);
			code.gen(CONST, prim);
			code.gen(INVOKEVIRTUAL, errcxt_cl, "list_fail", 
				 fun_VS_t);
		    }
		});
	    code.gen(GETFIELD, consval_cl, name, value_t);
	    return Kind.VALUE;
	}
    }	

    /** Inliner for generic field selection function */
    public class InlineSelect extends SimpleInline {
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

	@Override
	public Kind call() {
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
