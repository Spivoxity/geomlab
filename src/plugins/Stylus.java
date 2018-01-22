/*
 * Stylus.java
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

import funbase.Value;
import funbase.Primitive.DESCRIPTION;

/** An abstract drawing tablet on which a picture can be drawn */
public abstract class Stylus {
    private final double slider;
    private ColorValue palette[] = null;
    protected Tran2D trans;

    public Stylus(double slider) {
        this.slider = slider;
    }

    /** Set the transform for future drawing operations */
    public void setTrans(Tran2D trans) { this.trans = trans; }

    /** Set the stroke width for future drawing operations */
    public abstract void setStroke(double width);

    /** Draw a polygon in black */
    public abstract void drawStroke(Vec2D stroke[]);
    
    /** Fill a polygonal outline */
    public abstract void fillOutline(Vec2D outline[], ColorValue color);
    
    /** Draw a line */
    public abstract void drawLine(Vec2D from, Vec2D to, ColorValue color);

    /** Draw an arc of the unit circle, magnified by xrad in the 
     * x direction and yrad in the y direction.  The arc starts at
     * angle start, measured in degrees counterclockwise from the
     * x axis, and extends to angle start+extent. */
    public abstract void drawArc(Vec2D centre, double xrad, double yrad, 
				 double start, double extent, ColorValue color);

    /** Fill an oval */
    public abstract void fillOval(Vec2D centre, double xrad, double yrad, 
				  ColorValue color);

    /** Draw a raster image */
    public abstract void drawImage(Native.Image image);
    
    /** Get a palette colour */
    public ColorValue paletteColor(int index) {
        if (palette == null)
            palette = TilePicture.makePalette(slider);
        return palette[index & 0x3];
    }

    /** Fill an outline, using the palette for indexed colours */
    public void fillOutline(Vec2D outline[], Object spec, int col) {
	if (spec instanceof ColorValue) {
	    fillOutline(outline, (ColorValue) spec);
	} else if (spec instanceof Integer && col >= 0) {
	    int index = ((Integer) spec).intValue();
	    fillOutline(outline, paletteColor(index + col));
	}
    }

    /** Test if the transform t yields negligibly small results */
    public abstract boolean isTiny(Tran2D t);

    /** Finish drawing */
    public void close() { /* Do nothing */ }

    /* These two methods use the default painting methods that
     * are contained in TilePicture and TurtlePicture, but can be
     * overridden with special-purpose implementations.  This is
     * done in EPSWrite, for example. */

    /** Draw a tile picture */
    public void drawTile(TilePicture tile, int layer, int col) {
	tile.defaultDraw(layer, col, this);
    }

    /** Draw a turtle path */
    public void drawPath(TurtlePicture pic) {
	pic.defaultDraw(this);
    }

    /** A scalable picture that can draw itself with a Stylus */
    @DESCRIPTION("a picture")
    public interface Drawable {
	/** Perform any compute-intensive preparation for painting.
	 * 
	 *  Once a Drawable has been prerendered (typically in a worker
	 *  thread), it must be ready to be painted in a short time from
	 *  the GUI thread.  It may subsequently by prerendered again
	 *  with a different slider value, but it must always remain
	 *  ready for painting. */
	public void prerender(double slider);
	
	/** Compute aspect ratio width/height.
	 * 
	 *  The picture should be prerendered first. */
	public double getAspect();
	
	/** Test whether the picture supports slider interaction. */
	public boolean isInteractive();

	/** Draw the image in the unit square with specified transformation.
	 * 
	 *  The picture should be prerendered first. */
	public void draw(Stylus g, Tran2D t, ColorValue background);

        /** Draw the image in a specified rectangle
         *
         *  The default implementation of drawing in a screen rectangle is
         *  to scale the picture to fit.  But pixmap pictures like
         *  ImagePictures look bad if they are scaled up too much,
         *  so they override this method to add a blank border when
         *  necessary. */
        public void draw(Stylus g, int ww, int hh, ColorValue background);

        /** Render the image
         *
         *  This method is used for saving images on a file.  Like the
         *  draw method, it is implemented specially for ImagePictures. */
        public Native.Image render(int width, int height, 
                                   double slider, ColorValue background);
    }
}
