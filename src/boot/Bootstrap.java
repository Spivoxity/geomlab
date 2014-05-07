/*
 * Bootstrap.java
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

package boot;

import funbase.Value;
import funbase.Name;
import funbase.FunCode;
import funbase.FunCode.Opcode;

import geomlab.GeomBase;
import geomlab.Session;

import java.io.File;
import java.io.PrintWriter;

public abstract class Bootstrap {
    public static void define(String name, Value val) {
        Name x = Name.find(name);
        x.setGlodef(val, null, true);
    }

    public static class Instr { 
        public Opcode op;
        public int arg;

        public Instr(Opcode op, int arg) {
            this.op = op; this.arg = arg;
        }
    }

    public static class Body {
        public Instr instrs[];

        public Body(Instr instrs[]) {
            this.instrs = instrs;
        }
    }

    public static Value number(double x) {
        return Value.NumValue.getInstance(x);
    }

    public static Value truth = Value.BoolValue.truth,
        falsity = Value.BoolValue.falsity;

    public static Value string(String x) {
        return Value.StringValue.getInstance(x);
    }

    public static Body body(Instr... instrs) {
        return new Body(instrs);
    }

    public static Instr instr(Opcode op, int arg) {
        return new Instr(op, arg);
    }

    public static Value[] consts(Value... lits) {
        return lits;
    }

    public static FunCode funcode(String name, int arity, int fsize, int ssize,
                                  Body body, Value consts[]) {
        Instr code[] = body.instrs;
        int nops = code.length;
        Opcode ops[] = new Opcode[nops];
        int rands[] = new int[nops];

        for (int i = 0; i < nops; i++) {
            ops[i] = code[i].op;
            rands[i] = code[i].arg;
        }

        return new FunCode(name, arity, fsize, ssize, ops, rands, consts);
    }

    public static Value closure(FunCode body) {
        return body.makeClosure(new Value[1]);
    }

    public static Name name(String s) {
        return Name.find(s);
    }

    public abstract void boot();

    public void bootstrap(String fname) {
        try {
            System.setProperty("java.awt.headless", "true");
            funbase.Scanner.initSyntax();
            funbase.FunCode.install(new funbase.Interp());
            GeomBase.registerApp(new GeomBase());
            Session.loadPlugin(geomlab.GeomBase.class, true);
            Session.loadPlugin(funbase.FunCode.class, true);
            Session.loadPlugin(plugins.BasicPrims.class, true);
            Session.loadPlugin(plugins.Cell.class, true);
            Session.loadPlugin(plugins.StringPrims.class, true);
            this.boot();
            Session.saveSession(new File(fname));
        }
        catch (geomlab.Command.CommandException e) {
            throw new Error(e);
        }
    }

    public void bootstrap() {
        bootstrap("boot.gls");
    }
}
