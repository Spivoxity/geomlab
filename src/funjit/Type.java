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

    /** If a method type, the number of arguments */
    public final int arity;

    /** If a method type, the size of the arguments in words */
    public final int asize;

    /** The size in words of the value or (for a method) the result */
    public final int size;

    private Type(String desc, int arity, int asize, int size) {
	this.desc = desc;
        this.arity = arity;
	this.asize = asize;
	this.size = size;
    }

    private Type(String desc, int size) {
	this(desc, 0, 0, size);
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

	return new Type(s.toString(), nargs, asize, result.size);
    }

    public final static String
	object_cl = "java/lang/Object",
        class_cl = "java/lang/Class",
	string_cl = "java/lang/String",
        math_cl = "java/lang/Math",
	classcast_cl = "java/lang/ClassCastException",
	name_cl = "funbase/Name",
	value_cl = "funbase/Value",
	boolval_cl = "funbase/Value$Boolean",
        stringval_cl = "funbase/Value$StringVal",
	consval_cl = "funbase/Value$Cons",
        pairval_cl = "funbase/Value$Pair",
	wrongkind_cl = "funbase/Value$WrongKindException",
	function_cl = "funbase/Function",
	funcode_cl = "funbase/FunCode",
	jitfun_cl = "funjit/JitFunction",
	jitlarge_cl = "funjit/JitFunction$FuncN",
	jitsmall_cl = "funjit/JitFunction$Func",
	primitive_cl = "funbase/Primitive",
	primlarge_cl = "funjit/JitFunction$PrimN",
	primsmall_cl = "funbase/Primitive$Prim",
        constr_cl = "funbase/Primitive$Constructor",
	evaluator_cl = "funbase/Evaluator";

    public static String className(Class<?> cl) {
        String name = cl.getName();
        return name.replace('.', '/');
    }

    public final static Type
	object_t = class_t(object_cl),
	string_t = class_t(string_cl),
	name_t = class_t(name_cl),
	value_t = class_t(value_cl),
	valarray_t = array_t(value_t),
	function_t = class_t(function_cl),
	prim_t = class_t(primitive_cl),
        class_t = class_t(class_cl),
        consval_t = class_t(consval_cl);
    
    public final static Type
        fun_t = func_t(void_t),
	fun__B_t = func_t(bool_t),
	fun_A_V_t = func_t(valarray_t, value_t),
	fun_AII_t = func_t(valarray_t, int_t, int_t, void_t),
        fun_CV_O_t = func_t(class_t, value_t, object_t),
        fun_D_L_t = func_t(double_t, long_t),
	fun_IV_A_t = func_t(int_t, value_t, valarray_t),
        fun_I_V_t = func_t(int_t, value_t),
	fun_N_t = func_t(name_t, void_t),
	fun_S_t = func_t(string_t, void_t),
        fun_SC_t = func_t(string_t, class_t, void_t),
	fun_SI_t = func_t(string_t, int_t, void_t),
	fun_SII_t = func_t(string_t, int_t, int_t, void_t),
        fun_SS_t = func_t(string_t, string_t, void_t),
        fun_V_B_t = func_t(value_t, bool_t),
	fun_V_V_t = func_t(value_t, value_t),
        fun_VV_C_t = func_t(value_t, value_t, consval_t),
	fun_O_B_t = func_t(object_t, bool_t),
	fun_VS_t = func_t(value_t, string_t, void_t);

    public final static Type applyn_t[] = {
	func_t(value_t),
	func_t(value_t, value_t), 
	func_t(value_t, value_t, value_t),
	func_t(value_t, value_t, value_t, value_t),
	func_t(value_t, value_t, value_t, value_t, value_t),
	func_t(value_t, value_t, value_t, value_t, value_t, value_t),
	func_t(value_t, value_t, value_t, value_t, value_t, value_t, value_t)
    };

    public final static Type failn_t[] = {
	func_t(void_t),
	func_t(value_t, void_t),
	func_t(value_t, value_t, void_t),
	func_t(value_t, value_t, value_t, void_t),
	func_t(value_t, value_t, value_t, value_t, void_t),
	func_t(value_t, value_t, value_t, value_t, value_t, void_t),
	func_t(value_t, value_t, value_t, value_t, value_t, value_t, void_t)
    };

    public final static Type
        apply_t = func_t(valarray_t, value_t),
        applyN_t = func_t(valarray_t, int_t, value_t);
}
