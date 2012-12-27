/*
 * BasicPrims.java
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

package plugins;

import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import funbase.Primitive;
import funbase.Value;
import funbase.Name;
import funbase.ErrContext;
import funbase.FunCode;
import funbase.Evaluator;
import funbase.Scanner;

/** Basic primitives for handling numbers, booleans and lists */
public class BasicPrims {
    public static final Primitive primitives[] = {
	new Primitive.Prim2("=") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		return Value.makeBoolValue(x.equals(y));
	    }
	},
	
	new Primitive.Prim2("<>") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		return Value.makeBoolValue(! x.equals(y));
	    }
	},
	
	new Primitive.Prim2("+") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		return Value.makeNumValue(cxt.number(x) + cxt.number(y));
	    }
	},

	new Primitive.Prim2("-") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		return Value.makeNumValue(cxt.number(x) - cxt.number(y));
	    }
	},
	
	new Primitive.Prim2("*") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		return Value.makeNumValue(cxt.number(x) * cxt.number(y));
	    }
	},
	
	new Primitive.Prim2("/") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		if (cxt.number(y) == 0.0) 
		    cxt.primFail("division by zero", "#divzero");
		return Value.makeNumValue(cxt.number(x) / cxt.number(y));
	    }
	},
	
	new Primitive.Prim1("uminus") {
	    @Override
	    public Value invoke1(Value x) {
		return Value.makeNumValue(- cxt.number(x));
	    }
	},
	
	new Primitive.Prim2("<") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		return Value.makeBoolValue(cxt.number(x) < cxt.number(y));
	    }
	},
	
	new Primitive.Prim2("<=") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		return Value.makeBoolValue(cxt.number(x) <= cxt.number(y));
	    }
	},
	
	new Primitive.Prim2(">") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		return Value.makeBoolValue(cxt.number(x) > cxt.number(y));
	    }
	},
	
	new Primitive.Prim2(">=") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		return Value.makeBoolValue(cxt.number(x) >= cxt.number(y));
	    }
	},
	
	new Primitive.Prim1("numeric") {
	    @Override
	    public Value invoke1(Value x) {
		return Value.makeBoolValue(x.isNumValue());
	    }
	},
	
	new Primitive.Prim1("int") {
	    @Override
	    public Value invoke1(Value x) {
		return Value.makeNumValue(Math.floor(cxt.number(x)));
	    }
	},
	
	new Primitive.Prim1("sqrt") {
	    @Override
	    public Value invoke1(Value x) {
		if (cxt.number(x) < 0.0) 
		    cxt.primFail("taking square root of a negative number", 
				 "#sqrt");
		return Value.makeNumValue(Math.sqrt(cxt.number(x)));
	    }
	},
	
	new Primitive.Prim1("exp") {
	    @Override
	    public Value invoke1(Value x) {
		return Value.makeNumValue(Math.exp(cxt.number(x)));
	    }
	},

	new Primitive.Prim1("sin") {
	    @Override
	    public Value invoke1(Value x) {
		return Value.makeNumValue
		    (Math.sin(cxt.number(x) * Math.PI / 180));
	    }
	},
	
	new Primitive.Prim1("cos") {
	    @Override
	    public Value invoke1(Value x) {
		return Value.makeNumValue
		    (Math.cos(cxt.number(x) * Math.PI / 180));
	    }
	},
	
	new Primitive.Prim1("tan") {
	    @Override
	    public Value invoke1(Value x) {
		return Value.makeNumValue
		    (Math.tan(cxt.number(x) * Math.PI / 180));
	    }
	},
	
	new Primitive.Prim2("atan2") {
	    @Override
	    public Value invoke2(Value y, Value x) {
		return Value.makeNumValue
		    (Math.atan2(cxt.number(y), cxt.number(x)) * 180 / Math.PI);
	    }
	},

	new Primitive.Prim0("random") {
	    @Override
	    public Value invoke0() {
		return Value.makeNumValue(Math.random());
	    }
	},
	
	new Primitive.Prim1("name") {
	    @Override
	    public Value invoke1(Value x) {
		return Name.find(cxt.string(x));
	    }
	},

	new Primitive.Prim1("head") {
	    @Override
	    public Value invoke1(Value x) {
		return cxt.head(x);
	    }
	},
	
	new Primitive.Prim1("tail") {
	    @Override
	    public Value invoke1(Value x) {
		return cxt.tail(x);
	    }
	},
	
	new Primitive.Prim2(":") {
	    @Override
	    public Value invoke2(Value hd, Value tl) {
		if (! tl.isConsValue() && ! tl.isNilValue()) 
		    cxt.expect("list");
		return Value.cons(hd, tl);
	    }
	    
	    private Value args[] = new Value[2];

	    @Override
	    public Value[] pattMatch(Value obj, int nargs, ErrContext cxt) {
		if (nargs != 2) cxt.err_patnargs(name);
		try {
		    args[0] = obj.getTail();
		    args[1] = obj.getHead();
		    return args;
		}
		catch (Value.WrongKindException _) {
		    return null;
		}
	    }
	},

        /* A few system-oriented primitives */

        new Primitive.Prim1("primitive") {
            /* Look up a primitive */
            @Override
	    public Value invoke1(Value name) {
        	return Value.makeFunValue(Primitive.find(cxt.string(name)));
            }
        },

	new Primitive.Prim3("assemble") {
	    @Override
	    public Value invoke3(Value f0, Value arity0, Value code) {
		String f = f0.toString(); // Could be Name or String
		int arity = (int) cxt.number(arity0);
		return FunCode.assemble(f, arity, code, cxt);
	    }
	},

	new Primitive.Prim1("glodef") {
	    @Override
	    public Value invoke1(Value x) {
		Name n = cxt.name(x);
		Value v = n.getGlodef();
		if (v == null) cxt.err_notdef(n);
		return v;
	    }
	},

	new Primitive.Prim2("apply") {
	    @Override
	    public Value invoke2(Value x, Value y) {
		return x.apply(cxt.toArray(y), ErrContext.initContext);
	    }
	},

	new Primitive.Prim1("closure") {
	    @Override
	    public Value invoke1(Value code) {
		return code.makeClosure(new Value[1]);
	    }
	},

        new Primitive.Prim0("freeze") {
            @Override
            public Value invoke0() {
        	Name.freezeGlobals();
        	return Value.nil;
            }
        },

	new Primitive.Prim1("frozen") {
	    @Override
	    public Value invoke1(Value x) {
		Name n = cxt.name(x);
		return Value.makeBoolValue(n.isFrozen());
	    }
	},

	new Primitive.Prim1("spelling") {
	    @Override
	    public Value invoke1(Value x) {
		Name n = cxt.name(x);
		return Value.makeStringValue(n.toString());
	    }
	},

	new Primitive.Prim0("gensym") {
	    private int g = 0;

	    @Override
	    public Value invoke0() {
		return Name.find(String.format("$g%d", ++g));
	    }
	},

        new Primitive.Prim2("error") {
            @Override
	    public Value invoke2(Value msg, Value help) {
        	cxt.primFail(cxt.string(msg), cxt.string(help));
        	return null;
            }
        },
        
	new Primitive.PrimN("token", 4) {
	    @Override
	    public Value invoke(Value args[], int base) {
		Name tag = cxt.name(args[base+0]);
		Name tok = cxt.name(args[base+1]);
		int p = (int) cxt.number(args[base+2]);
		int rp = (int) cxt.number(args[base+3]);
		Scanner.makeToken(tag, tok, p, rp);
		return Value.nil;
	    }
	},

	new Primitive.Prim1("priority") {
	    @Override
	    public Value invoke1(Value x) {
		Name n = cxt.name(x);
		return Value.makeList(Value.makeNumValue(n.prio),
				      Value.makeNumValue(n.rprio));
	    }
	},

        new Primitive.Prim3("limit") {
            @Override
	    public Value invoke3(Value time, Value steps, Value conses) {
               Evaluator.setLimits((int) cxt.number(time),
				   (int) cxt.number(steps), 
				   (int) cxt.number(conses));
               return Value.nil;
           }
        },

        new Primitive.Prim1("dump") {
            @Override
            public Value invoke1(Value x) {
		try {
		    String fname = cxt.string(x);
		    PrintWriter out = 
			new PrintWriter(new BufferedWriter
					(new FileWriter(fname)));
		    Name.dumpNames(out);
		    return Value.nil;
		} catch (IOException e) {
		    throw new Evaluator.EvalException(e.getMessage());
		}
            }
        }
    };
}
