(ns clojure-ai-genalg.individual
  (:use clojure-ai-genalg.utils)
  (:use clojure-ai-genalg.city-examples))



(defrecord Individual [binary-rep decimal-rep path map-of-cities total-cost])

(defn get-random-decimal-rep-from-map
  "Generates a <b>random</b> decimal representation of a path within a city map"
  ([map-of-cities]
    (rand-int (factorial (count map-of-cities)))
    ))

(defn get-binary-rep-from-decimal-rep
  "Calculates an appropriate binary representation of a decimal representation"
  ([decimal-rep num-of-bits]
    (clojure.pprint/cl-format nil 
                              (clojure.string/replace "~tempToken,'0d" #"tempToken" (str (int num-of-bits)))
                              (java.lang.Integer/toString decimal-rep 2))))

(defn binary-rep-to-dec
  [binary-rep]
  (java.lang.Integer/parseInt binary-rep 2))

(defn decimal-rep-to-path
  "Traslates a given decimal representation to a specific path vector"
  ([decimal-rep map-of-cities]
   (let [towns (vec (range 0 (count map-of-cities))) result-path (vector)] 
     (decimal-rep-to-path (count towns) towns decimal-rep result-path)))
  ([count towns n result-path]
   (if (= count 1) (conj result-path (nth towns 0))
     (let [town-ind-to-pick-up (/ n (clojure-ai-genalg.utils/factorial (dec count)))]
       (recur (dec count)
              (clojure-ai-genalg.utils/remove-nth-from-vec towns town-ind-to-pick-up)
              (mod n (clojure-ai-genalg.utils/factorial (dec count)))
              (conj result-path (nth towns town-ind-to-pick-up)))))))

(defn rate-path
  "Rates a path vector on a given map"
  ([map-of-cities path-vector]
    (rate-path map-of-cities path-vector 0))
  ([map-of-cities path-vector cost]
    (if (= 1 (count path-vector)) cost
      (recur map-of-cities (subvec path-vector 1)
             (+ cost (cost-from-to map-of-cities
                                   (nth path-vector 0)
                                   (nth path-vector 1)))))))

(defn spawn-individual 
  "Generates a random individual for a given map of cities.
   Generates a specific individual for a given map and decimal representation. Will generate a hollow individual if decimal representation is negative."
  ([map-of-cities]
    (let [randdec (get-random-decimal-rep-from-map map-of-cities)] 
      (->Individual (get-binary-rep-from-decimal-rep randdec (java.lang.Math/ceil (log2 (factorial (count map-of-cities))))) 
                    randdec 
                    (decimal-rep-to-path randdec map-of-cities) 
                    map-of-cities 
                    (rate-path map-of-cities (decimal-rep-to-path randdec map-of-cities)))))
  ([map-of-cities dec-rep]
    (if (< dec-rep 0)
      (->Individual 0
                    0 
                    0 
                    map-of-cities 
                    0)
	    (let [the-path (decimal-rep-to-path dec-rep map-of-cities)]
       (->Individual (get-binary-rep-from-decimal-rep dec-rep (java.lang.Math/ceil (log2 (factorial (count map-of-cities)))))
	                  dec-rep 
	                  the-path
	                  map-of-cities 
	                  (rate-path map-of-cities the-path))))))

(defmulti is-valid? "Returns true if individual is valid. An individual is not valid if it's 
decimal representation is larger than the amount of possibilities" class)
(defmethod is-valid? Individual [individual] 
  (< (:decimal-rep individual) (factorial (count (:map-of-cities individual)))))


(defmulti rate-individual "Rates an individual's path and returns the rated individual" class)
(defmethod rate-individual Individual [individual] 
  (assoc individual 
         :total-cost 
         (rate-path (:map-of-cities individual) (:path individual))))

(defn bin-and-dec-matched?
  "Returns true if binary adequately represents the decimal transformation"
  [bin-rep dec-rep]
  (= (binary-rep-to-dec bin-rep) dec-rep))


(defmulti force-fix 
 "NOTE: This function will only fix issues caused by mutation. 
If you generate an unrealistic individual it will not be of any aid. Do not call it in that manner.
If the individual's decimal representation presents a path that is impossible in the current city scheme, this generates a valid individual" class)
(defmethod force-fix Individual [individual]
  (spawn-individual (:map-of-cities individual) (- (:decimal-rep individual) (clojure-ai-genalg.utils/factorial (count (:map-of-cities individual))) )))

(defmulti heal-self "Sometimes cross-stitching and mutations are hard on an individual.
An individual can heal itself using this method. If the individual is okay, it will simply return the individual. If not, it will forcibly fix the individual.
Using it's binary representation, the individual generates other fields and returns a healed individual" class)
(defmethod heal-self Individual [individual]
  (if (not (bin-and-dec-matched? (:binary-rep individual) (:decimal-rep individual)))
    (let [individ2 (assoc individual :decimal-rep (binary-rep-to-dec (:binary-rep individual)))]
    (if (not (is-valid? individ2))
      (force-fix individ2)  
      (assoc individ2
             :path (decimal-rep-to-path (:decimal-rep individ2) (:map-of-cities individ2))
             :total-cost (rate-path (:map-of-cities individ2) (decimal-rep-to-path (:decimal-rep individ2) (:map-of-cities individ2))))
      ))
  (let [new-dec-rep (binary-rep-to-dec (:binary-rep individual))]
    (assoc individual
               :decimal-rep new-dec-rep
               :path (decimal-rep-to-path new-dec-rep (:map-of-cities individual))
               :total-cost (rate-path (:map-of-cities individual) (decimal-rep-to-path new-dec-rep (:map-of-cities individual)))))))

