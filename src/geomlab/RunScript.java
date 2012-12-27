/*
 * RunScript.java
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

import funbase.BootLoader;
import funbase.Scanner;
import geomlab.Command.CommandException;
import plugins.Native;

import java.io.*;

/** RunScript allows expressions to be evaluated from the command line, and
 * that is convenient for preparing images to be included in documents.
 * It is also capable of bootstrapping the compiler from a text file
 * containing a dump of the object code. */
public class RunScript extends GeomBase {
    @SuppressWarnings("unused")
    private static final String svnid =
	"$Id: RunScript.java 628 2012-06-01 16:00:36Z mike $";

    public void evalString(String exp) {
	StringReader reader = new StringReader(exp);
	eval_loop(reader, true);
    }

    public static void main(String args[]) {
	System.setProperty("java.awt.headless", "true");
	
	Scanner.initSyntax();
	Native.register(new AWTFactory());
	final RunScript app = new RunScript();
	GeomBase.registerApp(app);
	app.setLog(new PrintWriter(System.out));
	
	int i = 0;
	funbase.FunCode.Jit translator = null;
	File bootfile = null;
	File sessfile = null;

	for (; i < args.length; i++) {
	    if (args[i].equals("-i"))
		translator = new funbase.Interp();
	    else if (i+1 < args.length && args[i].equals("-b"))
		bootfile = new File(args[++i]);
	    else if (i+1 < args.length && args[i].equals("-s"))
		sessfile = new File(args[++i]);
	    else if (i+1 < args.length && args[i].equals("-d"))
		funbase.Evaluator.debug = Integer.parseInt(args[++i]);
	    else
		break;
	}
	    
	if (translator == null) 
	    translator = 
		new funjit.TofuTranslator(new funjit.InlineTranslator());
	funbase.FunCode.install(translator);

	try {
	    if (bootfile != null) {
		Session.installPlugin(GeomBase.class);
		Session.installPlugin(plugins.BasicPrims.class);
		Session.installPlugin(plugins.Cell.class);
		Session.installPlugin(plugins.StringPrims.class);
		BootLoader.bootstrap(bootfile);
	    } else if (sessfile != null) {
		Session.loadSession(sessfile);
	    } else {
		Session.loadResource("geomlab.gls");
	    }
	}
	catch (CommandException e) {
	    throw new Error(e);
	}

	for (; i < args.length; i++) {
	    if (args[i].equals("-e") && i+1 < args.length)
		    app.evalString(args[++i]);
	    else if (args[i].equals("-"))
		app.loadFromStream(System.in);
	    else
		app.loadFromFile(new File(args[i]), false);
	}

	System.exit(app.getStatus());
    }
}
