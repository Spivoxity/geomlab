/*
 * StringPrims.java
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

public class StringPrims {
    public static final Primitive primitives[] = {	
	new Primitive.Prim2("^") {
	    /** Concatenate two strings */
	    @Override
	    public Value invoke2(Value s1, Value s2) {
		return Value.makeStringValue
		    (cxt.string(s1) + cxt.string(s2));
	    }
	},

	new Primitive.Prim1("explode") {
	    /** Split a string into a list of single-character strings */
	    @Override
	    public Value invoke1(Value x) {
		String s = cxt.string(x);
		Value result = Value.nil;
		for (int i = s.length()-1; i >= 0; i--)
		    result = 
			Value.cons(Value.makeStringValue(s.charAt(i)), result);
		return result;
	    }
	},

	new Primitive.Prim1("implode") {
	    /** Concatenate a list of strings into a single string */
	    @Override
	    public Value invoke1(Value ys) {
		StringBuffer result = new StringBuffer();
		for (Value xs = ys; ! xs.isNilValue(); xs = cxt.tail(xs))
		    result.append(cxt.string(cxt.head(xs)));
		return Value.makeStringValue(result.toString());
	    }
	},

	new Primitive.Prim1("chr") {
	    /** Make character with specified ASCII code */
	    @Override
	    public Value invoke1(Value x) {
		return Value.makeStringValue((char) cxt.number(x));
	    }
	},

	new Primitive.Prim1("ord") {
	    /** Return ASCII code of first character */
	    @Override
	    public Value invoke1(Value x) {
		String s = cxt.string(x);
		return Value.makeNumValue(s.length() == 0 ? 0 : s.charAt(0));
	    }
	}
    };
}
