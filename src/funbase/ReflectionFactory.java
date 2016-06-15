/*
 * ReflectionFactory.java
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

package funbase;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.lang.annotation.*;

import funbase.Evaluator.*;
import funbase.Value.FunValue;
import funbase.Value.WrongKindException;

public class ReflectionFactory implements Primitive.Factory {
    private static abstract class ReflectionPrimitive extends Primitive.PrimN {
        Class<?> rtype;

        public ReflectionPrimitive(String name, int arity, Class<?> rtype) {
            super(name, arity);
            this.rtype = rtype;
        }

        protected void fill(Object params[], int pbase,
                            Value args[], int abase, Class<?> ptypes[]) {
            for (int i = 0; pbase+i < params.length; i++) {
                Value x = args[abase+i];
                Class<?> t = ptypes[pbase+i];
                Object y;
                if (t == Value.class)
                    y = x;
                else if (t == Double.TYPE)
                    y = number(x);
                else if (t == String.class)
                    y = string(x);
                else if (t == Integer.TYPE)
                    y = (int) Math.round(number(x));
                else if (Value.class.isAssignableFrom(t))
                    y = cast(t, x);
                else
                    throw new Error("fill type " + t + " for " + name);
                params[pbase+i] = y;
            }
        }

        private Value promote(Object o) {
            if (rtype == Value.class)
                return (Value) o;
            else if (rtype == Double.TYPE)
                return Value.NumValue.getInstance((Double) o);
            else if (rtype == Boolean.TYPE)
                return Value.BoolValue.getInstance((Boolean) o);
            else if (rtype == Integer.TYPE)
                return Value.NumValue.getInstance((Integer) o);
            else if (rtype == String.class)
                return Value.StringValue.getInstance((String) o);
            else if (rtype == Void.TYPE)
                return Value.nil;
            else
                throw new Error("promote type " + rtype + " for " + name);
        }

        protected abstract Object invoke(Value args[], int base)
            throws InvocationTargetException, IllegalAccessException;

        public Value applyN(Value args[], int base) {
            try {
                Object o = invoke(args, base);
                return promote(o);
            }
            catch (InvocationTargetException e) {
                Throwable e0 = e.getCause();
                if (e0 instanceof Error)
                    throw (Error) e0;
                else if (e0 instanceof RuntimeException)
                    throw (RuntimeException) e0;
                else
                    throw new Error(e0);
            }
            catch (IllegalAccessException ex) {
                throw new Error("reflection failed for " + name);
            }
        }
    }

    public Primitive reflect(String name, final Method m) {
        final Class<?> ptypes[] = m.getParameterTypes();
        final Class<?> rtype = m.getReturnType();

        if (! Modifier.isStatic(m.getModifiers())) {
            // A method
            return new ReflectionPrimitive(name, ptypes.length+1, rtype) {
                Class<?> cl = m.getDeclaringClass();
                protected Object invoke(Value args[], int base) 
                    throws InvocationTargetException, 
                           IllegalAccessException {
                    Object rcvr = cast(cl, args[base]);
                    Object params[] = new Object[ptypes.length];
                    fill(params, 0, args, base+1, ptypes);
                    return m.invoke(rcvr, params);
                }
            };
        }
        else if (ptypes.length > 0 && ptypes[0] == Primitive.class) {
            // A static method that uses a Primitive object
            return new ReflectionPrimitive(name, ptypes.length-1, rtype) {
                protected Object invoke(Value args[], int base) 
                    throws InvocationTargetException, 
                           IllegalAccessException {
                    Object params[] = new Object[ptypes.length];
                    params[0] = this;
                    fill(params, 1, args, base, ptypes);
                    return m.invoke(null, params);
                }
            };
        }
        else {
            // An ordinary static method
            return new ReflectionPrimitive(name, ptypes.length, rtype) {
                protected Object invoke(Value args[], int base)
                    throws InvocationTargetException, 
                           IllegalAccessException {
                    Object params[] = new Object[ptypes.length];
                    fill(params, 0, args, base, ptypes);
                    return m.invoke(null, params);
                }
            };
        }
    }

    public Primitive select(String name, final Field f) {
        final Class<?> cl = f.getDeclaringClass();
        final Class<?> t = f.getType();

        return new ReflectionPrimitive(name, 1, t) {
            protected Object invoke(Value args[], int base)
                throws IllegalAccessException {
                Object rcvr = cast(cl, args[base]);
                return f.get(rcvr);
            }
        };
    }

    public void constructor(String name, Class<?> c) { }
}

