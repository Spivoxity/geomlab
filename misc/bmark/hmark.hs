module Main where
import System.Environment

f :: Int -> Int
f 0 = 1
f (n+1) = f n + g n

g :: Int -> Int
g 0 = 1
g (n+1) = g n + f n

main = 
  do args <- getArgs; putStrLn (show (f (read (head args))))
