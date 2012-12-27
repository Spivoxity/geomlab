/*
 * Scanner.java
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

import java.io.*;

public class Scanner {
    public Name tok;
    public Value sym;
    
    private Reader reader;
    private StringBuilder push_back = new StringBuilder();
    private int line_num = 1;
    private int char_num = 0, start_char, root_char;
    
    /* The scanner keeps track of the text that has been scanned, so that
     * the defining text can be saved with each name in the global env.
     * The variable 'virgin' indicates whether we are skipping characters that
     * come before the first token of the text. */
    private StringBuilder text = new StringBuilder(200);
    private boolean virgin = true;

    public Scanner(Reader reader) {
	this.reader = reader;
    }
    
    private char readChar() {
	try {
	    int ich = reader.read(); // returns -1 on EOF
	    if (ich < 0)
		return '\0';
	    else
		return (char) ich;
	} catch (IOException e) {
	    return '\0';
	}
    }
    
    private char getChar() {
	char ch;
	
	char_num++;
	
	if (push_back.length() == 0)
	    ch = readChar();
	else {
	    int i = push_back.length()-1;
	    ch = push_back.charAt(i); 
	    push_back.setLength(i);
	}

	if (ch != '\0') text.append(ch);
	return ch;
    }
    
    /** Push back one character onto the input */
    private void pushBack(char ch) {
	/* We could wrap the input in a PushbackReader, but that's overkill,
	 * as we only need to push back one character -- and anyway, we need
	 * to deal with the saved text. */
	if (ch != '\0') {
	    char_num--;
	    push_back.append(ch);
	    text.deleteCharAt(text.length()-1);
	}
    }
    
    public String getText() {
	return text.toString();
    }
    
    public void resetText() {
	text.setLength(0);
	virgin = true;
    }
    
    public static void makeToken(Name tag, Name tok, int p, int rp) {
	tag.token = tok;
	tag.prio = p;
	tag.rprio = rp;
    }
    
    private static void makeToken(String tag) {
	Name name = Name.find(tag);
	name.token = name;
    }

    private static Name ATOM, BRA, COMMA, EOF, EOL, KET, LPAR, RPAR,
    	NUMBER, SEMI, STRING, VBAR;
    
    /** Initialize names used for scanning */
    public static void initSyntax() {
	ATOM = Name.find("atom"); BRA = Name.find("bra");
	COMMA = Name.find("comma"); EOF = Name.find("eof");
	EOL = Name.find("eol"); KET = Name.find("ket");
	LPAR = Name.find("lpar"); NUMBER = Name.find("number"); 
	RPAR = Name.find("rpar"); SEMI = Name.find("semi"); 
	STRING = Name.find("string"); VBAR = Name.find("vbar");
	
	makeToken("define");
	makeToken("=");
	makeToken("-"); // Needed for boot
    }
    
    private boolean isOpChar(char ch) {
	final String opchars = ".!#$%&*+-/:<=>?@^~";
	return opchars.indexOf(ch) != -1;
    }
    
    public void scan() {
	start_char = char_num;
	char ch = getChar();
	tok = null; sym = Value.nil;
	while (tok == null) {
	    switch (ch) {
		case '\0': // EOF
		    tok = EOF; break;
		case ' ':
		case '\t':
		case '\r':
		    start_char = char_num; 
		    if (virgin) resetText();
		    ch = getChar(); 
		    break;
		case '\n':
		    line_num++; start_char = char_num; 
		    if (virgin) resetText();
		    ch = getChar(); 
		    break;
		case '{': {
		    int depth = 0;
		    do {
			if (ch == '{')
			    depth++;
			else if (ch == '}')
			    depth--;
			else if (ch == '\n')
			    line_num++;
			else if (ch == '\0') {
			    start_char = char_num; tok = EOF;
			    syntax_error("unterminated comment", "#comment");
			}
			
			ch = getChar();
		    } while (depth > 0);
		    break; 
		}
		case '}':
		    syntax_error("Can't find matching '{'", "#bracematch");
		    break;
		case '(':
		    tok = LPAR; break;
		case ')': 
		    tok = RPAR; break;
		case '[':
		    tok = BRA; break;
		case ']':
		    tok = KET; break;
		case ',': 
		    tok = COMMA; break;
		case ';':
		    tok = SEMI; break;
		case '|':
		    tok = VBAR; break;
		    
		case '"': {
		    StringBuilder string = new StringBuilder(80);
		    ch = getChar();
		    while (ch != '"' && ch != '\n' && ch != '\0') {
			string.append(ch); ch = getChar();
		    }
		    if (ch == '"') {
			tok = STRING;
			sym = Value.makeStringValue(string.toString());
		    } else {
			pushBack(ch);
			start_char = char_num;
			tok = (ch == '\n' ? EOL : EOF);
			syntax_error("unterminated string constant", "#string");
		    }
		    break;
		}
		
		case '#': {
		    StringBuilder buf = new StringBuilder(10);
		    ch = getChar();
		    if (Character.isLetter(ch) || ch == '_') {
			while (Character.isLetterOrDigit(ch) || ch == '_') {
			    buf.append(ch); ch = getChar();
			}
		    }
		    else if (isOpChar(ch)) {
			while (isOpChar(ch)) {
			    buf.append(ch); ch = getChar();
			}
		    }
		    else {
			syntax_error("I expected an identifier or operator",
				     "#nohelp");
		    }
		    pushBack(ch);
		    tok = ATOM;
		    sym = Name.find(buf.toString());
		    break;
		}
		
		default:
		    if (Character.isLetter(ch) || ch == '_') {
			// An identifier
			StringBuilder buf = new StringBuilder(10);
			while (Character.isLetterOrDigit(ch) || ch == '_') {
			    buf.append(ch); ch = getChar();
			}
			pushBack(ch);
			Name x = Name.find(buf.toString());
			tok = x.token; sym = x;
		    } else if (Character.isDigit(ch)) {
			// A numeric constant
			StringBuilder buf = new StringBuilder(10);
			while (Character.isDigit(ch)) {
			    buf.append(ch); ch = getChar();
			}
			if (ch == '.') {
			    buf.append(ch); ch = getChar();
			    if (! Character.isDigit(ch)) {
				pushBack(ch); ch = '.';
			    } else {
				while (Character.isDigit(ch)) {
				    buf.append(ch); ch = getChar();
				}
			    }
			}
			if (ch == 'E') {
			    buf.append(ch); ch = getChar();
			    if (ch == '+' || ch == '-') {
				buf.append(ch); ch = getChar();
			    }
			    if (! Character.isDigit(ch))
				badToken();
			    do {
				buf.append(ch); ch = getChar();
			    } while (Character.isDigit(ch));
			}
			pushBack(ch);
			tok = NUMBER; 
			double val = Double.parseDouble(buf.toString());
			sym = Value.makeNumValue(val);
		    } else if (isOpChar(ch)) {
			// A symbolic operator
			StringBuilder buf = new StringBuilder(10);
			while (isOpChar(ch)) {
			    buf.append(ch); ch = getChar();
			}
			pushBack(ch);
			Name x = Name.find(buf.toString());
			tok = x.token; sym = x;
			if (tok == Name.IDENT) badToken();
		    } else {
			badToken();
		    }
	    }
	}
	
	if (virgin) {
	    root_char = start_char;
	    virgin = false;
	}
    }

    public Value nextToken() {
	scan();
	return Value.makeList(tok, sym);
    }

    private void badToken() {
	syntax_error("unknown symbol", "#badtok");
    }
    
    /** Report a syntax error at the current token */
    public void syntax_error(String msg, String errtag) {
	String chars = 
	    (tok == EOF ? "end of input" :
		tok == EOL ? "end of line" :
		"'" + text.substring(start_char - root_char) + "'");
	throw new SyntaxException(msg, line_num, start_char, char_num, 
		chars, errtag);
    }
    
    public static class SyntaxException extends RuntimeException {
	private int line, start, end;
	private String errtok, errtag;
	
	public SyntaxException(String msg, int line, int start, int end, 
		String etok, String errtag) {
	    super(msg);
	    this.line = line;
	    this.start = start;
	    this.end = end;
	    this.errtok = etok;
	    this.errtag = errtag;
	}
	
	public String shortMessage() {
	    return getMessage() + " (at " + errtok + ")";
	}
	
	@Override
	public String toString() {
	    return getMessage() + " (at " + errtok + " on line " + line + ")";
	}
	
	/** Get the start position of the token where the error was detected */
	public int getStart() { return start; }
	
	/** Get the end position of the token where the error was detected */
	public int getEnd() { return end; }
	
	/** Get the help file tag for the error */
	public String getErrtag() { return errtag; }
    }
}

