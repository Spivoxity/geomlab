set DEFN {define N = 30;}

set INIT {{ init -- initial state }
define init = 
  let c = [[6,2], [7,3], [8,1], [8,2], [8,3], [19,15], [20,15], [21,15]] in
  image(N, N, function (p) if member(p, c) then black else white);}

set BLOBIFY {{ blobify -- make circular pixels }
define blobify(img) = above([ beside([ pixel(img, [x, y]) 
  | x <- [0..width(img)-1] ]) | y <- reverse([0..height(img)-1]) ]);}

set NEXT {{ next -- compute next state }
define next(state) = 
  image(N, N, function (p) 
    if viable(p, state) then black else white);

{ viable -- test if cell is alive in next state }
define viable(p, img) =
  let val(q) = if pixel(img, q) <> white then 1 else 0 in
  let s = sum([ val(q) | q <- region(p) ]) + val(p)/2 in s > 2 and s < 4;

{ region -- neighbours of a call }
define region([x, y]) = 
  [ [u, v] | u <- [x-1..x+1], v <- [y-1..y+1] when u <> x or v <> y ];}

set NEXT2 {{ next -- compute next state }
define next(state) = 
  image(N, N, function (p) 
    if viable(p, state) then fade(pixel(state, p)) else white);

{ viable -- test if cell is alive in next state }
define viable(p, img) =
  let val(q) = if pixel(img, q) <> white then 1 else 0 in
  let s = sum([ val(q) | q <- region(p) ]) + val(p)/2 in s > 2 and s < 4;

{ region -- neighbours of a call }
define region([x, y]) = 
  [ [u, v] | u <- [x-1..x+1], v <- [y-1..y+1] when u <> x or v <> y ];

{ fade -- fade from red to black }
define fade(c) =
  if c = white then red else rgb(rpart(c)*3/4, 0, 0);}

set ANIMATE {{ animate -- show frames under slider control }
define animate(frames) =
  let n = length(frames) in
  slide(function (t) nth(int((n-0.001)*t), frames));}

set LIFE {let life = map(blobify, repeat(200, next, init)) in animate(life)}

set X99 {define W = 15; define H = 7;

define blobify(img) = above([ beside([ pixel(img, [x, y]) 
  | x <- [0..width(img)-1] ]) | y <- reverse([0..height(img)-1]) ]);

define data =
  [ '.....*....*....', '.*....*..*.*.*.', '*.******....*..',
    '.*......**....*', '*.*...**.*..*.*', '..*****..*..**.',
    '**.*..*....*...' ];

define init =
  let c('*') = black | c('.') = white in
  let pxs = map (function (s) map(c, explode(s)), reverse(data)) in
  image(W, H, function ([x, y]) nth(x, nth(y, pxs)));

define next(state) = 
  image(W, H, function (p) 
    if viable(p, state) then fade(pixel(state, p)) else white);

define viable(p, img) =
  let score(q) = if pixel(img, q) <> white then 1 else 0 in
  let s = sum([ score(q) | q <- region(p) ]) + score(p)/2 in s > 2 and s < 4;

define region([x, y]) = 
  [ [u, v] | u <- [x-1..x+1], v <- [y-1..y+1] when u <> x or v <> y ];

define fade(c) =
  if c = white then red else rgb(rpart(c)*3/4, 0, 0);

define animate(frames) =
  let n = length(frames) in
  slide(function (t) nth(int((n-0.001)*t), frames));

let life = map(blobify, repeat(3, next, init)) in animate(life)}

set IMAGES {image(200, 200,
    function ([x, y]) rgb(x/200, 0, y/200))}

proc map {f xs} {
     set result {}
     foreach x $xs {lappend result [$f $x]}
     return $result
}

proc quote {text} {
    return "\"$text\""
}

proc prep {example} {
    set text [join $example "\n\n"]
    set lines [split $text "\n"]
    set body [join [map quote $lines] ",\n"]
    return "\[$body\]"
}

proc examples {args} {
     puts [join [map prep $args] ",\n"]
}

puts {define _init() =
  let join(x:xs) = foldl ((function (s, y) s ^ chr(10) ^ y), x, xs) in
  let subst(s) = implode(map(function (c) if c = "'" then chr(34) else c, 
              		     explode(s))) in
  let examples(menu) = _examples(map(subst, map(join, menu))) in
  examples([}

examples \
    [list {{ colours }} {black $ white $ red $ rgb(3/4, 1/4, 1/2)}] \
    [list {{ images }} $IMAGES] \
    [list {{ init }} $DEFN $INIT init] \
    [list {{ blobify }} $DEFN $INIT $BLOBIFY blobify(init)] \
    [list {{ next }} $DEFN $INIT $BLOBIFY $NEXT blobify(next(init))] \
    [list {{ slide }} {slide(function (t) rgb(1-t, 0, t))}] \
    [list {{ life }} $DEFN $INIT $BLOBIFY $NEXT2 $ANIMATE $LIFE] \
    [list {{ LIFE }} $X99]

puts {])}

