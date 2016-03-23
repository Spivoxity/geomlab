/*
 * JitPrimFactory.java
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

import java.lang.reflect.Modifier;

import funbase.Value;
import funbase.Primitive;
import funbase.Primitive.DESCRIPTION;

import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

public class JitPrimFactory implements Primitive.Factory {
    String name;
    int arity;
    FunctionClass code;

    private void beginPrim(String fname, String mname, int arity) {
        this.name = fname;
        this.arity = arity;

        code = new FunctionClass(fname, mname, arity, 
                                 primsmall_cl, primlarge_cl);
    }

    public void getArg(int n) {
        if (arity < JitTranslator.MANY)
            code.gen(ALOAD, n+1);
        else {
            code.gen(ALOAD, 1);
            code.gen(CONST, n);
            code.gen(AALOAD);
        }
    }

    public void castArg(String cl, int n) {
        code.gen(ALOAD, 0);
        code.gen(CLASS, cl);
        getArg(n);
        code.gen(INVOKEVIRTUAL, primitive_cl, "cast", fun_CV_O_t);
        code.gen(CHECKCAST, cl);
    }

    public void accessArg(String project, int n, Type proj_t) {
        code.gen(ALOAD, 0);
        getArg(n);
        code.gen(INVOKEVIRTUAL, primitive_cl, project, proj_t);
    }

    private void primArgs(int pbase, int abase, Species pkinds[]) {
        for (int i = 0; i < pkinds.length-pbase; i++) {
            Species k = pkinds[pbase+i];
            k.primarg(abase+i, this);
        }
    }

    private Species[] getParamKinds(java.lang.reflect.Method m) {
        Class<?> ptypes[] = m.getParameterTypes();
        Species pkinds[] = new Species[ptypes.length];
        for (int i = 0; i < ptypes.length; i++) 
            pkinds[i] = Species.find(ptypes[i]);
        return pkinds;
    }

    private Species getReturnKind(java.lang.reflect.Method m) {
        return Species.find(m.getReturnType());
    }

    private java.lang.reflect.Method findFactoryMethod(Class<?> cl) {
        for (java.lang.reflect.Method m : cl.getDeclaredMethods()) {
            String name = m.getName();
            if (name.equals("getInstance"))
                return m;
        }
        throw new Error("findFactoryMethod " + cl);
    }

    /** Make a primitive from a method or class method. */
    public Primitive reflect(String name, java.lang.reflect.Method m) {
        Class<?> cl = m.getDeclaringClass();
        Species pkinds[] = getParamKinds(m);
        Species rkind = getReturnKind(m);
        int nparams = pkinds.length;
        Type mtype = Species.methType(pkinds, rkind);
        String clstring = Type.className(cl);

        if (! Modifier.isStatic(m.getModifiers())) {
            // A dynamic method
            noteMethod(name, cl, m.getName(), false, pkinds, rkind);
            beginPrim(name, m.getName(), nparams+1);
            castArg(clstring, 0);
            primArgs(0, 1, pkinds);
            code.gen(INVOKEVIRTUAL, clstring, m.getName(), mtype);
        }
        else if (nparams > 0 && pkinds[0] == Species.PRIMITIVE) {
            // A static method that uses a Primitive object
            beginPrim(name, m.getName(), nparams-1);
            code.gen(ALOAD, 0); // this
            primArgs(1, 0, pkinds);
            code.gen(INVOKESTATIC, clstring, m.getName(), mtype);
        }
        else {
            // An ordinary static method
            noteMethod(name, cl, m.getName(), true, pkinds, rkind);
            beginPrim(name, m.getName(), nparams);
            primArgs(0, 0, pkinds);
            code.gen(INVOKESTATIC, clstring, m.getName(), mtype);
        }
        code.widen(rkind);
        code.gen(ARETURN);
        code.finish();

	byte binary[] = code.toByteArray();
	return (Primitive) ByteClassLoader.instantiate(code.name, binary);
    }

    /** Make a primitive that accesses an instance variable. */
    public Primitive select(String name, java.lang.reflect.Field f) {
        Species cl = Species.find(f.getDeclaringClass());
        Species rkind = Species.find(f.getType());

        noteSelector(name, cl, f.getName(), rkind);

        beginPrim(name, f.getName(), 1);
        castArg(cl.clname, 0);
        code.gen(GETFIELD, cl.clname, f.getName(), rkind.type);
        code.widen(rkind);
        code.gen(ARETURN);
        code.finish();

	byte binary[] = code.toByteArray();
	return (Primitive) ByteClassLoader.instantiate(code.name, binary);
    }

    @Override
    public void constructor(String name, Class<?> cl) { 
        java.lang.reflect.Method m = findFactoryMethod(cl);
        Species pkinds[] = getParamKinds(m);
        Species rkind = getReturnKind(m);
        noteMethod(name, cl, m.getName(), true, pkinds, rkind);
    }

    // Hooks for subclasses
    protected void noteMethod(String name, Class<?> cl, String mname,
                              boolean stat, Species pkinds[], 
                              Species rkind) { }

    protected void noteSelector(String name, Species cl, String fname, 
                                Species rkind) { }
}



