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

import funbase.Primitive;
import funbase.Value;
import funbase.Value.*;
import funbase.Name;
import funbase.FunCode;
import funbase.Evaluator;
import funbase.Scanner;
import funbase.Primitive.PRIMITIVE;

/** Basic primitives for handling numbers, booleans and lists */
public class BasicPrims {
    // Arithmitic primitives with special-purpose inliners

    @PRIMITIVE("=")
    public static Value equal(Primitive prim, Value x, Value y) {
	return BoolValue.getInstance(x.equals(y));
    }
    
    @PRIMITIVE("<>")
    public static Value unequal(Primitive prim, Value x, Value y) {
	return BoolValue.getInstance(! x.equals(y));
    }
	
    @PRIMITIVE("+")
    public static Value plus(Primitive prim, Value x, Value y) {
	return NumValue.getInstance(prim.number(x) + prim.number(y));
    }

    @PRIMITIVE("-")
    public static Value minus(Primitive prim, Value x, Value y) {
	return NumValue.getInstance(prim.number(x) - prim.number(y));
    }

    @PRIMITIVE("*")
    public static Value times(Primitive prim, Value x, Value y) {
	return NumValue.getInstance(prim.number(x) * prim.number(y));
    }

    @PRIMITIVE("/")
    public static Value divide(Primitive prim, Value x, Value y) {
	double yy = prim.number(y);
	if (yy == 0.0) Evaluator.error("#divzero");
	return NumValue.getInstance(prim.number(x) / yy);
    }
	
    @PRIMITIVE("~")
    public static Value uminus(Primitive prim, Value x) {
        return NumValue.getInstance(- prim.number(x));
    };

    @PRIMITIVE("<")
    public static Value less(Primitive prim, Value x, Value y) {
	return BoolValue.getInstance(prim.number(x) < prim.number(y));
    }

    @PRIMITIVE("<=")
    public static Value lesseq(Primitive prim, Value x, Value y) {
	return BoolValue.getInstance(prim.number(x) <= prim.number(y));
    }

    @PRIMITIVE(">")
    public static Value greater(Primitive prim, Value x, Value y) {
	return BoolValue.getInstance(prim.number(x) > prim.number(y));
    }

    @PRIMITIVE(">=")
    public static Value greatereq(Primitive prim, Value x, Value y) {
	return BoolValue.getInstance(prim.number(x) >= prim.number(y));
    }

    // Other arithmetic primitives

    @PRIMITIVE
    public static boolean numeric(Value x) {
	return (x instanceof Value.NumValue);
    }
	
    @PRIMITIVE("int")
    public static double intpart(double x) {
	return Math.floor(x);
    }

    @PRIMITIVE
    public static double sqrt(double x) {
	if (x < 0.0) Evaluator.error("#sqrt");
	return Math.sqrt(x);
    }

    @PRIMITIVE
    public static double exp(double x) {
	return Math.exp(x);
    }

    @PRIMITIVE
    public static double sin(double x) {
	return Math.sin(x * Math.PI / 180);
    }
	
    @PRIMITIVE
    public static double cos(double x) {
	return Math.cos(x * Math.PI / 180);
    }
	
    @PRIMITIVE
    public static double tan(double x) {
	return Math.tan(x * Math.PI / 180);
    }
	
    @PRIMITIVE
    public static double atan2(double y, double x) {
	return Math.atan2(y, x) * 180 / Math.PI;
    }

    @PRIMITIVE
    public static double random() {
	return Math.random();
    }
}
