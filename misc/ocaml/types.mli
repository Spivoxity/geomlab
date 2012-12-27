type name = 
  { x_id : int; 
    x_text : string; 
    mutable x_glodef : value option; }

and value =
    NumVal of int
  | BoolVal of bool
  | StringVal of string
  | AtomVal of name
  | NilVal
  | ConsVal of value * value
  | FuncVal of (value list -> value)

val name : string -> name

val fName : name -> Print.arg
val fValue : value -> Print.arg

val tt : value
val ff : value

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

and defn = ValDefn of name * expr | FunDefn of name * expr

type para = Calculate of expr | Define of defn
