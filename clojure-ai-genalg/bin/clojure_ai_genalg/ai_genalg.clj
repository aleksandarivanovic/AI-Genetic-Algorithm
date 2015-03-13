(ns clojure-ai-genalg.ai-genalg
  (:use clojure.pprint)
  (:use clojure-ai-genalg.individual)
  (:use clojure-ai-genalg.utils))

"Just a friendly reminder - 
AI acronym scattered about stands for Aleksandar Ivanovic, not Intellgence of any sort."

(defn- kill-off-excess
  [population max-size]
  (take max-size (sort-by :total-cost population)))

(defn init-population
  "Initializes a population and sorts it. (Less cost -> better)"
  [size map-of-cities]
  (sort-by :total-cost (take size (repeatedly #(spawn-individual map-of-cities)))))

(defn mutator1
  "If chance falls under threshold, picks a random bit and flips it.
NOTE: 49 is Char(1) converted to Int. 
Otherwise returns umodified individual."
  [individual chance]
  (if (< (rand) chance)
    (let [number (int (* (rand) (count (:binary-rep individual))))]
      (heal-self (assoc individual
                        :binary-rep (if (= (int (nth (:binary-rep individual) number)) 49)
                                      (replace-nth-char-in-string (:binary-rep individual) number 0)
                                      (replace-nth-char-in-string (:binary-rep individual) number 1)))))
    individual))

(defn- mutate-function
  "Mutates an individual according to a tactic. Feel free to add your own under a new function, just make sure you return an individual!"
  [individual tactic chance]
  (tactic individual chance))

(defn crossover-individuals
  "Returns a list of two new individuals"
  [individual1 individual2]
  (let [polovina (quot (count (:binary-rep individual1)) 2)]
    (list
      (heal-self (assoc individual1 
                        :binary-rep (str (subs (:binary-rep individual1) 0 polovina) (subs (:binary-rep individual2) polovina))))
      (heal-self (assoc individual2 
                        :binary-rep (str (subs (:binary-rep individual2) 0 polovina) (subs (:binary-rep individual1) polovina)))))))

(defn crossover-step
  "Splits the population in two. Uses the crossover function on indexed elements. (First with first, second with second etc)"
  ([population]
    (crossover-step population '() 0 (quot (count population) 2)))
  ([population working-population index stop-at]
    (if (= index stop-at)
      working-population
      (recur population
             (into working-population (crossover-individuals (nth population index) (nth population (+ stop-at index))))
             (inc index)
             stop-at))))

(defn mutate-step
  "Mutates the elements of the population and returns the result"
  ([population tactic-mutation chance]
    (mutate-step population tactic-mutation chance '() (dec (count population))))
  ([population tactic-mutation chance working-population elemsleft]
    (if (zero? elemsleft)
      working-population
      (recur
        population
        tactic-mutation
        chance
        (conj working-population (tactic-mutation (nth population elemsleft)chance))
        (dec elemsleft)))))

(defn genetic-algorithm
  ([map-of-cities
    mutation-chance
    tactic-mutation
    population-size
    no-of-iterations]
    (if (zero? no-of-iterations)
      "Well, you need at least ONE iteration. You are just generating a population."
      (genetic-algorithm 
        map-of-cities 
        mutation-chance 
        tactic-mutation 
        population-size 
        no-of-iterations 
        (init-population population-size map-of-cities)))
    )
  ([map-of-cities
    mutation-chance
    tactic-mutation
    population-size
    no-of-iterations
    working-population]
    (if (zero? no-of-iterations)
      working-population
      (recur map-of-cities 
             mutation-chance 
             tactic-mutation
             population-size 
             (dec no-of-iterations) 
             (kill-off-excess 
               (into (into working-population 
                           (crossover-step working-population))
                     (mutate-step working-population tactic-mutation mutation-chance))
               population-size)))))

