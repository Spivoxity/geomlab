open Types
open Environment
open Print

let primfail x =
  failwith (sprintf "Bad arguments to primitive $" [fStr x])

let primitive x h = (x, FuncVal h)

let binop x f =
  let h = function [NumVal a; NumVal b] -> NumVal (f a b) | _ -> primfail x in
  (x, FuncVal h)

let monop x f =
  let h = function [NumVal a] -> NumVal (f a) | _ -> primfail x in
  (x, FuncVal h)

let compare x f =
  let h = function [NumVal a; NumVal b] -> BoolVal (f a b) | _ -> primfail x in
  (x, FuncVal h)

let unary x f =
  let h = function [u] -> f u | _ -> primfail x in
  (x, FuncVal h)

let binary x f =
  let h = function [u; v] -> f u v | _ -> primfail x in
  primitive x h

let explode s =
  let n = String.length s in
  let rec loop i = if i >= n then [] else s.[i] :: loop (i+1) in
  loop 0

let make_list xs = 
  List.fold_right (fun h t -> ConsVal (h, t)) xs NilVal

let _ = 
  List.iter (fun (x, v) -> let xx = name x in xx.x_glodef <- Some v)
    [binop "+" (+); binop "-" (-); binop "*" ( * ); binop "div" (/);
      binop "mod" (mod); monop "~" (~-); compare "=" (=); 
      compare "<" (<); compare "<>" (<>); compare ">=" (>=); 
      compare ">" (>); 
      unary "numeric" (function NumVal _ -> tt | _ -> ff);
      unary "head" (function ConsVal (h, t) -> h | _ -> primfail "head");
      unary "tail" (function ConsVal (h, t) -> t | _ -> primfail "tail"); 
      binary ":" (fun h t -> ConsVal (h, t));
      primitive "^" 
	(function [StringVal a; StringVal b] -> StringVal (a ^ b)
	  | _ -> primfail "^");
      primitive "explode" (function [StringVal s] -> 
	    make_list (List.map (fun c -> StringVal (String.make 1 c)) 
	      (explode s))
	| _ -> primfail "explode")]
