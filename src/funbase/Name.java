/*
 * Name.java
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

import funbase.Primitive.PRIMITIVE;
import funbase.Primitive.DESCRIPTION;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/** Names in the Fun program are represented by unique Name objects.
 *  These contain a shallow binding to their value in the global
 *  environment. */
@DESCRIPTION("a name")
public final class Name extends Value implements Comparable<Name> {
    private static final long serialVersionUID = 1L;
	
    /** The name as a string */
    public final String tag;
    
    /** The definition in the global environment, or null */
    public transient Value glodef = null;
    
    /** Level at which the name is defined. Values 0 = fixed system, 
        1 = preloaded but redefinable, 2 = undefined or user-defined */
    private int level = 2;

    /** True if the global definition was loaded from bootstrap */
    private transient boolean inherited = false;

    private static List<Reset> resets = null;

    private Name(String tag) {
	this.tag = tag;
	nameTable.put(this.tag, this);
        this.subr = new NameFunction();
    }
    
    /** Set the global definition */
    public void setGlodef(Value v) { 
        if (resets != null)
            resets.add(new Reset(this));

	glodef = v;
        inherited = false;
        level = stage;
    }
    
    /** Set global definition from bootstrap */
    public void bootDef(Value v) {
        glodef = v;
        inherited = true;
        level = 0;
    }

    /** Get the global definition of a name */
    public Value getGlodef() { return glodef; }
    
    /** Test if the global definition is unmodifiable */
    public boolean isFrozen() { return (level == 0 && stage > 0); }
    
    @Override
    public int compareTo(Name other) {
	if (this == other) return 0;
	return this.tag.compareTo(other.tag);
    }
    
    @Override
    public boolean equals(Object other) {
	if (other instanceof String)
	    return tag.equals(other);
	else
	    return this == other;
    }

    @Override
    public String toString() { return tag; }
    
    @Override
    public void printOn(PrintWriter out) {
	out.printf("#%s", tag);
    }
    
    @Override
    public void dump(PrintWriter out) {
	out.printf("N(\"%s\")", tag);
    }

    /** A global mapping of strings to Name objects */
    private static Map<String, Name> nameTable = new HashMap<String, Name>(200);
    
    /** Find or create the unique Name with a given spelling */
    public static Name find(String tag) {
	Name name = nameTable.get(tag);
	if (name == null)
	    name = new Name(tag);
	return name;
    }

    /** Read global definitions from a serialized stream */
    public static void readNameTable(ObjectInputStream in) 
    		throws IOException, ClassNotFoundException {
        stage = (Integer) in.readObject();
	for (;;) {
	    Name x = (Name) in.readObject();
	    if (x == null) break;
	    x.glodef = (Value) in.readObject();
	}
    }

    /** Write global definitions to a serialized stream */
    public static void writeNameTable(ObjectOutputStream out) 
    		throws IOException {
        out.writeObject(stage);
	for (Name x : nameTable.values()) {
	    if (x.glodef != null) {
		out.writeObject(x); 
		out.writeObject(x.glodef);
	    }
	}
	out.writeObject(null);
    }
    
    private Object readResolve() {
	/* Careful control of sharing is needed for serialization to
	   work properly.  That's why the glodef field of a Name is
	   marked transient and restored separately after the Name
	   itself: that way, references to the name from inside its
	   own global definition are linked to the unique Name object,
	   not to its temporary proxy. */

	Name x = find(this.tag);
        x.level = this.level;
	return x;
    }

    /** Initialization stage.  Global definitions made in stage 0 become
        frozen once stage is non-zero. */
    private static int stage = 0;

    /** Whether functions created now should be ignored in tracebacks */
    public static boolean getFreezer() { return (stage == 0); }

    /** Get alphabetical list of globally defined names */
    public static List<String> getGlobalNames() {
        ArrayList<String> names = new ArrayList<String>(100);
        for (Name x : nameTable.values()) {
            String xx = x.tag;
            if (x.glodef != null && ! xx.startsWith("_"))
        	names.add(xx);	    
        }
	Collections.sort(names);
        return names;
    }
    
    /** Save globally defined names in Java boot format */
    public static void dumpNames(String name, PrintWriter out) {
	// Sort the entries to help us reach a fixpoint
	ArrayList<String> names = new ArrayList<String>(nameTable.size());
	names.addAll(nameTable.keySet());
	Collections.sort(names);

        out.printf("import static funbase.FunCode.Opcode.*;\n\n");
        out.printf("public class %s extends geomlab.Bootstrap {\n", name);
        out.printf("  @Override\n");
        out.printf("  public void boot() {\n");

	for (String k : names) {
            if (k.equals("_syntax")) continue;

	    Name x = find(k);
	    if (x.glodef != null && !x.inherited 
                && !(x.glodef.subr instanceof Primitive)) {
		out.printf("    D(\"%s\", ", x.tag);
		x.glodef.dump(out);
                out.printf(");\n");
	    }
	}
	out.printf("  }\n");
        out.printf("}\n");
	out.close();
    }


    // Mark and reset

    private static class Reset {
        Name n;
        Value v;
        int l;

        public Reset(Name n) {
            this.n = n;
            this.v = n.glodef;
            this.l = n.level;
        }

        public void reset() {
            n.glodef = v;
            n.level = l;
        }
    }

    /** Mark or return to the standard global environment */
    public static void reset() {
        if (resets == null)
            resets = new ArrayList<Reset>();
        else {
            for (Reset r : resets) r.reset();
            resets.clear();
        }
    }


    // Primitives

    @PRIMITIVE
    public static Value name(String x) {
	return find(x);
    }

    @PRIMITIVE
    public static Value _isname(Value x) {
        return Value.bool(x instanceof Name);
    }

    @PRIMITIVE
    public boolean _defined() {
        return (glodef != null);
    }

    @PRIMITIVE
    public boolean _iscons() {
        if (glodef == null) return false;
        return glodef.subr instanceof Primitive.Constructor;
    }

    @PRIMITIVE
    public Value _glodef() {
	if (glodef == null) Evaluator.err_notdef(this);
	return glodef;
    }

    @PRIMITIVE
    public static void _stage(int x) {
        stage = x;
    }

    @PRIMITIVE
    public void _redefine() {
	if (level == 0 && stage > 0)
	    Evaluator.error("#redef", this);
        else if (level == 2 && glodef != null)
            Evaluator.error("#multidef", this);
    }

    @PRIMITIVE
    public String _spelling() {
	return this.toString();
    }

    private static int g = 0;

    @PRIMITIVE
    public static Value _gensym() {
	return find(String.format("$g%d", ++g));
    }

    @PRIMITIVE
    public static Value _dump(String name) {
        String fname = name + ".java";
	try {
	    PrintWriter out = 
		new PrintWriter(new BufferedWriter(new FileWriter(fname)));
	    dumpNames(name, out);
	    return Value.nil;
	} catch (IOException e) {
	    throw new Error(e);
	}
    }

    private class NameFunction
            extends Function implements Primitive.Constructor {
        public NameFunction() {
            super("#"+tag, -1);
        }

        public Value apply(int nargs, Value args0[], int base) {
            Value args[] = new Value[nargs];
            System.arraycopy(args0, base, args, 0, nargs);
            return Value.Blob.instance(Name.this, args);
        }

        @Override
        public Value[] pattMatch(int nargs, Value obj) {
            if (! (obj instanceof Value.Blob)) return null;
            Value.Blob blob = (Value.Blob) obj;
            if (blob.functor != Name.this || blob.args.length != nargs)
                return null;
            return blob.args;
        }
    }
}
