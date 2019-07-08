/*
 * FunctionClass.java
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

import funbase.Evaluator;
import funbase.Primitive;

import static funjit.Opcodes.*;
import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

/** A specialised version of ClassFile for making GeomLab functions. */
public class FunctionClass extends ClassFile {
    private String fname;
    private int arity;
    private String superclass;
    private Method code;

    public FunctionClass(String name, int arity, 
                         String smallcl, String largecl) {
        this(name, name, arity, smallcl, largecl);
    }

    public FunctionClass(String fname, String mname, int arity, 
                         String smallcl, String largecl) {
        super(ACC_PUBLIC+ACC_SUPER, gensym(mname), null);
        this.fname = fname;
        this.arity = arity;
        this.superclass =
            (arity < JitTranslator.MANY ? smallcl+arity : largecl);
        setSuperclass(superclass);

        makeInit();
        start();
    }

    private void makeInit() {
        Method init = addMethod(ACC_PUBLIC, "<init>", fun_t);

        if (arity < JitTranslator.MANY) {
            // super(fname);
            init.gen(ALOAD, 0);
	    init.gen(CONST, fname);
	    init.gen(INVOKESPECIAL, superclass, "<init>", fun_S_t);
        }
        else {
            // super(fname, arity);
	    init.gen(ALOAD, 0);
	    init.gen(CONST, fname);
	    init.gen(CONST, arity);
	    init.gen(INVOKESPECIAL, superclass, "<init>", fun_SI_t);
        }

        init.gen(RETURN);
    }

    private void start() {
        if (arity < JitTranslator.MANY)
            code = addMethod(ACC_PUBLIC, "apply"+arity, applyn_t[arity]);
        else {
            code = addMethod(ACC_PUBLIC, "apply", apply_t);

            // if (args.length != arity) 
	    //     ErrContext.err_nargs(this.name, args.length, arity)
	    Label lab = new Label();
	    code.gen(ALOAD, 1);
            code.gen(ARRAYLENGTH);
	    code.gen(CONST, arity);
	    code.gen(IF_ICMPEQ, lab);
	    code.gen(ALOAD, 0);
	    code.gen(GETFIELD, function_cl, "name", string_t);
	    code.gen(ALOAD, 1);
            code.gen(ARRAYLENGTH);
	    code.gen(CONST, arity);
	    code.gen(INVOKESTATIC, evaluator_cl, "err_nargs", fun_SII_t);
	    code.label(lab);
        }
    }

    public void finish() {
        compileHandlers();
    }

    @Override
    public byte[] toByteArray() {
        byte binary[] = super.toByteArray();

	if (Evaluator.debug > 4) {
	    try {
		java.io.OutputStream dump = 
		    new java.io.FileOutputStream(name + ".class");
		dump.write(binary);
		dump.close();
	    }
	    catch (java.io.IOException ex) { }
	}

        return binary;
    }

    public void checkpoint() {
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

    public void widen(Species k) {
        k.widen(this);
    }

    public void narrow(Species k, String prim) {
        k.narrow(prim, this);
    }

    public void cast(String cl, String name) {
        cast(cl, new ExpectCl(name, cl));
    }

    public void cast(String cl, Handler handler) {
	Label start = new Label(), end = new Label();
	code.tryCatchBlock(start, end, makeHandler(handler), classcast_cl);
	code.label(start);
	code.gen(CHECKCAST, cl);
	code.label(end);
    }

    public void access(String method, Type type, String prim, String kind) {
        access(method, type, new Expect(prim, kind));
    }

    public void access(String method, Type type, Handler handler) {
	Label start = new Label(), end = new Label();
	code.tryCatchBlock(start, end, makeHandler(handler), wrongkind_cl);
	code.label(start);
	code.gen(INVOKEVIRTUAL, value_cl, method, type);
	code.label(end);
    }

    public abstract class Handler {
	public final String prim;
	public final String reason;
	protected Label label = null;

	public Handler(String prim, String reason) {
	    this.prim = prim;
	    this.reason = reason;
	}

	public abstract void compile();

	@Override 
	public boolean equals(Object other) {
	    Handler that = (Handler) other;
	    return (this.getClass() == that.getClass()
		    && this.prim.equals(that.prim) 
		    && this.reason.equals(that.reason));
	}

	@Override 
	public int hashCode() {
	    return 5 * prim.hashCode() + reason.hashCode();
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

    public class Crash extends Handler {
	public Crash(String method) {
	    super("*crash", method);
	}

	@Override 
	public void compile() {
	    code.gen(INVOKESTATIC, evaluator_cl, "err_"+reason, fun_t);
	}
    }

    public class Expect extends Handler {
	public Expect(String prim, String tyname) {
	    super(prim, tyname);
	}

	@Override 
	public void compile() {
	    // ErrContext.expect(prim, failure);
	    Primitive p = Primitive.find(prim);
	    code.gen(CONST, p.name);
	    code.gen(CONST, reason);
	    code.gen(INVOKESTATIC, evaluator_cl, "expect", fun_SS_t);
	}
    }

    public class ExpectCl extends Handler {
	public ExpectCl(String prim, String tyname) {
	    super(prim, tyname);
	}

	@Override 
	public void compile() {
	    // ErrContext.expect(prim, failure);
	    Primitive p = Primitive.find(prim);
	    code.gen(CONST, p.name);
	    code.gen(CLASS, reason);
	    code.gen(INVOKESTATIC, evaluator_cl, "expect", fun_SC_t);
	}
    }

    // Method delegates for generating code
    public void gen(Op op) {
        code.gen(op);
    }

    public void gen(Op op, int rand) {
        code.gen(op, rand);
    }

    public void gen(Op op, String cl) {
        code.gen(op, cl);
    }

    public void gen(Op op, String cl, String name, Type ty) {
        code.gen(op, cl, name, ty);
    }

    public void gen(Op op, Label label) {
        code.gen(op, label);
    }

    public void gen(Op op, double rand) {
        code.gen(op, rand);
    }

    public void gen(Op op, Object rand) {
        code.gen(op, rand);
    }

    public void tryCatchBlock(Label start, Label end, 
                              Label handler, String type) {
        code.tryCatchBlock(start, end, handler, type);
    }

    public void label(Label label) {
        code.label(label);
    }


    private static int gcount = 0;

    private static String gensym(String name) {
	return String.format("G%04d_%s", ++gcount, name);
    }
}
