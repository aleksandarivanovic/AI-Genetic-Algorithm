(ns clojure-ai-genalg.utils)

"Just a friendly reminder - 
AI stands for Aleksandar Ivanovic"

(defn log2 
  "Returns the logarithm for base 2" 
  [number]
  (/ (Math/log number) (Math/log 2)))

(defn factorial
  "Returns the factorial for a given number. Eg. 5! = 120"
 ([number]
   (factorial number 1))
 ([number accumulator]
   (if (= number 0) accumulator
     (recur (dec number) (* accumulator number)))))

(defn remove-nth-from-vec
  "Removes nth element from a vector"
  [vector nth]
  (vec (concat (subvec vector 0 nth) (subvec vector (inc nth)))))

(defn cost-from-to
  "Returns the cost to travel from city to city"
  [map-of-cities from to]
  (nth (nth map-of-cities from) to))

(defn replace-nth-char-in-string
  [string n with]
  (str (subs string 0 n) with (subs string (inc n))))