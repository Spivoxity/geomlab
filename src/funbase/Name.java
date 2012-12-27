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

import java.io.PrintWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/** Names in the Fun program are represented by unique Name objects.
 *  These contain a shallow binding to their value in the global
 *  environment. */
public final class Name extends Value implements Comparable<Name> {
    @SuppressWarnings("unused")
    private static final String svnid =
	"$Id: Name.java 642 2012-07-15 22:31:52Z mike $";
    
    private static final long serialVersionUID = 1L;
	
    /** The name as a string */
    public final String tag;
    
    /** The definition in the global environment, or null */
    public transient Value glodef = null;
    
    /** For user-defined names, the text of the definition */
    private String deftext = null;
    
    /** True for names that are system-defined and may not be changed */
    private boolean frozen = false;
    
    /** Token returned for this word by the scanner */
    public Name token;

    /** Priority when used as a binary operator */
    public int prio = 0;

    /** Priority for right operands */
    public int rprio = 0;

    private Name(String tag) {
	this.tag = tag;
	nameTable.put(this.tag, this);
    }
    
    /** Set the global definition and defining text */
    public void setGlodef(Value v, String text) { 
	this.glodef = v;
	this.deftext = text;
	if (Name.freezer) frozen = true; 
    }
    
    /** Get the global definition of a name */
    public Value getGlodef() { return glodef; }
    
    /** Get the defining text */
    public String getDeftext() { return deftext; }
    
    /** Test if the global definition is unmodifiable */
    public boolean isFrozen() { return frozen && !Name.freezer; }
    
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
	out.printf("name #%s\n", tag);
    }

    /** A global mapping of strings to Name objects */
    private static Map<String, Name> nameTable = 
	new HashMap<String, Name>(200);
    
    public static Name IDENT = new Name("ident");

    static {
	IDENT.token = IDENT;
    }
	
    /** Find or create the unique Name with a given spelling */
    public static Name find(String tag) {
	Name name = nameTable.get(tag);
	if (name == null) {
	    name = new Name(tag);
	    name.token = IDENT;
	}
	return name;
    }

    /** Discard all names */
    public static void clearNameTable() {
	nameTable.clear();
	IDENT = new Name("ident");
	IDENT.token = IDENT;
    }

    /** Read global definitions from a serialized stream */
    public static void readNameTable(ObjectInputStream in) 
    		throws IOException, ClassNotFoundException {
	for (;;) {
	    Name x = (Name) in.readObject();
	    if (x == null) break;
	    x.glodef = (Value) in.readObject();
	}
    }

    /** Write global definitions to a serialized stream */
    public static void writeNameTable(ObjectOutputStream out) 
    		throws IOException {
	for (Name x : nameTable.values()) {
	    if (x.glodef != null || x.token != IDENT) {
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
	x.token = (this.token == this ? x : this.token);
	x.prio = this.prio;
	x.rprio = this.rprio;
	x.deftext = this.deftext;
	x.frozen = this.frozen;
	return x;
    }

    /** Initial definitions become frozen once this is set to false */
    public static boolean freezer = true;

    /** Freeze all global definitions made so far */
    public static void freezeGlobals() {
	freezer = false;
    }
    
    /** Get alphabetical list of globally defined names */
    public static List<String> getGlobalNames() {
        ArrayList<String> names = new ArrayList<String>(100);
        for (Name x : nameTable.values()) {
            String xx = x.tag;
            if (x.getGlodef() != null && ! xx.startsWith("_"))
        	names.add(xx);	    
        }
	Collections.sort(names);
        return names;
    }
    
    /** Save globally defined names in bootstrap format */
    public static void dumpNames(PrintWriter out) {
	    // Sort the entries to help us reach a fixpoint
	    ArrayList<String> names = new ArrayList<String>(nameTable.size());
	    names.addAll(nameTable.keySet());
	    Collections.sort(names);
	    for (String k : names) {
		Name x = find(k);
		if (x.glodef != null) {
		    out.printf("global #%s ", x.tag);
		    x.glodef.dump(out);
		}
	    }
	    out.println("quit");
	    out.close();
    }
}
