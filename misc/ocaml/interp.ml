open Types
open Environment
open Print

let rec try_each f =
  function
      [] -> raise Not_found
    | x::xs -> try f x with Not_found -> try_each f xs

let rec pattmatch patt value env =
  match patt with
      FixedPatt v -> 
	if v = value then env else raise Not_found
    | NamePatt x ->
	if islocal env x then
	  if find env x = value then env else raise Not_found
	else
	  define env x value
    | AnonPatt -> env
    | ConsPatt (ph, pt) ->
	begin match value with
	    ConsVal (h, t) ->
	      pattmatch pt t (pattmatch ph h env)
	  | _ -> raise Not_found
	end
    | PlusPatt (p, x) ->
	failwith "PlusPatt"

let rec try_clause (patts, body, guard) vs env =
  let env' = 
    List.fold_left2 
      (fun env1 patt value -> pattmatch patt value env1)
      (newblock env) patts vs in
  match eval guard env' with
      BoolVal true -> eval body env'
    | BoolVal false -> raise Not_found
    | _ -> failwith "boolean needed by guard"

and eval exp env =
  match exp with
      Const v -> v
    | Variable x ->
	begin try find env x with Not_found ->
	  begin match x.x_glodef with
	      Some v -> v
	    | None -> failwith (sprintf "$ is not defined" [fName x])
	  end
	end
    | IfExpr (e1, e2, e3) ->
	(match eval e1 env with
	    BoolVal true -> eval e2 env
	  | BoolVal false -> eval e3 env
	  | _ -> failwith "boolean needed by if")
    | Let (d, e1) ->
	eval e1 (elab d env)
    | Lambda clauses ->
	abstract clauses (ref env)
    | Apply (e1, es) ->
	apply (eval e1 env) (List.map (fun e -> eval e env) es)
    | ListExpr es ->
	List.fold_right (fun x xs -> ConsVal (x, xs)) 
	  (List.map (fun e -> eval e env) es) NilVal

and abstract clauses env =
  let h args = 
    try_each (fun cl -> try_clause cl args !env) clauses in
  FuncVal h

and apply f args = 
  match f with
      FuncVal h -> h args
    | _ -> failwith "calling a non-function"

and elab d env =
  match d with
      ValDefn (x, e) -> define env x (eval e env)
    | FunDefn (x, Lambda clauses) -> 
        let knot = ref arid in
	let env' = define env x (abstract clauses knot) in
	knot := env'; env'
    | FunDefn (x, _) ->
	failwith "RHS of a letrec must be a lambda"

let exec para =
  match para with
      Calculate e ->
	printf "--> $\n" [fValue (eval e arid)]
    | Define (ValDefn (x, e)) ->
	let v = eval e arid in
	x.x_glodef <- Some v;
	printf "--- $ = $\n" [fName x; fValue v]
    | Define (FunDefn (x, e)) ->
	let v = eval e arid in
	x.x_glodef <- Some v;
	printf "--- $ = $\n" [fName x; fValue v]

