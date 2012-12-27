open Types

module IdMap = Map.Make(struct type t = int let compare = compare end)

type 'a environment = int list * 'a IdMap.t

let arid = ([], IdMap.empty)

let newblock (locs, dict) = ([], dict)

let define (locs, dict) x v = (x.x_id::locs, IdMap.add x.x_id v dict)

let find (locs, dict) x = IdMap.find x.x_id dict

let islocal (locs, dict) x = List.mem x.x_id locs
