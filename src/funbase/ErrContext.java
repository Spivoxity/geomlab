/*
 * ErrContext.java
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

import java.io.Serializable;
import funbase.Evaluator.EvalException;
import funbase.Value.WrongKindException;

/** Context for runtime error messages.
 *    
 *  An instance of this class names the function that is running.  Subclasses
 *  represent a context that is frozen on entry to a library function.  This
 *  allows errors detected inside the function to be reported with the site
 *  where the library was entered, not the location where the error was 
 *  detected. 
 */
public class ErrContext implements Serializable {
    @SuppressWarnings("unused")
    private static final String svnid =
	"$Id: ErrContext.java 642 2012-07-15 22:31:52Z mike $";
    
    private static final long serialVersionUID = 1L;

    private String func, parent, prim;

    public ErrContext(String name) {
	func = parent = name;
    }

    public ErrContext(String func, String parent, String prim) {
	this.func = func; this.parent = parent; this.prim = prim;
    }

    /** Compute the new context on entering a function */
    public ErrContext enter(ErrContext cxt) {
	return cxt;
    }

    /** Freeze the context on entry to a library function. */
    public ErrContext freezeEnter(String name) {
	return new FrozenContext(name, func);
    }
    
    /** Compute the context for executing a primitive */
    public ErrContext primEnter(String name) {
	prim = name; return this;
    }

    public String format(String msg, String name) {
	if (name != null)
	    return msg + " in function '" + name + "'";
	else
	    return msg;
    }

    private static class FrozenContext extends ErrContext {

	public FrozenContext(String func, String parent) {
	    super(func, parent, func);
	}

	@Override
	public ErrContext enter(ErrContext cxt) {
	    return cxt;
	}

	@Override
	public ErrContext freezeEnter(String name) {
	    return this;
	}

	@Override
	public ErrContext primEnter(String name) {
	    return this;
	}
    }

    /** An initial context used in the top level, outside user code */
    private static class NullContext extends ErrContext {
	public NullContext() {
	    super(null);
	}

	@Override
	public ErrContext enter(ErrContext cxt) {
	    return this;
	}

	@Override
	public ErrContext freezeEnter(String name) {
	    return this;
	}
    }

    public static final ErrContext 
        initContext = new ErrContext(null),
        nullContext = new NullContext();
    

    // Errors during evaluation

    public void error(String msg) {
	throw new EvalException(format(msg, func));
    }

    public void error(String msg, String help) {
	throw new EvalException(format(msg, func), help);
    }

    /** Complain when the wrong number of arguments are provided */
    public void err_nargs(String name, int nargs, int arity) {
	error("function " + name + " called with " + nargs
	      + (nargs == 1 ? " argument" : " arguments")
	      + " but needs " + arity, "#numargs");
    }

    /** Complain when no pattern matches in a function definition */
    public void err_nomatch(Value args[], int base, int arity) {
	StringBuilder buf = new StringBuilder();
	if (arity > 0) {
	    buf.append(args[base+0]);
	    for (int i = 1; i < arity; i++)
		buf.append(", " + args[base+i]);
	}

	error("no pattern matches "
	      + (arity == 1 ? "argument" : "arguments")
	      + " (" + buf + ")", "#match");
    }

    public void err_nomatch1(Value arg1) {
	err_nomatch(new Value[] { arg1 }, 0, 1);
    }

    public void err_nomatch2(Value arg1, Value arg2) {
	err_nomatch(new Value[] { arg1, arg2 }, 0, 2);
    }

    public void err_nomatch3(Value arg1, Value arg2, Value arg3) {
	err_nomatch(new Value[] { arg1, arg2, arg3 }, 0, 3);
    }

    /** Complain about an undefined name */
    public void err_notdef(Name x) {
	error("'" + x + "' is not defined", "#undef");
    }

    /** Complain about a non-boolean guard or 'if' condition */
    public void err_boolcond() {
	error("boolean expected as condition", "#condbool");
    }

    /** Complain about matching against a constructor with the
     *  wrong number of argument patterns */
    public void err_patnargs(String name) {
	error("matching constructor '" + name 
	      + "' with wrong number of arguments", "#patnargs");
    }

    // Utility methods for primitives
    
    public void primFail(String msg) {
        throw new EvalException(format(msg, parent));
    }

    public void primFail(String msg, String errtag) {
        throw new EvalException(format(msg, parent), errtag);
    }

    public void expect(String expected) {
	String vowels = "aeiou";
	String a = (vowels.indexOf(expected.charAt(0)) >= 0 ? "an" : "a");
        primFail("'" + prim + "' expects " + a + " "
		 + expected + " argument", "#type");
    }

    public void primExpect(String prim, String expected) {
	ErrContext cxt1 = this.primEnter(prim);
	cxt1.expect(expected);
    }

    /** Fetch value of a NumValue object, or throw EvalException */
    public double number(Value a) {
        try {
            return a.asNumber();
        }
        catch (WrongKindException e) {
            expect("numeric");
            return 0.0;
        }
    }

    /** Fetch value of a BoolValue object, or throw EvalException */ 
    public boolean bool(Value a) {
        try {
            return a.asBoolean();
        }
        catch (WrongKindException e) {
            expect("boolean");
            return false;
        }
    }

    /** Fetch value of a StringValue object, or throw EvalException */ 
    public String string(Value a) {
        try {
            return a.asString();
        }
        catch (WrongKindException e) {
            expect("string");
            return null;
        }
    }
    
    public Name name(Value a) {
	return cast(Name.class, a, "name");
    }

    public void list_fail(Value xs, String msg) {
	primFail("taking " + msg + " of " 
		 + (xs.isNilValue() ? "the empty list" : "a non-list"),
		 "#" + msg);
    }

    /** Fetch head of a ConsValue object, or throw EvalException */ 
    public Value head(Value xs) {
        try {
            return xs.getHead();
        }
        catch (WrongKindException e) {
	    list_fail(xs, "head");
            return null;
        }
    }

    /** Fetch tail of a ConsValue object, or throw EvalException */ 
    public Value tail(Value xs) {
        try {
            return xs.getTail();
        }
        catch (WrongKindException e) {
	    list_fail(xs, "tail");
            return null;
        }
    }

    /** Compute length of a list argument */ 
    public int listLength(Value xs) {
	Value ys = xs;
	int n = 0; 
        while (ys.isConsValue()) {
            ys = tail(ys); n++;
        }
        if (! ys.isNilValue()) expect("list");
        return n;
    }

    /** Convert list argument to array */
    public Value[] toArray(Value xs) {
	try {
	    return Value.makeArray(xs);
	} catch (WrongKindException _) {
	    expect("list");
	    return null;
	}
    }

    /** Cast an argument to some specified subclass */
    public <T extends Value> T cast(Class<T> cl, Value v, String expected) {
        try {
            return cl.cast(v);
        }
        catch (ClassCastException _) {
            expect(expected);
            return null;
        }
    }

    /** Convert list argument to array of specified class */
    public <T extends Value> T[] toArray(Class<T> cl, Value xs, 
					 String expected) {
	try {
	    return Value.makeArray(cl, xs);
	} catch (WrongKindException _) {
	    expect(expected);
	    return null;
	}
    }
}

/*

NullContext(func=null, parent=null, prim='head')
    Boolean expected as condition
    'head' expects a list argument
  call 'foo' --> NullContext(null, null)
  call 'reverse' --> NullContext(null, null)

ErrContext(func='foo', parent='foo', prim='head')
    Boolean expected as condition in function 'foo'
    'head' expects a list argument in function 'foo'
  call 'baz' --> ErrContext(baz, baz)
  call 'reverse' --> FrozenContext(reverse, foo, reverse)

FrozenContext(func=reverse, parent=foo, prim=reverse)
    Boolean expected as condition in function 'reverse'
    'reverse' expects a list argument in function 'foo'
  call 'foo' --> ErrContext(foo, foo)
  call 'reva' --> FrozenContext(reverse, foo, reverse)

What about 'no pattern matches arguments ([]) in function foo called from baz'?

*/
