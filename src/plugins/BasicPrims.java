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
    @PRIMITIVE("=")
    public static boolean equal(Value x, Value y) { return x.equals(y); }
    
    @PRIMITIVE("<>")
    public static boolean unequal(Value x, Value y) { return ! x.equals(y); }
	
    @PRIMITIVE("+")
    public static double plus(double x, double y) { return x + y; }

    @PRIMITIVE("-")
    public static double minus(double x, double y) { return x - y; }

    @PRIMITIVE("*")
    public static double times(double x, double y) { return x * y; }

    @PRIMITIVE("/")
    public static double divide(double x, double y) {
	if (y == 0.0) Evaluator.err_divzero();
	return x / y;
    }
	
    @PRIMITIVE("~")
    public static double uminus(double x) { return - x; };

    @PRIMITIVE("<")
    public static boolean less(double x, double y) { return (x < y); }

    @PRIMITIVE("<=")
    public static boolean lesseq(double x, double y) { return (x <= y); }

    @PRIMITIVE(">")
    public static boolean greater(double x, double y) { return (x > y); }

    @PRIMITIVE(">=")
    public static boolean greatereq(double x, double y) { return (x >= y); }

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
