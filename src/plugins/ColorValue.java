/*
 * ColorValue.java
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

import funbase.Primitive;
import funbase.Primitive.PRIMITIVE;
import funbase.Primitive.CONSTRUCTOR;
import funbase.Primitive.DESCRIPTION;
import funbase.Value;
import funbase.Evaluator;

/** A colour wrapped as a value */
@DESCRIPTION("a colour")
public class ColorValue extends Picture {
    private static final long serialVersionUID = 1L;

    /* The most computationally intensive use of colours is in
     * computing bitmap images with the image() primitive.  So we want
     * to be able to create a colour and find its RGB encoding as
     * efficiently as possible, without needing to create a Native
     * colour object. Thus, the peer instance variable is computed
     * lazily if it is needed. */
    
    /** RGB colour component. */
    @PRIMITIVE
    public final double rpart;

    @PRIMITIVE
    public final double gpart;

    @PRIMITIVE
    public final double bpart;
    
    /** Composite RGB value */
    public final int rgb;
    
    private ColorValue(double r, double g, double b, int comp) {
	super(1.0);
        rpart = r; gpart = g; bpart = b; rgb = comp;
    }

    private static int P = 509;

    /** A direct-mapped cache of colours, at most one per hash */
    private static ColorValue cache[] = new ColorValue[P];

    private static ColorValue cachedInstance(double r, double g, double b, 
                                             int rgb) {
        // It's tempting but wrong to use the rgb value as the hash.  Doing
        // so means that putting a colour into an image and getting it out
        // again creates two slightly different colours with the same hash,
        // and we want to be able to cache both of them.  It's also important 
        // to check that the reddish colours created in the Life example 
        // don't clash with each other: choose P by experiment.

        long rx = Double.doubleToLongBits(r);
        long bx = Double.doubleToLongBits(g);
        long gx = Double.doubleToLongBits(b);
        long h0 = rx ^ (gx << 16) ^ (gx >>> 48) ^ (bx << 32) ^ (bx >>> 32);
        int h = (int) ((h0 & 0x7fffffffffffffffL) % P);
        ColorValue p = cache[h];

        if (p == null || p.rpart != r || p.gpart != g || p.bpart != b) {
            // System.out.printf("Color %f %f %f %d %d\n", r, g, b, rgb, h);
            p = new ColorValue(r, g, b, rgb);
            cache[h] = p;
        }

        return p;
    }

    public static ColorValue instance(double rr, double gg, double bb) {
	double r = cutoff(rr), g = cutoff(gg), b = cutoff(bb);
	int rx = (int) Math.round(255.0 * r); 
	int gx = (int) Math.round(255.0 * g);
	int bx = (int) Math.round(255.0 * b);
	int rgb = (rx << 16) + (gx << 8) + bx;
        return cachedInstance(r, g, b, rgb);
    }
    
    public static final ColorValue black = instance(0.0, 0.0, 0.0);
    public static final ColorValue white = instance(1.0, 1.0, 1.0);

    public static ColorValue getRGB(int rgb) {
        double rpart = ((rgb >> 16) & 0xff)/255.0;
	double gpart = ((rgb >> 8) & 0xff)/255.0;
	double bpart = (rgb & 0xff)/255.0;
        return cachedInstance(rpart, gpart, bpart, rgb & 0xffffff);
    }

    public static ColorValue getGrey(double g) {
	return instance(g, g, g);
    }

    /** Compute a colour from Hue, Saturation and Brightness values,
     *  according to the traditional scheme. */
    @PRIMITIVE("hsv")
    public static ColorValue getHSB(double h, double s, double b) {
	double red, green, blue;

	h -= Math.floor(h);
	h *= 6.0;
	int sextant = (int) Math.floor(h);
	double frac = h - sextant;

	double p = b * (1.0 - s);
	double q = b * (1.0 - s * frac);
	double t = b * (1.0 - s * (1.0 - frac));

	switch (sextant) {
	    case 0: red = b; green = t; blue = p; break;
	    case 1: red = q; green = b; blue = p; break;
	    case 2: red = p; green = b; blue = t; break;
	    case 3: red = p; green = q; blue = b; break;
	    case 4: red = t; green = p; blue = b; break;
	    case 5: red = b; green = p; blue = q; break;
	    default:
		throw new Error("HSB");
	}

	return instance(red, green, blue);
    }

    /** The native colour object corresponding to this colour */
    private Object peer = null;

    /** Compute the corresponding native colour object */
    public Object getNative() { 
	if (peer == null) {
	    Native factory = Native.instance();
	    peer = factory.color(this);
	}

	return peer; 
    }
    
    @Override
    public void printOn(PrintWriter out) {
	out.print("rgb("); Value.printNumber(out, rpart); out.print(", ");
	Value.printNumber(out, gpart); out.print(", ");
	Value.printNumber(out, bpart); out.print(")");
    }
    
    /** Paint the colour as a circular swatch. */
    @Override
    public void paint(int layer, int col, Stylus g, Tran2D t) {
        if (layer == FILL) {
            g.setTrans(t);
            g.fillOval(new Vec2D(0.5, 0.5), 0.48, 0.48, this);
        }
    }

    @Override
    public boolean equals(Object a) {
        if (! (a instanceof ColorValue)) return false;
        ColorValue acolor = (ColorValue) a;
        return (rpart == acolor.rpart && gpart == acolor.gpart 
                && bpart == acolor.bpart);
    }

    @Override
    public int hashCode() {
        return rgb;
    }

    /** Truncate a double argument to the range [0.0, 1.0]. */
    public static double cutoff(double arg) {
	if (arg < 0.0)
	    return 0.0;
	else if (arg > 1.0)
	    return 1.0;
	else 
	    return arg;
    }
    
    /** Create a colour from RGB values in the range [0, 1] */
    @PRIMITIVE
    @CONSTRUCTOR(ColorValue.class)
    public static class RgbPrim extends Primitive.Prim3 {
        public RgbPrim() { super("rgb"); }

	@Override
	public Value apply3(Value rpart, Value gpart, Value bpart) {
	    return instance(number(rpart), number(gpart), number(bpart));
	}
	    
	private Value args[] = new Value[3];

	@Override
	public Value[] pattMatch(int nargs, Value obj) {
	    if (nargs != 3) Evaluator.err_patnargs(name);
	    
	    if (! (obj instanceof ColorValue)) return null;

	    ColorValue v = (ColorValue) obj;
	    args[0] = Value.number(v.rpart);
	    args[1] = Value.number(v.gpart);
	    args[2] = Value.number(v.bpart);
	    return args;
	}
    }
}
