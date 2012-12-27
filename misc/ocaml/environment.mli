open Types

type 'a environment

val islocal: 'a environment -> name -> bool

val find : 'a environment -> name -> 'a

val define : 'a environment -> name -> 'a -> 'a environment

val newblock : 'a environment -> 'a environment

val arid : 'a environment
