/*
 * Tran2D.java
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
import funbase.Evaluator;

/** An affine transformation in 2D, represented by six real coefficients:
 *  [x'] = [m_xx  m_xy] [x] + [m_x]
 *  [y']   [m_yx  m_yy] [y]   [m_y] */
@DESCRIPTION("a transform")
public class Tran2D extends Value {
    private static final long serialVersionUID = 1L;
    
    /* There's a library class for transformations, but it contains lots
     * of convoluted special-case code that isn't helpful for us. */
    
    public final double m_xx, m_yx, m_xy, m_yy, m_x, m_y;
    
    public Tran2D(double m_xx, double m_yx, double m_xy, 
                  double m_yy, double m_x, double m_y) {
	this.m_xx = m_xx; this.m_yx = m_yx; this.m_xy = m_xy; 
        this.m_yy = m_yy; this.m_x = m_x; this.m_y = m_y;
    }

    @PRIMITIVE("_trvec")
    public Vec2D transform(Vec2D a) {
	return new Vec2D(m_xx * a.x + m_xy * a.y + m_x, 
		m_yx * a.x + m_yy * a.y + m_y);
    }
    
    public static Tran2D instance(double m_xx, double m_yx, double m_xy, 
                                  double m_yy, double m_x, double m_y) {
        return new Tran2D(m_xx, m_yx, m_xy, m_yy, m_x, m_y);
    }

    public Vec2D getOrigin() { return new Vec2D(m_x, m_y); }
    public Vec2D getXaxis() { return new Vec2D(m_xx, m_yx); }
    public Vec2D getYaxis() { return new Vec2D(m_xy, m_yy); }
    
    /** Cached AffineTransform object */
    private Object peer = null;

    /** Convert to an AffineTransform object */
    public Object getNative() {
	if (peer == null) {
	    Native factory = Native.instance();
	    peer = factory.transform(this);
	}

	return peer;
    }
    
    /** Compose this transform (on the left) with another one on the right */
    public Tran2D concat(Tran2D other) {
	return compose(this, other);
    }

    /** Compose two transformations */
    @PRIMITIVE("_compose")
    public static Tran2D compose(Tran2D a, Tran2D b) {
	//  [ a_xx  a_xy  a_x ] [ b_xx  b_xy  b_x ]
	//  [ a_yx  a_yy  a_y ] [ b_yx  b_yy  b_y ]
	//  [  0     0     1  ] [  0     0     1  ]
	return new Tran2D(
		a.m_xx * b.m_xx + a.m_xy * b.m_yx,
		a.m_yx * b.m_xx + a.m_yy * b.m_yx,
		a.m_xx * b.m_xy + a.m_xy * b.m_yy,
		a.m_yx * b.m_xy + a.m_yy * b.m_yy,
		a.m_xx * b.m_x + a.m_xy * b.m_y + a.m_x,
		a.m_yx * b.m_x + a.m_yy * b.m_y + a.m_y);
    }
    
    public Tran2D translate(double dx, double dy) {
	return new Tran2D(m_xx, m_yx, m_xy, m_yy,
		m_xx * dx + m_xy * dy + m_x,
		m_yx * dx + m_yy * dy + m_y);
    }
    
    public static Tran2D translation(float dx, float dy) {
	return new Tran2D(1, 0, 0, 1, dx, dy);
    }
    
    public Tran2D scale(double sx, double sy) {
	return new Tran2D(m_xx * sx, m_yx * sx, 
                          m_xy * sy, m_yy * sy, m_x, m_y);
    }
    
    public static Tran2D scaling(double sx, double sy) {
	return new Tran2D(sx, 0, 0, sy, 0, 0);
    }
    
    public Tran2D rotate(float angle) {
	double c = BasicPrims.cos(angle), s = BasicPrims.sin(angle);
        return new Tran2D(m_xx * c + m_xy * s, m_yx * c + m_yy * s,
                          -m_xx * s + m_xy * c, -m_yx * s + m_yy * c,
                          m_x, m_y);
    }
    
    public Tran2D rot90() {
	return new Tran2D(m_xy, m_yy, -m_xx, -m_yx, m_x, m_y);
    }
    
    public static Tran2D rotation(double angle) {
	double c = BasicPrims.cos(angle), s = BasicPrims.sin(angle);
	return new Tran2D(c, s, -s, c, 0, 0);
    }
    
    public static final Tran2D identity = new Tran2D(1, 0, 0, 1, 0, 0);
    
    /* The coordinates are perturbed by 0.001 before rounding so that
     * ambiguous values do not result from repeated halving of the
     * integer size of a window. */

    /** Compute rounded x coordinate of a transformed vector */
    public int scaleX(Vec2D a) {
	return (int) Math.round(m_xx * a.x + m_xy * a.y + m_x + 0.001);
    }
    
    /** Compute rounded y coordinate of a transformed vector */
    public int scaleY(Vec2D a) {
	return (int) Math.round(m_yx * a.x + m_yy * a.y + m_y + 0.001);
    }
    
    public boolean isTiny(double eps) {
	return Math.abs(m_xx) <= eps && Math.abs(m_yx) <= eps 
		|| Math.abs(m_xy) <= eps && Math.abs(m_yy) <= eps;
    }

    @Override
    public void printOn(PrintWriter out) {
	out.print("_transform(");
	Value.printNumber(out, m_xx); out.print(", ");
	Value.printNumber(out, m_yx); out.print(", ");
	Value.printNumber(out, m_xy); out.print(", ");
	Value.printNumber(out, m_yy); out.print(", ");
	Value.printNumber(out, m_x); out.print(", ");
	Value.printNumber(out, m_y); out.print(")");
    }

    @PRIMITIVE
    public static Primitive _transform = new TransPrim();
    
    public static class TransPrim
            extends Primitive.Prim6 implements Primitive.Constructor {
        public TransPrim() { super("_transform"); }

	@Override
	public Value apply6(Value m_xx, Value m_yx, Value m_xy, 
			    Value m_yy, Value m_x, Value m_y) {
            return instance(number(m_xx), number(m_yx), number(m_xy), 
                            number(m_yy), number(m_x), number(m_y));
        }

	public Value invoke(double m_xx, double m_yx, double m_xy, 
			    double m_yy, double m_x, double m_y) {
            return instance(m_xx, m_yx, m_xy, m_yy, m_x, m_y);
        }

	private Value args[] = new Value[6];

	@Override
	public Value[] pattMatch(int nargs, Value obj) {
	    if (nargs != 6) Evaluator.err_patnargs(name);
	    if (! (obj instanceof Tran2D)) return null;
	    Tran2D v = (Tran2D) obj;
	    args[0] = Value.number(v.m_xx); 
	    args[1] = Value.number(v.m_yx);
	    args[2] = Value.number(v.m_xy); 
	    args[3] = Value.number(v.m_yy);
	    args[4] = Value.number(v.m_x); 
	    args[5] = Value.number(v.m_y);
	    return args;
	}
    }
}
