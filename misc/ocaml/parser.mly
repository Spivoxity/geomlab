%token <Types.name> 	IDENT ATOM 
%token <Types.name>	OROP ANDOP RELOP APPOP ADDOP MULOP CONSOP MONOP
%token <int> 		NUMBER
%token <string>		STRING
%token 			AND ANDOP OR OROP EQUAL RELOP APPOP PLUS MINUS ADDOP
			MULOP CONS CONSOP
			LPAR RPAR COMMA SEMI BRA KET VBAR SEQ UMINUS ANON
			IF THEN ELSE LET DEFINE IN LAMBDA WHEN OP 
			EOT BADTOK

%left			OR OROP
%left			AND ANDOP
%left			EQUAL RELOP
%right			APPOP
%left			PLUS MINUS ADDOP
%left			MULOP
%right			CONS CONSOP

%type<Types.para>	para
%start 			para

%{
open Types

let monop w a = Apply (Variable w, [a])
let binop w a b = Apply (Variable w, [a; b])

let fname = ref (name "*anon*")
%}

%%

para :
    expr SEMI				{ Calculate $1 }
  | DEFINE defn	SEMI			{ Define $2 } 
  | EOT					{ raise Exit } ;

expr :
    LET defn IN expr			{ Let ($2, $4) }
  | LAMBDA LPAR patts RPAR expr		{ Lambda [($3, $5, 
  						    Const (BoolVal true))] }
  | cond				{ $1 } ;

cond :
    IF cond THEN cond ELSE cond		{ IfExpr ($2, $4, $6) }
  | term				{ $1 } ;

term :
    term OR term			{ IfExpr ($1, Const tt, $3) }
  | term OROP term			{ binop $2 $1 $3 }
  | term AND term			{ IfExpr ($1, $3, Const ff) }
  | term ANDOP term			{ binop $2 $1 $3 }
  | term EQUAL term			{ binop (name "=") $1 $3 }
  | term RELOP term			{ binop $2 $1 $3 }	
  | term APPOP term			{ binop $2 $1 $3 }
  | term PLUS term			{ binop (name "+") $1 $3 }
  | term MINUS term			{ binop (name "-") $1 $3 }
  | term ADDOP term			{ binop $2 $1 $3 }
  | term MULOP term			{ binop $2 $1 $3 }
  | term CONS term			{ binop (name ":") $1 $3 }
  | term CONSOP term			{ binop $2 $1 $3 }
  | factor				{ $1 } ;

factor :
    MINUS factor			{ monop (name "~") $2 }
  | UMINUS factor			{ monop (name "~") $2 }
  | MONOP factor			{ monop $1 $2 }
  | NUMBER				{ Const (NumVal $1) }
  | ATOM				{ Const (AtomVal $1) }
  | STRING				{ Const (StringVal $1) }
  | name				{ Variable $1 }
  | name LPAR exprs RPAR		{ Apply (Variable $1, $3) }
  | LPAR expr RPAR			{ $2 }
  | BRA exprs KET			{ ListExpr $2 } ;

name :
    IDENT				{ $1 }
  | OP op				{ $2 } ;
    
op :
    OR					{ name "or" }
  | OROP				{ $1 }
  | AND					{ name "and" }
  | ANDOP				{ $1 }
  | EQUAL				{ name "=" }
  | RELOP				{ $1 }
  | APPOP				{ $1 }
  | PLUS				{ name "+" }
  | MINUS				{ name "-" }
  | ADDOP				{ $1 }
  | MULOP				{ $1 }
  | CONS				{ name ":" }
  | CONSOP				{ $1 }
  | UMINUS				{ name "~" }
  | MONOP				{ $1 } ;

defn :
    name EQUAL expr			{ ValDefn ($1, $3) }
  | clauses				{ FunDefn (!fname, Lambda $1) } ;

clauses :
    clause				{ [$1] }
  | clause VBAR clauses			{ [$1] @ $3 } ;

clause :
    name LPAR patts RPAR EQUAL expr guard  
      { fname := $1; ($3, $6, $7) } ;

guard :
    WHEN expr				{ $2 }
  | /* empty */				{ Const (BoolVal true) } ;

patts :
    /* empty */				{ [] }
  | pattlist				{ $1 } ;

pattlist :
    patt				{ [$1] }
  | patt COMMA pattlist			{ [$1] @ $3 } ;

patt :
    patt PLUS number			{ PlusPatt ($1, $3) }
  | patt CONS patt			{ ConsPatt ($1, $3) }
  | name				{ NamePatt $1 }
  | ANON				{ AnonPatt }
  | ATOM				{ FixedPatt (AtomVal $1) }
  | number				{ FixedPatt (NumVal $1) }
  | STRING				{ FixedPatt (StringVal $1) }
  | LPAR patt RPAR			{ $2 }
  | BRA patts KET			
      { List.fold_right (fun ph pt -> ConsPatt (ph, pt)) 
	  $2 (FixedPatt NilVal) } ;
    
exprs :
    /* empty */				{ [] }
  | exprlist				{ $1 } ;

exprlist :
    expr				{ [$1] }
  | expr COMMA exprlist			{ [$1] @ $3 } ;

number :
    NUMBER				{ $1 }
  | MINUS NUMBER			{ - $2 }
  | UMINUS NUMBER			{ - $2 } ;

