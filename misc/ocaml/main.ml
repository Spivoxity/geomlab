(* main.ml *)

open Print

let do_phrase lexbuf =
  let cmd = Parser.para Lexer.token lexbuf in
  Interp.exec cmd

let load_file fn =
  let chan = open_in fn in
  let lexbuf = Lexing.from_channel chan in
  Lexer.lnum := 1;
  try
    while true do
      try do_phrase lexbuf with
	Parsing.Parse_error ->
	  let tok = Lexing.lexeme lexbuf in
	  fprintf stderr "\"$\", line $: syntax error at token '$'\n" 
	    [fStr fn; fNum !Lexer.lnum; fStr tok];
	  raise Exit
    done
  with Exit -> 
    close_in chan

let read_stdin () =
  let lexbuf = Lexing.from_channel stdin in
  Lexer.lnum := 1;
  try
    while true do
      flush stderr;
      printf ">>> " [];
      try do_phrase lexbuf with
	Parsing.Parse_error ->
	  let tok = Lexing.lexeme lexbuf in
	  fprintf stderr "Syntax error at token '$'\n" [fStr tok];
	  Lexing.flush_input lexbuf
    done
  with Exit -> printf "\nBye\n" []

(* |main| -- main program *)
let main =
  for i = 1 to Array.length Sys.argv - 1 do
    load_file Sys.argv.(i)
  done;
  read_stdin ()

