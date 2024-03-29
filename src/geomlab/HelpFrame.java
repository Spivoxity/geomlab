/*
 * HelpFrame.java
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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

/** A mini-browser for help texts */
public class HelpFrame extends JFrame {
    private JEditorPane browser = new JEditorPane();

    private HelpFrame() {
	super("GeomLab help");
	setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	setSize(500, 500);
	setLocation(100, 100);
	
	Container content = getContentPane();
	
	JToolBar toolbar = new JToolBar();
	toolbar.add(new AbstractAction("Back") {
	    @Override
	    public void actionPerformed(ActionEvent e) { goBack(); }
	});
	toolbar.add(new AbstractAction("Forward") {
	    @Override
	    public void actionPerformed(ActionEvent e) { goForward(); }
	});
	toolbar.add(new AbstractAction("Contents") {
	    @Override
	    public void actionPerformed(ActionEvent e) { goHome(); }
	});
	content.add(toolbar, "North");
	
	browser.setEditable(false);
	content.add(new JScrollPane(browser), "Center");
	
	browser.addHyperlinkListener(new HyperlinkListener() {
	    @Override
	    public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		    URL target = e.getURL();
		    visitPage(target);
		}
	    }
	});
    }
    
    private static ClassLoader loader = HelpFrame.class.getClassLoader();
    
    /** History of pages visited */
    private Stack<URL> history = new Stack<URL>();
    
    /** Index in the history */
    private int index = -1;
    
    /** Visit the next page in the history */
    public void goForward() {
	if (index+1 < history.size())
	    loadPage(history.get(++index));
    }
    
    /** Visit the previous page in the history */
    public void goBack() {
	if (index > 0)
	    loadPage(history.get(--index));
    }
    
    /** Visit the contents page */
    public void goHome()  {
	visitPage(loader.getResource("contents.html"));
    }
    
    /** Visit a named page and add it to the history */
    public void visitPage(URL url) {
	if (index >= 0 && url.equals(history.get(index))) return;
	
	history.setSize(++index);
	history.push(url);
	loadPage(url);
    }
    
    /** Load a page given as a URL */
    private void loadPage(URL url) {
	try {
	    browser.setPage(url);
	}
	catch (IOException e) {
	    browser.setText("Oops, I didn't manage to load that page.");
	}
    }
    
    /** The singleton help frame */
    private static HelpFrame theFrame = null;
    
    /** Show the help frame */
    private static void openFrame() {
	if (theFrame == null) theFrame = new HelpFrame();

	theFrame.setVisible(true);
	if (theFrame.getExtendedState() == Frame.ICONIFIED)
		theFrame.setExtendedState(Frame.NORMAL);
    }
    
    /** Show the contents page in the help frame */
    public static void showContents() {
	openFrame();
	theFrame.goHome();
    }
    
    /** Show help after an error message */
    public static void errorHelp(String tag) {
	openFrame();
	URL errpage = loader.getResource("errors.html");
	if (errpage == null) return;
	try {
	    URL anchor = new URL(errpage.toString() + tag);
	    theFrame.visitPage(anchor);
	}
	catch (MalformedURLException e) { 
	    theFrame.visitPage(errpage);
	}
    }
    
    /** For debugging */
    public static void main(String args[]){
	openFrame();
	theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	showContents();
    }
}
