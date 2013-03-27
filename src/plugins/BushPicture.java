/*
 * BushPicture.java
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

import java.util.Stack;

import funbase.Primitive;
import funbase.Value;

/** Fractal plants */
public class BushPicture extends Picture {
    private static final long serialVersionUID = 1L;
    
    protected static float linewidth = 2.0f;
    protected static float alpha = 10.0f, theta = 20.0f; 
    private static Tran2D rot, invrot;
    protected static ColorValue palette[];
    
    static { setColours(20, 0.3f, 0.5f, 0.7f); }

    private String commands;
    protected final float xmin, xmax, ymin, ymax;
    
    public BushPicture(String commands) {
	super(calcAspect(commands), true);
	this.commands = commands;
	xmin = _xmin; xmax = _xmax; 
	ymin = _ymin; ymax = _ymax;
    }

    /* A few global variables save a lot of mess.  But yuck, nevertheless. */
    protected static float _xmin, _xmax, _ymin, _ymax;
    
    private static float calcAspect(String commands) {
	_xmin = _xmax = _ymin = _ymax = 0.0f;
	
	for (int j = 0; j <= 10; j++) {
	    setAngles(j / 10.0f);
	    interp(commands, new View() {
		@Override
		public void move(Vec2D pos, int col, boolean draw) {
		    _xmin = Math.min(_xmin, pos.x);
		    _xmax = Math.max(_xmax, pos.x);
		    _ymin = Math.min(_ymin, pos.y);
		    _ymax = Math.max(_ymax, pos.y);
		}
	    });
	}
	
	_xmin -= 1.0f; _xmax += 1.0f; _ymin -= 1.0f; _ymax += 1.0f;
	return (_xmax - _xmin)/(_ymax - _ymin);
    }
    
    @Override
    public void prerender(float slider) {
	setAngles(slider);
    }
    
    @Override
    protected void paint(int layer, int c, final Stylus g, final Tran2D t) { 
	if (layer != DRAW) return;
	
	g.setStroke(linewidth);
	g.setTrans(t);

	interp(commands, new View() {
	    private Vec2D oldpos = new Vec2D(0.0f, 0.0f);
	    
	    @Override
	    public void move(Vec2D pos, int col, boolean draw) {
		Vec2D newpos = 
		    new Vec2D((pos.x-xmin)/(xmax-xmin),
			    (pos.y-ymin)/(ymax-ymin));
			
		if (draw)
		    g.drawLine(oldpos, newpos, palette[col]);
		
		oldpos = newpos;
	    }
	});
    }
    
    private static void interp(String commands, View view) {
	Vec2D pos = new Vec2D(0.0f, 0.0f), dir = new Vec2D(0.0f, 1.0f);
	int col = 0;
	Stack<Vec2D> pstack = new Stack<Vec2D>(), dstack = new Stack<Vec2D>();
	Stack<Integer> hstack = new Stack<Integer>();
	
	view.move(pos, col, false);
	
	for (int i = 0; i < commands.length(); i++) {
	    char cmd = commands.charAt(i); 
	    switch (cmd) {
	    case 'F':
	    case 'f':
		pos = pos.add(dir);
		view.move(pos, col, (cmd == 'F'));
		break;
	    case '+':
		dir = rot.transform(dir);
		break;
	    case '-':
		dir = invrot.transform(dir);
		break;
	    case 'C':
		col = (col+1) % palette.length;
		break;
	    case 'c':
		col = (col+palette.length-1) % palette.length;
		break;
	    case '<':
		dir = dir.scale(0.9f);
		break;
	    case '>':
		dir = dir.scale(1/0.9f);
		break;
	    case '[':
		pstack.push(pos); 
		dstack.push(dir);
		hstack.push(col);
		break;
	    case ']':
		if (pstack.size() > 0) {
		    pos = pstack.pop();
		    dir = dstack.pop();
		    col = hstack.pop();
		    view.move(pos, col, false);
		}
		break;
	    default:
		// skip
	    }
	}
    }
    
    private interface View {
	public void move(Vec2D pos, int col, boolean draw);
    }
    
    /** Set the angles used for + and - commands */
    public static void setAngles(float t) {
	rot = Tran2D.rotation(alpha - (2 * t - 1.0f) * theta);
	invrot = Tran2D.rotation(- alpha - (2 * t - 1.0f) * theta);
    }
    
    /** Set the palette of colours accessed by C and c commands */
    public static void setColours(int ncols, 
	    float inithue, float sat, float val) {
	palette = new ColorValue[ncols];
	for (int i = 0; i < ncols; i++)
	    palette[i] =
		ColorValue.getHSB(inithue + (float) i / ncols, sat, val);
    }
    
    public static final Primitive primitives[] = {
	new Primitive.Prim1("bush") {
	    /* Create a fractal picture from a string of commands */
	    @Override
	    public Value apply1(Value x) {
		return new BushPicture(string(x));
	    }	    
	},
	
	new Primitive.PrimN("bushparams", 7) {
	    /* Set parameters used to interpret commands */
	    @Override
	    public Value applyN(Value args[], int base) {
		linewidth = (float) number(args[base+0]);
		alpha = (float) number(args[base+1]);
		theta = (float) number(args[base+2]);
		setColours((int) number(args[base+3]),
			(float) number(args[base+4]), 
			(float) number(args[base+5]), 
			(float) number(args[base+6]));
		return Value.nil;
	    }
	}
    };
}
