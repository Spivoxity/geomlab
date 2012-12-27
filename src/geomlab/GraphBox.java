/*
 * GraphBox.java
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

package geomlab;

import java.awt.*;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.*;

import funbase.Evaluator;
import plugins.Stylus;
import plugins.ColorValue;
import plugins.Native;
import geomlab.Command.CommandException;

/** A panel for displaying a Picture object */
public class GraphBox extends JPanel {
    protected Stylus.Drawable picture = null;
    protected boolean antialiased = false;

    protected final JComponent canvas = new JComponent() {
	@Override
	public void paintComponent(Graphics g) {
	    if (picture == null) return;
	    
	    try {
		float aspect = picture.getAspect();
	    
		if (aspect == 0.0) return;

		Dimension dim = getSize();
		int w = dim.width, h = dim.height;
	    
		/* Determine hh <= h and ww <= w so that hh = h or ww = w
		 * and ww/hh ~= aspect */ 
		int ww = w, hh = h;
		if (h * aspect >= w)
		    hh = (int) (w / aspect + 0.5f);
		else
		    ww = (int) (h * aspect + 0.5f);
	    
		Graphics2D g2 = (Graphics2D) g;
		if (antialiased)
		    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
	    
		g2.translate((w - ww)/2, (h - hh)/2);
		plugins.Stylus t = new ScreenStylus(g2, sliderValue());

		picture.draw(t, ww, hh, ColorValue.white);
	    }
	    catch (Throwable e) {
		GeomBase.theApp.evalError("Failure: ",
					  e.toString(), "#failure");
		picture = null;
	    }
	}
    };
    
    private JSlider slider = new JSlider();

    public GraphBox() {
	setLayout(new BorderLayout());
	canvas.setBackground(Color.lightGray);
	canvas.setPreferredSize(new Dimension(400, 400));
	add(canvas, "Center");
	add(slider, "South");
	slider.setVisible(false);
	
	slider.addChangeListener(new ChangeListener() {
	    @Override
	    public void stateChanged(ChangeEvent e) {
		prerender(picture);
	    }
	});
    }
    
    public void setPicture(Stylus.Drawable pic) {
	slider.setVisible(pic != null && pic.isInteractive());
	slider.revalidate();
	prerender(pic);
    }
    
    public boolean isAntialiased() { return antialiased; }
    
    public void setAntialiased(boolean antialiased) {
        this.antialiased = antialiased;
	repaint();
    }
    
    public float sliderValue() { return slider.getValue() / 100.0f; }
    
    private void prerender(Stylus.Drawable pic) {
	try {
	    if (pic != null) pic.prerender(sliderValue());
	}
	catch (Evaluator.EvalException e) {
	    GeomBase.theApp.evalError("Aargh: ", 
		    e.toString(), e.getErrtag());
	    pic = null;
	}
	
	this.picture = pic;
	repaint();
    }

    /** Command -- print the current picture */
    public void print() throws CommandException {
        if (picture == null) 
            throw new CommandException("No picture", "#nopicture");
        
        PrinterJob job = PrinterJob.getPrinterJob();
        if (job == null)
            throw new CommandException("No print job", "#noprint");
        
        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics g, PageFormat fmt, int n) {
        	if (n > 0) return Printable.NO_SUCH_PAGE;
        	double width = fmt.getImageableWidth();
        	double height = fmt.getImageableHeight();
        	double x = fmt.getImageableX(), y = fmt.getImageableY();
        	float aspect = picture.getAspect();
        	
        	if (height * aspect >= width)
        	    height = width / aspect;
        	else
        	    width = height * aspect;
        	
        	Graphics2D g2 = (Graphics2D) g;
        	g2.translate(x, y);
        	Stylus t = new ScreenStylus(g2, sliderValue());
        	picture.draw(t, (int) width, (int) height, ColorValue.white);
        	
        	return Printable.PAGE_EXISTS;
            }
        });
        
        if (! job.printDialog()) return;
        
        try {
            job.print();
        }
        catch (PrinterException e) {
            throw new CommandException("Printing failed", "#printfail");
        }
    }

    public boolean isPicture() { return picture != null; }

    private static final int imageMean = 400;

    public void writePicture(File file) throws IOException {
	float slider = sliderValue();
	picture.prerender(slider);
	Native.Image image = 
	    Native.factory.render(picture, imageMean, slider, ColorValue.white);
	Native.factory.writeImage(image, "png", file);
    }
}   
