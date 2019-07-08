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

import java.util.*;

import funbase.FunCode;
import funbase.FunCode.Opcode;
import funbase.Function;
import funbase.Primitive;
import funbase.Evaluator;
import funbase.Name;
import funbase.Value;
import funbase.Value.WrongKindException;

import static funjit.Opcodes.*;
import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

public class InlineTranslator extends JitTranslator {
    public InlineTranslator() {
	// Inliners for various common primitives
	register(new Equality("=", true));
	register(new Equality("<>", false));
	register(new Operator("+", DADD));
	register(new Operator("-", DSUB));
	register(new Operator("*", DMUL));
	register(new Operator("_uminus", DNEG));
	register(new Comparison("<", DCMPG, IFGE));
	register(new Comparison("<=", DCMPG, IFGT));
	register(new Comparison(">", DCMPL, IFLE));
	register(new Comparison(">=", DCMPG, IFLT));
	register(new ListSelect("head", "#head"));
	register(new ListSelect("tail", "#tail"));

        register(new Inliner("numeric") {
            @Override
            public Species call() {
                code.gen(INVOKESTATIC, value_cl, "numeric", fun_V_B_t);
                return Species.BOOL;
            }
        });

        register(new Inliner("/") {
            @Override
            public Species argkind(int i) { return Species.NUMBER; }

            public Species call() {
                Label lab = new Label();
                code.gen(DUP2);
                code.gen(CONST, 0.0);
                code.gen(DCMPL);
                code.gen(IFNE, lab);
                code.gen(INVOKESTATIC, evaluator_cl, "err_divzero", fun_t);
                code.label(lab);
                code.gen(DDIV);
                return Species.NUMBER;
            }
        });
    }

    /** Inliner for = and <> */
    public class Equality extends Inliner {
	private boolean sense;

	public Equality(String name, boolean sense) {
	    super(name);
	    this.sense = sense;
	}

	@Override 
	public Species call() {
	    code.gen(INVOKEVIRTUAL, object_cl, "equals", fun_O_B_t);
	    if (! sense) {
		code.gen(CONST, 1); code.gen(IXOR);
	    }
	    return Species.BOOL;
	}

	@Override 
	public void jcall(Label lab) {
	    code.gen(INVOKEVIRTUAL, object_cl, "equals", fun_O_B_t);
	    code.gen((sense ? IFEQ : IFNE), lab);
	}
    }

    /** Inliner for numeric operations */
    public class Operator extends Inliner {
	private Op op;

	public Operator(String name, Op op) {
	    super(name);
	    this.op = op;
	}

        @Override
        public Species argkind(int i) { return Species.NUMBER; }

	@Override
	public Species call() {
	    code.gen(op);
	    return Species.NUMBER;
	}
    }

    /** Inliner for numeric comparisons */
    public class Comparison extends Inliner {
	private Op cmp_op;	// Double comparison op DCMPL or DCMPG
	private Op jump_op;	// Conditional branch if condition false

	public Comparison(String name, Op cmp_op, Op jump_op) {
	    super(name);
	    this.cmp_op = cmp_op; 
            this.jump_op = jump_op;
	}

        @Override
        public Species argkind(int i) { return Species.NUMBER; }

	@Override 
	public Species call() {
	    Label lab = new Label(), lab2 = new Label();    
	    code.gen(cmp_op);
	    code.gen(jump_op, lab);
	    code.gen(CONST, 1);
	    code.gen(GOTO, lab2);
	    code.label(lab);
	    code.gen(CONST, 0);
	    code.label(lab2);
	    return Species.BOOL;
	}

	@Override 
	public void jcall(Label lab) {
	    code.gen(cmp_op);
	    code.gen(jump_op, lab);
	}
    }

    /** Inliner for head and tail */
    public class ListSelect extends Inliner {
	String errtag;

	public ListSelect(String name, String errtag) {
	    super(name);
	    this.errtag = errtag;
	}

	@Override 
	public Species call() {
	    code.gen(DUP);
	    code.gen(ASTORE, _temp);
	    code.cast(consval_cl, code.new Handler(name, "list") {
                @Override 
                public void compile() {
                    // Evaluator.list_fail(<msg>);
                    code.gen(ALOAD, _temp);
                    code.gen(CONST, errtag);
                    code.gen(INVOKESTATIC, evaluator_cl, 
                             "list_fail", fun_VS_t);
                }
            });
	    code.gen(GETFIELD, consval_cl, name, value_t);
	    return Species.VALUE;
	}
    }	

    /** An inliner that calls a specific method */
    private class Invoker extends Inliner {
        String cl, mname;
        Species rcvr;
        Species pkinds[];
        Species rkind;
        Type mtype;

        public Invoker(String name, String cl, String mname, 
                       Species rcvr, Species pkinds[], Species rkind) {
            super(name);
            this.cl = cl; this.mname = mname; this.rcvr = rcvr;
            this.pkinds = pkinds; this.rkind = rkind; 
            this.mtype = Species.methType(pkinds, rkind);
        }

        @Override
        public Species argkind(int i) {
            if (rcvr == null)
                return pkinds[i];
            else
                return (i == 0 ? rcvr : pkinds[i-1]);
        }

        @Override
        public Species call() {
            // We don't inline the method call, but we do inline
            // the boilerplate code that prepares for it.
            if (rcvr == null)
                code.gen(INVOKESTATIC, cl, mname, mtype);
            else
                code.gen(INVOKEVIRTUAL, cl, mname, mtype);

            return rkind;
        }
    }

    /** Inliner for generic field selection function */
    public class Selector extends Inliner {
	private final String fname;
	private final Species cl, kind;

	public Selector(String name, Species cl, String fname, Species kind) {
	    super(name);
	    this.cl = cl; this.fname = fname; this.kind = kind;
	}

        @Override
        public Species argkind(int i) {
            return cl;
        }

	@Override 
	public Species call() {
	    code.gen(GETFIELD, cl.clname, fname, kind.type);
	    return kind;
	}
    }

    /** A primitive factory that catches primitives that are implemented
        by methods or field access and creates inliners for them. */
    class InliningPrimFactory extends JitPrimFactory {
        @Override
        protected void noteMethod(String name, String cl, String mname,
                                  Species rcvr, Species pkinds[], 
                                  Species rkind) { 
            register(new Invoker(name, cl, mname, rcvr, pkinds, rkind));
        }

        @Override
        protected void noteSelector(String name, Species cl, String fname, 
                                    Species rkind) {
            register(new Selector(name, cl, fname, rkind));
        }
    }

    @Override
    public Primitive.Factory makePrimitiveFactory() {
        return new InliningPrimFactory();
    }
}
