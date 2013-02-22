/*
 * GeomBase.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import funbase.Evaluator;
import funbase.Name;
import funbase.Primitive;
import funbase.Scanner;
import funbase.Value;

/** Common superclass for classes that provide a read-eval-print loop */
public class GeomBase {
    protected boolean statsFlag = false;
    protected String errtag = "";
    protected int status = 0;
    private File currentFile = null;
    protected boolean echo;
    protected boolean display;
    protected PrintWriter log;
    protected Value last_val = null;
    protected Scanner scanner;

    public void setLog(PrintWriter log) {
        this.log = log;
    }

    public void logWrite(String s) {
	log.println(s);
	log.flush();
    }

    public void logWrite(Value v) {
	v.printOn(log);
	log.println();
	log.flush();
    }

    public void logMessage(String msg) {
	logWrite("\n[" + msg + "]");
    }

    public void errorMessage(String msg, String errtag) {
	logMessage(msg);
	if (status < 1) status = 1;
	this.errtag = errtag;
    }

    public void evalError(String prefix, String message, String errtag) {
	log.print(prefix); log.println(message); log.flush();
	if (status < 2) status = 2;
	this.errtag = errtag;
    }
 
    /** Called when a phrase has been parsed */
    protected void showPhrase() {
	if (echo) {
	    logWrite(scanner.getText());
	}
    }

    /** Called when evaluation of a top-level expression is complete */
    protected void exprValue(Value v) {
	last_val = v;
	if (display) {
	    log.print("--> ");
	    v.printOn(log);
	    log.println();
	}
    }

    /** Called when elaboration of a top-level definition is complete */
    protected void defnValue(Name n, Value v) {
	last_val = v;
	n.setGlodef(v, scanner.getText());
	if (display) {
	    log.format("--- %s = ", n);
	    v.printOn(log);
	    log.println();
	}
    }

    protected Value scan() {
	return scanner.nextToken();
    }

    protected void syntax_error(String msg, String help) {
	scanner.syntax_error(msg, help);
    }

    public class ErrReporter {
	public void syntaxError(Scanner.SyntaxException e) {
	    evalError("Oops: ", e.toString(), e.getErrtag());
	}

	public void runtimeError(Evaluator.EvalException e) {
	    evalError("Aargh: ", e.getMessage(), e.getErrtag());
	}

	public void failure(Throwable e) {
	    evalError("Failure: ", e.toString(), "#failure");
	}
    }

    protected boolean eval_loop(Reader reader, boolean display) {
	return eval_loop(reader, display, display);
    }

    protected boolean eval_loop(Reader reader, boolean echo, boolean display) {
	return eval_loop(reader, echo, display, new ErrReporter());
    }

    protected boolean eval_loop(Reader reader, boolean display, 
				ErrReporter err) {
	return eval_loop(reader, display, display, err);
    }

    protected boolean eval_loop(Reader reader, boolean echo, boolean display, 
				ErrReporter err) {
	Name top = Name.find("_top");
	Scanner scanner = new Scanner(reader);
	errtag = "";
	last_val = null;

	while (true) {
	    try {
		scanner.resetText();
		this.scanner = scanner;
		this.echo = echo;
		this.display = display;
		if (Evaluator.execute(top.glodef) != Value.makeBoolValue(true))
		    return true;

		if (display) {
		    if (statsFlag) Evaluator.printStats(log);
		    log.flush();
		}
	    }
	    catch (Scanner.SyntaxException e) {
		err.syntaxError(e);
		return false;
	    }
	    catch (Evaluator.EvalException e) {
		err.runtimeError(e);
		return false;
	    }
	    catch (Throwable e) {
		err.failure(e);
	        return false;
	    }
	}
    }

    /** Load from a file */
    protected void loadFromFile(File file, boolean display) {
        File save_currentFile = currentFile;
        try {
            Reader reader = new BufferedReader(new FileReader(file));
            currentFile = file;
            eval_loop(reader, display);
            logMessage("Loaded " + file.getName());
            try { reader.close(); } catch (IOException e) { /* Ignore */ }
        }
        catch (FileNotFoundException e) {
            errorMessage("Can't read " + file.getName(), "#nofile");
        }
        finally {
            currentFile = save_currentFile;
        }
    }

    protected void loadFromStream(InputStream in) {
	Reader reader = new InputStreamReader(in);
	eval_loop(reader, false);
    }

    public File getCurrentFile() { return currentFile; }

    public void exit() {
	System.exit(0);
    }

    public boolean getStatsFlag() {
        return statsFlag;
    }

    public void setStatsFlag(boolean statsFlag) {
        this.statsFlag = statsFlag;
    }

    public String getErrtag() { return errtag; }

    public int getStatus() { return status; }
    
    public String getEditText() {
	return "";
    }
    
    public void setEditText(String text) {
	// So nothing
    }

    public static GeomBase theApp;

    public static void registerApp(GeomBase app) {
	theApp = app;
    }

    public static final Primitive primitives[] = {
	new Primitive.Prim0("scan") {
	    @Override
	    public Value invoke0() {
		return theApp.scan();
	    }
	},

	new Primitive.Prim2("synerror") {
	    @Override
	    public Value invoke2(Value msg, Value help) {
		theApp.syntax_error(cxt.string(msg), cxt.string(help));
		return Value.nil;
	    }
	},

	new Primitive.Prim1("topval") {
	    @Override
	    public Value invoke1(Value v) {
		theApp.exprValue(v);
		return Value.nil;
	    }
	},

	new Primitive.Prim2("topdef") {
	    @Override
	    public Value invoke2(Value x, Value v) {
		Name n = cxt.cast(Name.class, x, "name");
		theApp.defnValue(n, v);
		return Value.nil;
	    }
	},
	
	new Primitive.Prim0("toptext") {
	    @Override
	    public Value invoke0() {
		theApp.showPhrase();
		return Value.nil;
	    }
	},

        new Primitive.Prim1("load") {
            @Override
            public Value invoke1(Value fname0) {
        	String fname = cxt.string(fname0);
        	File current = theApp.getCurrentFile();
        	File file = (current == null ? new File(fname)
			     : new File(current.getParentFile(), fname));
        	theApp.loadFromFile(file, false);
        	return Value.nil;
            }
        },

	new Primitive.Prim1("print") {
	    @Override
	    public Value invoke1(Value v) {
		theApp.logWrite(v);
		Thread.yield();
		return v;
	    }
	},

	new Primitive.Prim0("debug") {
	    @Override
	    public Value invoke0() {
		return Value.makeNumValue(funbase.Evaluator.debug);
	    }
	},

        new Primitive.Prim1("install") {
            /* Install a plug-in class with primitives. */
            @Override
	    public Value invoke1(Value name) {
        	String clname = cxt.string(name);
        	try {
        	    Session.installPlugin(Class.forName("plugins." + clname));
        	}
        	catch (Exception e) {
        	    cxt.primFail("install failure for " + clname
        		    + " - " + e.getMessage(), "#install");
        	}
        	return Value.nil;
            }
        },

        new Primitive.Prim1("save") {
            @Override
            public Value invoke1(Value fname) {
        	try {
        	    Session.saveSession(new File(cxt.string(fname)));
        	    return Value.nil;
        	} catch (Command.CommandException e) {
        	    cxt.primFail(e.toString());
		    return null;
        	}
            }
        },
        
	new Primitive.Prim1("restore") {
	    @Override
	    public Value invoke1(Value fname) {
		try {
		    Session.loadSession(new File(cxt.string(fname)));
		    return Value.nil;
		} catch (Command.CommandException e) {
		    cxt.primFail(e.toString());
		    return null;
		}
	    }
	},

        new Primitive.Prim0("quit") {
            @Override
            public Value invoke0() {
        	theApp.exit();
        	return Value.nil;
            }
        }
    };
}
