/*
 * BootLoader.java
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

package funbase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/** Bootstrap GeomLab from a saved state.
 * 
 *  Part of the process of building a working GeomLab system is to 
 *  input code for the compiler that was prepared using the previous
 *  incarnation of the system.  The code is written out using the
 *  method Name.dumpNames and the dump methods of various Value
 *  classes.  This class contains methods for reading it back.  Once
 *  the GeomLab system is working, this class is no longer needed, so
 *  the error handling is deliberately spartan. */
public class BootLoader {
    private Scanner scanner;
    
    public BootLoader(Reader r) {
	scanner = new Scanner(r);
    }
    
    /** Read a sequence of global value definitions. */
    public void boot() {
	scanner.scan();
	
	while (true) {
	    if (scanner.tok == Name.IDENT && scanner.sym.equals("quit"))
		break;

	    Value t = get("ident");
	    if (! t.equals("global")) 
		throw new Error("BootLoader.boot " + t);

	    Name x = (Name) get("atom");
	    Value v = value();
	    x.setGlodef(v, null);
	}
    }
    
    /** Read a value from the bootfile */
    private Value value() {
	Value t = get("ident");
	if (t.equals("boolean"))
	    return Value.makeBoolValue(getInt() != 0);
	else if (t.equals("name"))
	    return get("atom");
	else if (t.equals("string"))
	    return get("string");
	else if (t.equals("integer"))
	    return Value.makeNumValue(getInt());
	else if (t.equals("primitive"))
	    return Value.makeFunValue(Primitive.find(getString()));
	else if (t.equals("nil"))
	    return Value.nil;
	else if (t.equals("funcode")) {
	    /* Bytecode for a function. */
	    String name = getString();
	    int arity = getInt();
	    int nops = getInt();
	    int nconsts = getInt();
	    int fsize = getInt();
	    int ssize = getInt();
	    FunCode.Opcode ops[] = new FunCode.Opcode[nops];
	    int rands[] = new int[nops];
	    Value consts[] = new Value[nconsts];

	    for (int i = 0; i < nops; i++) {
		Value op = get("ident");
		ops[i] = FunCode.getOpcode(op.toString());
		rands[i] = getInt();
	    }

	    for (int j = 0; j < nconsts; j++) {
		consts[j] = value();
	    }

	    return new FunCode(name, arity, fsize, ssize, 
			       ops, rands, consts);
	} else if (t.equals("closure")) {
	    /* A closure.  We don't deal with closures that have free
	       variables, but the compiler avoids these. */
	    FunCode body = (FunCode) value();
	    return body.makeClosure(new Value[1]);
	} else {
	    throw new Error("BootLoader.value " + t);
	}
    }
    
    /** Check for a given token and return its value */
    private Value get(String t) {
	if (! scanner.tok.equals(t)) 
	    throw new Error("expected " + t);
	Value s = scanner.sym;
	scanner.scan(); return s;
    }
    
    /** Return the next token as a string */
    private String getString() {
	try {
	    Value.StringValue s = (Value.StringValue) get("string");
	    return s.text;
	}
	catch (ClassCastException _) {
	    throw new Error("missing string");
	}
    }

    /** Scan and return an integer */
    private int getInt() {
	boolean neg = false;
	if (scanner.tok.equals("-")) {
	    scanner.scan();
	    neg = true;
	}
	try {
	    Value.NumValue x = (Value.NumValue) get("number");
	    int n = (int) x.val;
	    return (neg ? -n : n);
	}
	catch (ClassCastException _) {
	    throw new Error("missing integer");
	}
    }
    
    /** Bootstrap the system from a file. */
    public static void bootstrap(File file) {
        try {
            Reader reader = new BufferedReader(new FileReader(file));
            BootLoader loader = new BootLoader(reader);
            loader.boot();
            try { reader.close(); } catch (IOException e) {
        	throw new Error(e);
            }
        }
        catch (FileNotFoundException e) {
            throw new Error("Can't read " + file.getName());
        }
    }
}
