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
import funbase.FunCode;
import funbase.Evaluator;
import funbase.Scanner;

/** Basic primitives for handling numbers, booleans and lists */
public class BasicPrims {
    public static final Primitive primitives[] = {
	new Primitive.Prim2("=") {
	    @Override
	    public Value apply2(Value x, Value y) {
		return Value.makeBoolValue(x.equals(y));
	    }
	},
	
	new Primitive.Prim2("<>") {
	    @Override
	    public Value apply2(Value x, Value y) {
		return Value.makeBoolValue(! x.equals(y));
	    }
	},
	
	new Primitive.Prim2("+") {
	    @Override
	    public Value apply2(Value x, Value y) {
		return Value.makeNumValue(number(x) + number(y));
	    }
	},

	new Primitive.Prim2("-") {
	    @Override
	    public Value apply2(Value x, Value y) {
		return Value.makeNumValue(number(x) - number(y));
	    }
	},
	
	new Primitive.Prim2("*") {
	    @Override
	    public Value apply2(Value x, Value y) {
		return Value.makeNumValue(number(x) * number(y));
	    }
	},
	
	new Primitive.Prim2("/") {
	    @Override
	    public Value apply2(Value x, Value y) {
		if (number(y) == 0.0) 
		    Evaluator.error("division by zero", "#divzero");
		return Value.makeNumValue(number(x) / number(y));
	    }
	},
	
	new Primitive.Prim1("uminus") {
	    @Override
	    public Value apply1(Value x) {
		return Value.makeNumValue(- number(x));
	    }
	},
	
	new Primitive.Prim2("<") {
	    @Override
	    public Value apply2(Value x, Value y) {
		return Value.makeBoolValue(number(x) < number(y));
	    }
	},
	
	new Primitive.Prim2("<=") {
	    @Override
	    public Value apply2(Value x, Value y) {
		return Value.makeBoolValue(number(x) <= number(y));
	    }
	},
	
	new Primitive.Prim2(">") {
	    @Override
	    public Value apply2(Value x, Value y) {
		return Value.makeBoolValue(number(x) > number(y));
	    }
	},
	
	new Primitive.Prim2(">=") {
	    @Override
	    public Value apply2(Value x, Value y) {
		return Value.makeBoolValue(number(x) >= number(y));
	    }
	},
	
	new Primitive.Prim1("numeric") {
	    @Override
	    public Value apply1(Value x) {
		return Value.makeBoolValue(x instanceof Value.NumValue);
	    }
	},
	
	new Primitive.Prim1("int") {
	    @Override
	    public Value apply1(Value x) {
		return Value.makeNumValue(Math.floor(number(x)));
	    }
	},
	
	new Primitive.Prim1("sqrt") {
	    @Override
	    public Value apply1(Value x) {
		if (number(x) < 0.0) 
		    Evaluator.error("taking square root of a negative number", 
				     "#sqrt");
		return Value.makeNumValue(Math.sqrt(number(x)));
	    }
	},
	
	new Primitive.Prim1("exp") {
	    @Override
	    public Value apply1(Value x) {
		return Value.makeNumValue(Math.exp(number(x)));
	    }
	},

	new Primitive.Prim1("sin") {
	    @Override
	    public Value apply1(Value x) {
		return Value.makeNumValue
		    (Math.sin(number(x) * Math.PI / 180));
	    }
	},
	
	new Primitive.Prim1("cos") {
	    @Override
	    public Value apply1(Value x) {
		return Value.makeNumValue
		    (Math.cos(number(x) * Math.PI / 180));
	    }
	},
	
	new Primitive.Prim1("tan") {
	    @Override
	    public Value apply1(Value x) {
		return Value.makeNumValue
		    (Math.tan(number(x) * Math.PI / 180));
	    }
	},
	
	new Primitive.Prim2("atan2") {
	    @Override
	    public Value apply2(Value y, Value x) {
		return Value.makeNumValue
		    (Math.atan2(number(y), number(x)) * 180 / Math.PI);
	    }
	},

	new Primitive.Prim0("random") {
	    @Override
	    public Value apply0() {
		return Value.makeNumValue(Math.random());
	    }
	},
	
	new Primitive.Prim1("name") {
	    @Override
	    public Value apply1(Value x) {
		return Name.find(string(x));
	    }
	},

	new Primitive.Prim1("head") {
	    @Override
	    public Value apply1(Value x) { return head(x); }
	},
	
	new Primitive.Prim1("tail") {
	    @Override
	    public Value apply1(Value x) { return tail(x); }
	},
	
	new Primitive.Prim2(":") {
	    @Override
	    public Value apply2(Value hd, Value tl) {
		if (! isCons(tl) && ! tl.equals(Value.nil)) expect("list");
		return Value.cons(hd, tl);
	    }
	    
	    private Value args[] = new Value[2];

	    @Override
	    public Value[] pattMatch(Value obj, int nargs) {
		if (nargs != 2) Evaluator.err_patnargs(name);
		try {
		    Value.ConsValue cell = (Value.ConsValue) obj;
		    args[0] = cell.tail;
		    args[1] = cell.head;
		    return args;
		}
		catch (ClassCastException _) {
		    return null;
		}
	    }
	},

        /* A few system-oriented primitives */

        new Primitive.Prim1("primitive") {
            /* Look up a primitive */
            @Override
	    public Value apply1(Value name) {
        	return Value.makeFunValue(Primitive.find(string(name)));
            }
        },

	FunCode.assemble,

	new Primitive.Prim1("glodef") {
	    @Override
	    public Value apply1(Value x) {
		Name n = name(x);
		Value v = n.getGlodef();
		if (v == null) Evaluator.err_notdef(n);
		return v;
	    }
	},

	new Primitive.Prim2("apply") {
	    @Override
	    public Value apply2(Value x, Value y) {
		try {
		    Value.FunValue fun = (Value.FunValue) x;
		    Value args[] = toArray(y);
		    return fun.apply(args);
		}
		catch (ClassCastException _) {
		    Evaluator.err_apply();
		    return null;
		}
	    }
	},

	new Primitive.Prim1("closure") {
	    @Override
	    public Value apply1(Value x) {
		FunCode body = cast(FunCode.class, x, "funcode");
		return body.makeClosure(new Value[1]);
	    }
	},

        new Primitive.Prim0("freeze") {
            @Override
            public Value apply0() {
        	Name.freezeGlobals();
        	return Value.nil;
            }
        },

	new Primitive.Prim1("frozen") {
	    @Override
	    public Value apply1(Value x) {
		Name n = name(x);
		return Value.makeBoolValue(n.isFrozen());
	    }
	},

	new Primitive.Prim1("spelling") {
	    @Override
	    public Value apply1(Value x) {
		Name n = name(x);
		return Value.makeStringValue(n.toString());
	    }
	},

	new Primitive.Prim0("gensym") {
	    private int g = 0;

	    @Override
	    public Value apply0() {
		return Name.find(String.format("$g%d", ++g));
	    }
	},

        new Primitive.Prim2("error") {
            @Override
	    public Value apply2(Value msg, Value help) {
        	Evaluator.error(string(msg), string(help));
        	return null;
            }
        },
        
	new Primitive.PrimN("token", 4) {
	    @Override
	    public Value apply(Value args[], int base) {
		Name tag = name(args[base+0]);
		Name tok = name(args[base+1]);
		int p = (int) number(args[base+2]);
		int rp = (int) number(args[base+3]);
		Scanner.makeToken(tag, tok, p, rp);
		return Value.nil;
	    }
	},

	new Primitive.Prim1("priority") {
	    @Override
	    public Value apply1(Value x) {
		Name n = name(x);
		return Value.makeList(Value.makeNumValue(n.prio),
				      Value.makeNumValue(n.rprio));
	    }
	},

        new Primitive.Prim3("limit") {
            @Override
	    public Value apply3(Value time, Value steps, Value conses) {
               Evaluator.setLimits((int) number(time),
				   (int) number(steps), 
				   (int) number(conses));
               return Value.nil;
           }
        },

        new Primitive.Prim1("dump") {
            @Override
            public Value apply1(Value x) {
		try {
		    String fname = string(x);
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
