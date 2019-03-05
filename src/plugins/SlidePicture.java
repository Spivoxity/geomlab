/*
 * SlidePicture.java
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
import funbase.Evaluator;
import funbase.Primitive;
import funbase.Primitive.PRIMITIVE;

/** A picture given as a program dependent on the slider */
public class SlidePicture extends Value implements Drawable {
    private static final long serialVersionUID = 1L;

    /** Function value that is invoked to compute the image */
    private final Value.FunValue render;
    
    /** Slider value from last update */
    private double slider;
    
    /** Cached picture from last update */
    private Picture cache;

    private SlidePicture(Value.FunValue render) { 
        this.render = render; 
    }
	
    @Override
    public double getAspect() {
	return cache.getAspect();
    }

    @Override
    public void draw(Stylus g, int ww, int hh,
                     ColorValue background, boolean force_size) {
        cache.draw(g, ww, hh, background, force_size);
    }

    @Override
    public Native.Image render(int width, int height, 
                               double slider, ColorValue background) {
        return cache.render(width, height, slider, background);
    }

    @Override
    public boolean isInteractive() { return true; }

    @Override
    public void printOn(PrintWriter out) {
	out.print("<picture control>");
    }

    @Override
    public void prerender(double slider) {
	if (cache != null && this.slider == slider) return;

	this.slider = slider;
	Value v = callRender(slider);
	if (v instanceof Picture)
	    cache = (Picture) v;
	else
	    cache = Picture.nullPicture;
    }

    private Value callRender(double slider) {
	return Evaluator.execute(render.subr, NumValue.instance(slider));
    }

    @PRIMITIVE
    public static Value slide(FunValue fun) {
	return new SlidePicture(fun);
    }
}
