open Print

type name = { x_id: int; x_text: string; mutable x_glodef: value option }

and value =
    NumVal of int
  | BoolVal of bool
  | StringVal of string
  | AtomVal of name
  | NilVal
  | ConsVal of value * value
  | FuncVal of (value list -> value)

let nametab = Hashtbl.create 1009
let namecount = ref 0

let name s = 
  try Hashtbl.find nametab s with Not_found ->
    let x = { x_id = !namecount; x_text = s; x_glodef = None } in
    incr namecount; Hashtbl.add nametab s x; x

let fName x = fStr x.x_text

let rec fValue =
  function
      NumVal x -> fNum x
    | BoolVal b -> if b then fStr "true" else fStr "false"
    | StringVal s -> fMeta "\"$\"" [fStr s]
    | AtomVal x -> fMeta "#$" [fName x]
    | NilVal -> fStr "[]"
    | ConsVal (hd, tl) -> fMeta "[$$]" [fValue hd; fTail tl]
    | FuncVal _ -> fStr "<function>"

and fTail =
  function
      NilVal -> fStr ""
    | ConsVal (hd, tl) -> fMeta ", $$" [fValue hd; fTail tl]
    | x -> fMeta ". $" [fValue x]

let tt = BoolVal true and ff = BoolVal false

type patt =
    FixedPatt of value
  | NamePatt of name
  | AnonPatt
  | ConsPatt of patt * patt
  | PlusPatt of patt * int

type expr =
    Const of value
  | Variable of name
  | IfExpr of expr * expr * expr 
  | Let of defn * expr
  | Lambda of (patt list * expr * expr) list
  | Apply of expr * expr list
  | ListExpr of expr list

and defn =
    ValDefn of name * expr
  | FunDefn of name * expr

type para =
    Calculate of expr
  | Define of defn

