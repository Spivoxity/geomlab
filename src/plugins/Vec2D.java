/*
 * Vec2D.java
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

import funbase.Value;
import funbase.Primitive;
import funbase.Primitive.PRIMITIVE;
import funbase.Primitive.DESCRIPTION;
import funbase.Primitive.CONSTRUCTOR;
import funbase.Evaluator;

/** Floating point vectors in 2D */
@DESCRIPTION("a vector")
public class Vec2D extends Value {
    private static final long serialVersionUID = 1L;

    public final double x, y;
    private Object peer;
    
    public Vec2D(double x, double y) {
	this.x = x;
	this.y = y;
    }
    
    public static Vec2D getInstance(double x, double y) {
        return new Vec2D(x, y);
    }

    /** Return the native counterpart of the vector, computed
     *  using the installed Native factory. */
    public Object getNative() {
	if (peer == null) {
	    Native factory = Native.instance();
	    peer = factory.vector(this);
	}

	return peer;
    }

    public double length() {
	return Math.sqrt(x*x + y*y);
    }
    
    public Vec2D add(Vec2D a) {
	return new Vec2D(x + a.x, y + a.y);
    }
    
    public Vec2D add(double dx, double dy) {
	return new Vec2D(x + dx, y + dy);
    }

    public Vec2D scale(double r) {
	return new Vec2D(r*x, r*y);
    }
    
    public Vec2D rotate(double angle) {
	double c = BasicPrims.cos(angle), s = BasicPrims.sin(angle);
	return new Vec2D(c*x-s*y, s*x+c*y);
    }
    
    @Override
    public void printOn(PrintWriter out) {
	out.print("_vector("); 
	Value.printNumber(out, x); out.print(", ");
	Value.printNumber(out, y); out.print(")");
    }

    @PRIMITIVE
    @CONSTRUCTOR(Vec2D.class)
    public static class VecPrim extends Primitive.Prim2 {
        public VecPrim() { super("_vector"); }

	@Override
	public Value apply2(Value x, Value y) {
	    return getInstance(number(x), number(y));
	}

	private Value args[] = new Value[2];

	@Override
	public Value[] pattMatch(int nargs, Value obj) {
	    if (nargs != 2) Evaluator.err_patnargs(name);

	    if (! (obj instanceof Vec2D)) return null;

	    Vec2D v = (Vec2D) obj;
	    args[0] = NumValue.getInstance(v.x);
	    args[1] = NumValue.getInstance(v.y);
	    return args;
	}
    }
}
