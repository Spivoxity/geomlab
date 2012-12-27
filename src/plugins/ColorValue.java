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
import funbase.Value;
import funbase.ErrContext;

/** A colour wrapped as a value */
public class ColorValue extends Picture {
    @SuppressWarnings("unused")
    private static final String svnid =
	"$Id: ColorValue.java 642 2012-07-15 22:31:52Z mike $";
    
    private static final long serialVersionUID = 1L;

    /* The most computationally intensive use of colours is in
     * computing bitmap images with the image() primitive.  So we want
     * to be able to create a colour and find its RGB encoding as
     * efficiently as possible, without needing to create a Native
     * colour object. Thus, the peer instance variable is computed
     * lazily if it is needed. */
    
    /** RGB colour component. */
    public final double rpart, gpart, bpart;
    
    /** Composite RGB value */
    public final int rgb;
    
    public ColorValue(double rf, double gf, double bf) {
	super(1.0f, false);
	rpart = cutoff(rf); gpart = cutoff(gf); bpart = cutoff(bf);
	int rx = Math.round(255.0f * (float) rpart); 
	int gx = Math.round(255.0f * (float) gpart);
	int bx = Math.round(255.0f * (float) bpart);
	rgb = (rx << 16) + (gx << 8) + bx;
    }
    
    public ColorValue(int rgb) {
	super(1.0f, false);
	this.rgb = rgb;
	rpart = ((rgb >> 16) & 0xff)/255.0;
	gpart = ((rgb >> 8) & 0xff)/255.0;
	bpart = (rgb & 0xff)/255.0;
    }
    
    public static final ColorValue black = new ColorValue(0.0, 0.0, 0.0);
    public static final ColorValue white = new ColorValue(1.0, 1.0, 1.0);

    public static ColorValue getGrey(float g) {
	return new ColorValue(g, g, g);
    }

    /** Provided for invocation from inlined JIT code */
    public static Value getInstance(double rf, double gf, double bf) {
	return new ColorValue(rf, gf, bf);
    }

    /** Compute a colour from Hue, Saturation and Brightness values,
     *  according to the traditional scheme. */
    public static ColorValue getHSB(float h, float s, float b) {
	float red, green, blue;

	h -= Math.floor(h);
	h *= 6.0f;
	int sextant = (int) Math.floor(h);
	float frac = h - sextant;

	float p = b * (1.0f - s);
	float q = b * (1.0f - s * frac);
	float t = b * (1.0f - s * (1.0f - frac));

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

	return new ColorValue(red, green, blue);
    }

    /** The native colour object corresponding to this colour */
    private Object peer = null;

    /** Compute the corresponding native colour object */
    public Object getNative() { 
	if (peer == null) 
	    peer = Native.factory.color(this);
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
	g.setTrans(t);
	g.fillOval(new Vec2D(0.5f, 0.5f), 0.45f, 0.45f, this);
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
    
    public static final Primitive primitives[] =  {
	new Primitive.Prim3("rgb") {
	    /* Create a colour from RGB values in the range [0, 1] */
	    @Override
	    public Value invoke3(Value rpart, Value gpart, Value bpart) {
		return new ColorValue(cxt.number(rpart), cxt.number(gpart), 
				      cxt.number(bpart));
	    }
	    
	    private Value args[] = new Value[3];

	    @Override
	    public Value[] pattMatch(Value obj, int nargs, ErrContext cxt) {
		if (nargs != 3) cxt.err_patnargs(name);

		if (! (obj instanceof ColorValue)) return null;

		ColorValue v = (ColorValue) obj;
		args[0] = Value.makeNumValue(v.rpart);
		args[1] = Value.makeNumValue(v.gpart);
		args[2] = Value.makeNumValue(v.bpart);
		return args;
	    }
	},
	
	new Primitive.Prim1("rpart") {
	    @Override
	    public Value invoke1(Value obj) {
		ColorValue v = cxt.cast(ColorValue.class, obj, "colour");
		return Value.makeNumValue(v.rpart);
	    }
	},
	
	new Primitive.Prim1("gpart") {
	    @Override
	    public Value invoke1(Value obj) {
		ColorValue v = cxt.cast(ColorValue.class, obj, "colour");
		return Value.makeNumValue(v.gpart);
	    }
	},
	
	new Primitive.Prim1("bpart") {
	    @Override
	    public Value invoke1(Value obj) {
		ColorValue v = cxt.cast(ColorValue.class, obj, "colour");
		return Value.makeNumValue(v.bpart);
	    }
	},
	
	new Primitive.Prim3("hsv") {
	    /* Create a colour from HSV values in the range [0, 1] */
	    @Override
	    public Value invoke3(Value h, Value s, Value v) {
		return getHSB((float) cxt.number(h),
			      (float) cutoff(cxt.number(s)),
			      (float) cutoff(cxt.number(v)));
	    }
	}
    };
}
