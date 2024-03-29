/*
 * ByteClassLoader.java
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

import funbase.Evaluator;

import java.lang.reflect.Constructor;

class ByteClassLoader extends ClassLoader {
    private String name;

    private ByteClassLoader(String name) { 
	/* Don't fall back on the system class loader, but instead use
	   the class loader that loaded us: this is needed for Web Start */
	super(ByteClassLoader.class.getClassLoader());
	this.name = name;
    }

    public Class<?> defineClass(byte[] b) {
	return defineClass(name, b, 0, b.length);
    }

    /*
    @Override 
    public void finalize() {
	if (Evaluator.debug > 2)
	    System.out.printf("Discarding class %s\n", name);
    }
    */

    public static Object instantiate(String name, byte code[]) {
	ByteClassLoader loader = new ByteClassLoader(name);
	Class<?> cl = loader.defineClass(code);

	try {
            Constructor<?> c = cl.getDeclaredConstructor();
            return c.newInstance();
	}
	catch (Exception e) {
	    throw new Error(e);
	}
    }
}
