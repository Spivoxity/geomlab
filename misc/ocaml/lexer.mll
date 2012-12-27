{
open Parser
open Types
open Lexing

let idtable = Hashtbl.create 100

let init =
  let add (s, v) = Hashtbl.add idtable s v in
  List.iter add 
    [ ("if", IF); ("then", THEN); ("else", ELSE); ("let", LET);
      ("define", DEFINE); ("in", IN); ("lambda", LAMBDA); ("when", WHEN);
      ("op", OP); ("_", ANON); ("div", MULOP (name "div")); 
      ("mod", MULOP (name "mod")); ("and", AND); ("or", OR); 
      ("not", MONOP (name "not")); ("=", EQUAL);
      ("+", PLUS); ("-", MINUS); ("$", MULOP (name "$")); 
      ("*", MULOP (name "*")); ("/", MULOP (name "/")); 
      ("&", ADDOP (name "&")); ("~", UMINUS); (":", CONS);
      ("@", APPOP (name "@")); ("<", RELOP (name "<")); 
      ("<=", RELOP (name "<=")); ("<>", RELOP (name "<>")); 
      (">", RELOP (name ">")); (">=", RELOP (name ">="));
      (">>", SEQ) ]

let lookup s =
  try Hashtbl.find idtable s with Not_found -> IDENT (name s)

let findop s =
  try Hashtbl.find idtable s with Not_found -> BADTOK

let lnum = ref 1
}


rule token = parse
    ['A'-'Z''a'-'z''_']['A'-'Z''a'-'z''0'-'9''_']*
		    	{ lookup (lexeme lexbuf) }
  | ['0'-'9']+		{ NUMBER (int_of_string (lexeme lexbuf)) }
  | ['!''#''$''%''&''*''+''-''/'':''<''=''>''?''@''^''~']+
  			{ findop (lexeme lexbuf) }
  | '"'[^'"''\n']*'"'	{ let s = lexeme lexbuf in
			  STRING (String.sub s 1 (String.length s - 2)) }
  | '#'['A'-'Z''a'-'z']+  { ATOM (name (lexeme lexbuf)) }
  | '('			{ LPAR }
  | ')'			{ RPAR }
  | '['			{ BRA }
  | ']'			{ KET }
  | ','			{ COMMA }
  | ';'			{ SEMI }
  | '|'			{ VBAR }
  | '{'			{ comment lexbuf; token lexbuf }
  | [' ''\t']+		{ token lexbuf }
  | '\n'		{ incr lnum; token lexbuf }
  | _			{ BADTOK }
  | eof			{ EOT }

and comment = parse
    '{'			{ comment lexbuf; comment lexbuf }
  | '}'			{ () }
  | '\n'		{ incr lnum; comment lexbuf }
  | _			{ comment lexbuf }
  | eof			{ raise Parsing.Parse_error }

and junk_line =
  parse
      [^'\n']*'\n'	{ () }
    | _			{ () }
    | eof		{ () }

