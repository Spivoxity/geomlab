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
import funbase.Primitive.PRIMITIVE;

/** Basic primitives for handling numbers, booleans and lists */
public class BasicPrims {
    @PRIMITIVE("=")
    public static Value equal(Primitive prim, Value x, Value y) {
	return Value.makeBoolValue(x.equals(y));
    }
    
    @PRIMITIVE("<>")
    public static Value unequal(Primitive prim, Value x, Value y) {
	return Value.makeBoolValue(! x.equals(y));
    }
	
    @PRIMITIVE("+")
    public static Value plus(Primitive prim, Value x, Value y) {
	return Value.makeNumValue(prim.number(x) + prim.number(y));
    }

    @PRIMITIVE("-")
    public static Value minus(Primitive prim, Value x, Value y) {
	return Value.makeNumValue(prim.number(x) - prim.number(y));
    }

    @PRIMITIVE("*")
    public static Value times(Primitive prim, Value x, Value y) {
	return Value.makeNumValue(prim.number(x) * prim.number(y));
    }

    @PRIMITIVE("/")
    public static Value divide(Primitive prim, Value x, Value y) {
	double yy = prim.number(y);
	if (yy == 0.0) 
	    Evaluator.error("division by zero", "#divzero");
	return Value.makeNumValue(prim.number(x) / yy);
    }
	
    @PRIMITIVE
    public static final Primitive uminus = new Primitive.Prim1("_uminus") {
	@Override 
	public Value apply1(Value x) {
	    return Value.makeNumValue(- number(x));
	}

	@Override 
	public String getPName() { return "unary -"; }
    };

    @PRIMITIVE("<")
    public static Value less(Primitive prim, Value x, Value y) {
	return Value.makeBoolValue(prim.number(x) < prim.number(y));
    }

    @PRIMITIVE("<=")
    public static Value lesseq(Primitive prim, Value x, Value y) {
	return Value.makeBoolValue(prim.number(x) <= prim.number(y));
    }

    @PRIMITIVE(">")
    public static Value greater(Primitive prim, Value x, Value y) {
	return Value.makeBoolValue(prim.number(x) > prim.number(y));
    }

    @PRIMITIVE(">=")
    public static Value greatereq(Primitive prim, Value x, Value y) {
	return Value.makeBoolValue(prim.number(x) >= prim.number(y));
    }

    @PRIMITIVE
    public static Value numeric(Primitive prim, Value x) {
	return Value.makeBoolValue(x instanceof Value.NumValue);
    }
	
    @PRIMITIVE("int")
    public static Value intpart(Primitive prim, Value x) {
	return Value.makeNumValue(Math.floor(prim.number(x)));
    }

    @PRIMITIVE
    public static Value sqrt(Primitive prim, Value x) {
	double arg = prim.number(x);
	if (arg < 0.0) 
	    Evaluator.error("taking square root of a negative number", "#sqrt");
	return Value.makeNumValue(Math.sqrt(arg));
    }

    @PRIMITIVE
    public static Value exp(Primitive prim, Value x) {
	return Value.makeNumValue(Math.exp(prim.number(x)));
    }

    @PRIMITIVE
    public static Value sin(Primitive prim, Value x) {
	return Value.makeNumValue(Math.sin(prim.number(x) * Math.PI / 180));
    }
	
    @PRIMITIVE
    public static Value cos(Primitive prim, Value x) {
	return Value.makeNumValue(Math.cos(prim.number(x) * Math.PI / 180));
    }
	
    @PRIMITIVE
    public static Value tan(Primitive prim, Value x) {
	return Value.makeNumValue(Math.sin(prim.number(x) * Math.PI / 180));
    }
	
    @PRIMITIVE
    public static Value atan2(Primitive prim, Value y, Value x) {
	return Value.makeNumValue(Math.atan2(prim.number(y), prim.number(x)) 
				  * 180 / Math.PI);
    }

    @PRIMITIVE
    public static Value random(Primitive prim) {
	return Value.makeNumValue(Math.random());
    }

    @PRIMITIVE
    public static Value name(Primitive prim, Value x) {
	return Name.find(prim.string(x));
    }

    @PRIMITIVE
    public static final Primitive cons = new Primitive.Prim2(":") {
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
    };

    @PRIMITIVE
    public static Value head(Primitive prim, Value x) {
	return prim.head(x);
    }
	
    @PRIMITIVE
    public static Value tail(Primitive prim, Value x) {
	return prim.tail(x);
    }
	
    /* A few system-oriented primitives */

    @PRIMITIVE
    public static Value _glodef(Primitive prim, Value x) {
	Name n = prim.name(x);
	Value v = n.getGlodef();
	if (v == null) Evaluator.err_notdef(n);
	return v;
    }

    @PRIMITIVE
    public static Value _apply(Primitive prim, Value x, Value y) {
	try {
	    Value.FunValue fun = (Value.FunValue) x;
	    Value args[] = prim.toArray(y);
	    return fun.apply(args);
	}
	catch (ClassCastException _) {
	    Evaluator.err_apply();
	    return null;
	}
    }

    @PRIMITIVE
    public static Value _closure(Primitive prim, Value x) {
	FunCode body = prim.cast(FunCode.class, x, "funcode");
	return body.makeClosure(new Value[1]);
    }

    @PRIMITIVE
    public static Value _freeze(Primitive prim) {
	Name.freezeGlobals();
	return Value.nil;
    }

    @PRIMITIVE
    public static Value _frozen(Primitive prim, Value x) {
	Name n = prim.name(x);
	return Value.makeBoolValue(n.isFrozen());
    }

    @PRIMITIVE
    public static Value _spelling(Primitive prim, Value x) {
	Name n = prim.name(x);
	return Value.makeStringValue(n.toString());
    }

    private static int g = 0;

    @PRIMITIVE
    public static Value _gensym(Primitive prim) {
	return Name.find(String.format("$g%d", ++g));
    }

    @PRIMITIVE
    public static Value _error(Primitive prim, Value msg, Value help) {
	Evaluator.error(prim.string(msg), prim.string(help));
	return null;
    }

    @PRIMITIVE
    public static Value _token(Primitive prim, Value tag, Value tok, 
			       Value p, Value rp) {
	Scanner.makeToken(prim.name(tag), prim.name(tok), 
			  (int) prim.number(p), (int) prim.number(rp));
	return Value.nil;
    }

    @PRIMITIVE
    public static Value _priority(Primitive prim, Value x) {
	Name n = prim.name(x);
	return Value.makeList(Value.makeNumValue(n.prio),
			      Value.makeNumValue(n.rprio));
    }

    @PRIMITIVE
    public static Value _limit(Primitive prim, Value time, 
			       Value steps, Value conses) {
	Evaluator.setLimits((int) prim.number(time),
			    (int) prim.number(steps), 
			    (int) prim.number(conses));
	return Value.nil;
    }

    @PRIMITIVE
    public static Value _dump(Primitive prim, Value x) {
	try {
	    String fname = prim.string(x);
	    PrintWriter out = 
		new PrintWriter(new BufferedWriter(new FileWriter(fname)));
	    Name.dumpNames(out);
	    return Value.nil;
	} catch (IOException e) {
	    throw new Evaluator.EvalException(e.getMessage());
	}
    }
}
