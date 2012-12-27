(* token -- scan a token *)
val token : Lexing.lexbuf -> Parser.token

(* lnum -- line number in input *)
val lnum : int ref
