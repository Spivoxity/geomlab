/*
 * GeomLab.java
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

import funbase.Name;
import funbase.Value;
import funbase.Evaluator;
import geomlab.Command.CommandException;
import plugins.Stylus.Drawable;
import plugins.Native;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.UIManager.*;

/** The main application class for GeomLab.
 * 
 *  The Geomlab application is made up of three parts: (i) an interpreter for
 *  the core of the GeomLab language, (iii) a graphical interface where you can
 *  enter GeomLab expressions and have them submitted to the interpreter,
 *  and (iii) a collection of classes that implement primitives that are
 *  included in the initial environment of the interpreter.  These pieces are
 *  quite independent of each other: for example, the interpreter knows
 *  nothing about the data type of pictures that's implemented by the
 *  Picture class.  The GUI knows how to display pictures that satisfy the
 *  Drawable interface (as instances of Picture do), but does not
 *  know any details of how pictures are made up.
 */
public class GeomLab extends GeomBase {
    @SuppressWarnings("unused")
    private static final String svnid =
	"$Id: GeomLab.java 635 2012-06-20 08:33:15Z mike $";

    public final AppFrame frame = new AppFrame();
    
    public boolean antialiased = false;
    
    public GeomLab() {
	setLog(frame.getLogWriter());
	
	frame.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		evaluate();
	    }
	});
    }
    
    /** Update the picture display */
    protected void displayUpdate(Value val) {
	if (val != null && (val instanceof Drawable))
	    frame.setPicture((Drawable) val);
	else
	    frame.setPicture(null);
    }
    
    public void loadFileCommand(File file) {
	log.println();
	loadFromFile(file, true);
    }

    /** Subclass of ErrReporter that displays location of syntax errors. */
    public class ShowError extends ErrReporter {
	@Override
	public void syntaxError(funbase.Scanner.SyntaxException e) {
	    evalError("Oops: ", e.shortMessage(), e.getErrtag());
	    frame.showError(e.getStart(), e.getEnd());	
	}
    }

    /** Command -- evaluate expressions */
    public void evaluate() {
	/* This runs the commands in another thread to allow
	 * display updates during evaluation */
	
	String command = frame.input.getText();
	
	frame.setEnabled(false);
	frame.results.setText("");

	// log.println(command);
	// log.flush();
	
	final StringReader reader = new StringReader(command);
	
	Thread evalThread = new Thread() {
	    @Override
	    public void run() {
		eval_loop(reader, true, new ShowError());
		EventQueue.invokeLater(new Runnable() {
		    @Override
		    public void run() {
			frame.spinner.stop();
			displayUpdate(last_val);
			// if (done) frame.input.setText("");
			frame.setEnabled(true);
		    }
		});
	    }
	};
	
	frame.spinner.start();
	evalThread.start();
    }

    /** Command -- paste a list of global names into the log */
    public void listNames() {
	java.util.List<String> names = Name.getGlobalNames();
	
	log.println();
	if (names.size() == 0)
	    log.print("(no global definitions)");
	else {
	    final int MAX = 40;
	    String s = names.get(0);
	    int w = s.length(); 
	    log.print(s);
	    for (int i = 1; i < names.size(); i++) {
		s = names.get(i);
		if (w + s.length() + 2 < MAX) {
		    w += 2; log.print(", ");
		} else {
		    w = 0; log.println(",");
		}
		w += s.length();
		log.print(s);
	    }
	}
	log.println();
	log.flush();
    }

    /** Command -- paste the defining text for a name into the input area */
    public void findDefinition() {
	String x = frame.input.getText();
	if (x.equals("")) return;
	
	Name name = Name.find(x);
	String def = name.getDeftext();
	if (def == null) {
	    frame.input.setText("\"No definition found\"");
	    return;
	}
	
	frame.input.setText(def);
    }
    

    public boolean isAntialiased() {
        return antialiased;
    }

    public void setAntialiased(boolean antialiased) {
        this.antialiased = antialiased;
        frame.setAntialiased(antialiased);
        HelpFrame.setAntialiased(antialiased);
    }

    private static Font fontResource = null;

    private float fontSize = 12.0f;

    public void fontScale(float s) {
	fontSize *= s;
	loadFontResource();
    }

    private void loadFontResource() {
        String name = "DejaVuSansMono.ttf";
        
        if (fontResource == null) {
            ClassLoader loader = AppFrame.class.getClassLoader();
            InputStream stream = loader.getResourceAsStream(name);
        
            if (stream != null) {
        	try {
        	    fontResource = Font.createFont(Font.TRUETYPE_FONT, stream);
        	}
        	catch (IOException e) { /* Ignore */ } 
        	catch (FontFormatException e) { /* Keep calm */ }
        	finally { 
        	    try { stream.close(); } 
        	    catch (IOException e) { /* Carry on */ }
        	}
            }
        }
            	
    	if (fontResource != null)
    	    setFont(fontResource.deriveFont(fontSize));
    	else
    	    setFont(new Font("Default", Font.PLAIN, Math.round(fontSize)));
    }
    
    private void setFont(Font font) {
	frame.setFont(font);
    }

    @Override
    public String getEditText() {
	return frame.input.getText();
    }

    @Override
    public void setEditText(String text) {
	frame.input.setText(text);
	frame.input.clearUndo();
    }

    public static void main(String args[]) {
	// Under Java Web Start, the default security manager doesn't allow
	// creation of new class loaders.
	System.setSecurityManager(null);

	if (System.getProperty("mrj.version") != null) {
	    // Use Mac menu bar
	    System.setProperty("apple.laf.useScreenMenuBar", "true");
	    System.setProperty(
                "com.apple.mrj.application.apple.menu.about.name", "GeomLab");
	} else {
	    // Try for the Nimbus look and feel.
	    try {
		for (LookAndFeelInfo info : 
			 UIManager.getInstalledLookAndFeels()) {
		    if (info.getName().equals("Nimbus")) {
			UIManager.setLookAndFeel(info.getClassName());
			break;
		    }
		}
	    } catch (Exception e) {
		// Stick with default L&F
	    }
	}
	
	Native.register(new AWTFactory());
	GeomLab app = new GeomLab();
	GeomBase.registerApp(app);
	app.loadFontResource();
	app.setAntialiased(true);
	app.frame.setJMenuBar(Command.makeAppMenuBar(app));
	app.frame.pack();
	app.frame.setVisible(true);
	app.frame.input.requestFocusInWindow();
	app.logWrite("Welcome to GeomLab");
	
	funbase.FunCode.install(
	  // new funbase.Interp()
	  new funjit.TofuTranslator(new funjit.InlineTranslator())
	);

	String image = System.getProperty("jnlp.session", "geomlab.gls");
	try {
	    Session.loadResource(image);
	    Session.installPlugin(Command.class);
	}
	catch (CommandException e) {
	    app.errorMessage(e.getMessage(), e.getErrtag());
	}
	
	for (String f : args)
	    app.loadFromFile(new File(f), false);

	Name init = Name.find("_init");
	if (init.glodef != null) {
	    Evaluator.apply(init.glodef, new Value[0]);
	}

	app.log.flush();
    }
}
