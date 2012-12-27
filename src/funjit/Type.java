/*
 * Type.java
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

/** A JVM type. 
 * 
 *  This class provides a convenient way of computing the type
 *  descriptor strings that are embedded in class files. */
class Type {
    /** Type descriptor string */
    public final String desc;

    /** If a method type, the size of the arguments in words */
    public final int asize;

    /** The size in words of the value or (for a method) the result */
    public final int size;

    private Type(String desc, int asize, int size) {
	this.desc = desc;
	this.asize = asize;
	this.size = size;
    }

    private Type(String desc, int size) {
	this(desc, 0, size);
    }

    /** A predefined type */
    public static final Type byte_t = new Type("B", 1);
    public static final Type char_t = new Type("C", 1);
    public static final Type double_t = new Type("D", 2);
    public static final Type float_t = new Type("F", 1);
    public static final Type int_t = new Type("I", 1);
    public static final Type long_t = new Type("J", 2);
    public static final Type short_t = new Type("S", 1);
    public static final Type void_t = new Type("V", 0);
    public static final Type bool_t = new Type("Z", 1);

    /** Create a class type */
    public static Type class_t(String cl) {
	return new Type("L" + cl + ";", 1);
    }

    /** Create an array type */
    public static Type array_t(Type t) {
	return new Type("[" + t.desc, 1);
    }

    /** Create a function type (t1, t2, ..., t[n-1]) -> t[n] */
    public static Type func_t(Type... args) {
	int nargs = args.length-1;
	Type result = args[nargs];

	StringBuilder s = new StringBuilder();
	int asize = 0;
	s.append("(");
	for (int i = 0; i < nargs; i++) {
	    s.append(args[i].desc);
	    asize += args[i].size;
	}
	s.append(")");
	s.append(result.desc);

	return new Type(s.toString(), asize, result.size);
    }
}
