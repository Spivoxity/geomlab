/*
 * ImagePicture.java
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;

import funbase.Primitive;
import funbase.Value;

/** A picture defined by a bitmap. */
public class ImagePicture extends Picture {
    @SuppressWarnings("unused")
    private static final String svnid =
	"$Id: ImagePicture.java 642 2012-07-15 22:31:52Z mike $";
    
    private static final long serialVersionUID = 1L;
    
    /** The bitmap itself, represented in a platform-dependent way. */
    protected transient Native.Image image;
    
    /** Name of a resource from which the image can be reloaded, if any */
    private String resourceName = null;
    
    public ImagePicture(Native.Image image, String resourceName) {
	super((float) image.getWidth() / image.getHeight());
	this.image = image;
	this.resourceName = resourceName;
    }
    
    public ImagePicture(Native.Image image) {
	this(image, null);
    }
    
    public Native.Image getImage() { return image; }

    @Override
    public void printOn(PrintWriter w) {
	w.print("<image>");
    }
    
    /** The paint method from class Picture */
    @Override
    protected void paint(int layer, int col, Stylus g, Tran2D t) {
	if (layer != FILL) return;
	g.setTrans(t);
	g.drawImage(image);
    }
    
    /** The draw method from class Drawable */
    @Override
    public void draw(Stylus g, int ww, int hh, ColorValue background) {
	/* If the only thing being drawn is an image, then
	   don't expand it beyond its natural size. */
	int w = image.getWidth(), h = image.getHeight();
	Tran2D t;

	if (ww <= w || hh <= h)
	    t = Tran2D.translation(0, hh).scale(ww, -hh);
	else
	    t = Tran2D.translation((ww-w)/2, (hh+h)/2).scale(w, -h);
	
	g.setTrans(t);
	g.drawImage(image);
    }

    /** Write a serialized copy of the image.
     * 
     *  The image is write in a simple, platform-indendent format.
     *  The pixels are not writte at all if the resource can be
     *  reconstituted from an application resource. */
    private void writeObject(ObjectOutputStream stream) throws IOException {
	stream.defaultWriteObject();
	
	if (resourceName != null) return;

	int w = image.getWidth(), h = image.getHeight();
	stream.writeInt(image.getWidth());
	stream.writeInt(image.getHeight());
	for (int y = 0; y < h; y++)
	    for (int x = 0; x < w; x++)
		stream.writeInt(image.getRGB(x, y));
    }
    
    /** Reconsitute the image from a serialized stream. */
    private void readObject(ObjectInputStream stream) 
    		throws IOException, ClassNotFoundException {
	stream.defaultReadObject();
	
	if (resourceName != null) {
	    image = loadResource(resourceName);
	    return;
	}
	
	int w = stream.readInt();
	int h = stream.readInt();
	image = Native.factory.image(w, h);
	for (int y = 0; y < h; y++)
	    for (int x = 0; x < w; x++)
		image.setRGB(x, y, stream.readInt());
    }
    
    protected static Native.Image loadResource(String name) 
    							throws IOException {
	ClassLoader loader = ImagePicture.class.getClassLoader();
	InputStream in = loader.getResourceAsStream(name);
	Native.Image image = Native.factory.readImage(in);
	in.close();
	return image;
    }
    
    public static final Primitive primitives[] = {
	new Primitive.Prim1("photo") {
	    @Override
	    public Value invoke1(Value name) {
		try {
		    URL url = new URL(cxt.string(name));
		    InputStream in = url.openStream();
		    Native.Image image = Native.factory.readImage(in);
		    in.close();
		    return new ImagePicture(image);
		}
		catch (IOException e) {
		    cxt.primFail("Image I/O error - " + e);
		    return null;
		}
	    }
	},
	
	new Primitive.Prim1("resource") {
	    @Override
	    public Value invoke1(Value v) {
		try {
		    String name = cxt.string(v);
		    return new ImagePicture(loadResource(name), name);
		}
		catch (IOException e) {
		    cxt.primFail("Image I/O error - " + e);
		    return null;
		}
	    }
	},
	
	new Primitive.Prim3("image") {
	    @Override
	    public Value invoke3(Value width0, Value height0, Value fun) {
		int width = (int) cxt.number(width0);
		int height = (int) cxt.number(height0);
		Native.Image image = Native.factory.image(width, height);
		Value args[] = new Value[2];
		
		for (int x = 0; x < width; x++) {
		    args[0] = Value.makeNumValue(x);
		    for (int y = 0; y < height; y++) {
			args[1] = Value.makeNumValue(y);
			Value v = fun.apply(args, cxt);
			ColorValue col = 
			    cxt.cast(ColorValue.class, v, "colour");
			image.setRGB(x, height-y-1, col.rgb);
		    }
		}

		return new ImagePicture(image);
	    }
	},
	
	new Primitive.PrimN("render", 4) {
	    @Override 
	    public Value invoke(Value args[], int base) {
		Picture pic = picture(args[base+0], cxt);
		int size = (int) Math.round(cxt.number(args[base+1]));
		float slider = (float) cxt.number(args[base+2]);
		float grey = (float) cxt.number(args[base+3]);
		ColorValue bg = ColorValue.getGrey(grey);
		pic.prerender(slider);
		Native.Image image = 
		    Native.factory.render(pic, size, slider, bg);
		return new ImagePicture(image);
	    }
	},

	new Primitive.Prim3("pixel") {
	    @Override
	    public Value invoke3(Value p0, Value x0, Value y0) {
		ImagePicture p = cxt.cast(ImagePicture.class, p0, "image");
		int w = p.image.getWidth(), h = p.image.getHeight();
		int x = (int) Math.round(cxt.number(x0));
		int y = (int) Math.round(cxt.number(y0));
		if (0 <= x && x < w && 0 <= y && y < h) {
		    int rgb = p.image.getRGB(x, h-y-1);
		    return new ColorValue(rgb);
		} else {
		    return ColorValue.white;
		}
	    }
	},
	
	new Primitive.Prim1("width") {
	    @Override
	    public Value invoke1(Value v) {
		ImagePicture p = cxt.cast(ImagePicture.class, v, "image");
		return Value.makeNumValue(p.image.getWidth());
	    }
	},
	
	new Primitive.Prim1("height") {
	    @Override
	    public Value invoke1(Value v) {
		ImagePicture p = cxt.cast(ImagePicture.class, v, "image");
		return Value.makeNumValue(p.image.getHeight());
	    }
	},

	/** Save image as a file */
	new Primitive.Prim3("saveimg") {
	    @Override
	    public Value invoke3(Value v, Value fmt, Value fn) {
		ImagePicture p = cxt.cast(ImagePicture.class, v, "image");
		String format = cxt.string(fmt);
		String fname = cxt.string(fn);

		try {
		    Native.factory.writeImage(p.image, format, new File(fname));
		}
		catch (IOException e) {
		    cxt.primFail("I/O failed: " + e.getMessage());
		}

		return Value.nil;
	    }
	}
    };
}
