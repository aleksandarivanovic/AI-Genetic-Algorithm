(ns clojure-ai-genalg.core-test
  (:require [clojure.test :refer :all]
            [clojure-ai-genalg.core :refer :all]
            [clojure-ai-genalg.individual :refer :all]))


(deftest test-dec-to-path
  (testing "Path 4 in Example 1 should be [2 0 1]"
    (is (= [2 0 1] (decimal-rep-to-path 4 clojure-ai-genalg.city-examples/city-example1)))
    (is (not= [2 0 1] (decimal-rep-to-path 0 clojure-ai-genalg.city-examples/city-example1)))))

(deftest test-get-binary-rep-from-decimal-rep
  (testing "Binary representation test"
           (is (= (str 0 0 0 1 0 1) (get-binary-rep-from-decimal-rep 5 6)))
           (is (not= (str 1) (get-binary-rep-from-decimal-rep 9 1)))
           (is (= (str 1 0 0 1) (get-binary-rep-from-decimal-rep 9 1)))))

(deftest test-individual-is-valid?
  (testing "Idividual is-valid? function test"
           (is (true? (is-valid? 
                        (->Individual 
                          0
	                             3
	                             0 
	                             clojure-ai-genalg.city-examples/city-example1
	                             0))))
           (is (not (true? (is-valid? 
                             (->Individual 
                               0
                          33
                          0 
                          clojure-ai-genalg.city-examples/city-example1
                          0)))))))

(deftest test-bin-to-dec
  (testing "Binary to decimal"
           (is (= 341 (binary-rep-to-dec "101010101")))
           (is (= 25 (binary-rep-to-dec "000011001")))
           (is (not= 42 (binary-rep-to-dec "000101")))))


(deftest test-rate-path
  (testing "Path rating"
           (is (= 7 (rate-path clojure-ai-genalg.city-examples/city-example1 [2 0 1])))))


(deftest test-rate-individual
  (testing "Individual rating itself"
           (is (= (spawn-individual clojure-ai-genalg.city-examples/city-example1 4) 
                  (rate-individual (->Individual 
                                     (get-binary-rep-from-decimal-rep 4 (java.lang.Math/ceil (clojure-ai-genalg.utils/log2 (clojure-ai-genalg.utils/factorial (count clojure-ai-genalg.city-examples/city-example1)))))
                                     4
                                     (decimal-rep-to-path 4 clojure-ai-genalg.city-examples/city-example1)
                                     clojure-ai-genalg.city-examples/city-example1
                                     0))))))

(deftest test-healing
  (testing "Testing healing of individuals"
           (is (= (spawn-individual clojure-ai-genalg.city-examples/city-example1 4) 
                  (heal-self (rate-individual (->Individual 
                                     (get-binary-rep-from-decimal-rep 4 (java.lang.Math/ceil (clojure-ai-genalg.utils/log2 (clojure-ai-genalg.utils/factorial (count clojure-ai-genalg.city-examples/city-example1)))))
                                     94
                                     (decimal-rep-to-path 4 clojure-ai-genalg.city-examples/city-example1)
                                     clojure-ai-genalg.city-examples/city-example1
                                     0)))))
           (is (= (spawn-individual clojure-ai-genalg.city-examples/city-example1 4)        
                  (heal-self (rate-individual (->Individual 
                                     (get-binary-rep-from-decimal-rep 4 (java.lang.Math/ceil (clojure-ai-genalg.utils/log2 (clojure-ai-genalg.utils/factorial (count clojure-ai-genalg.city-examples/city-example1)))))
                                     94
                                     (decimal-rep-to-path 4 clojure-ai-genalg.city-examples/city-example1)
                                     clojure-ai-genalg.city-examples/city-example1
                                     0)))))
           (is (not= (spawn-individual clojure-ai-genalg.city-examples/city-example1 1) 
                  (heal-self (rate-individual (->Individual 
                                     (get-binary-rep-from-decimal-rep 4 (java.lang.Math/ceil (clojure-ai-genalg.utils/log2 (clojure-ai-genalg.utils/factorial (count clojure-ai-genalg.city-examples/city-example1)))))
                                     94
                                     (decimal-rep-to-path 4 clojure-ai-genalg.city-examples/city-example1)
                                     clojure-ai-genalg.city-examples/city-example1
                                     0)))))))


(deftest test-force-fix
  (testing "Testing forcefix"
           (is (= (spawn-individual clojure-ai-genalg.city-examples/city-example2 17)
                  (force-fix (->Individual 
                                     (get-binary-rep-from-decimal-rep 7 (java.lang.Math/ceil (clojure-ai-genalg.utils/log2 (clojure-ai-genalg.utils/factorial (count clojure-ai-genalg.city-examples/city-example2)))))
                                     7
                                     (decimal-rep-to-path 7 clojure-ai-genalg.city-examples/city-example2)
                                     clojure-ai-genalg.city-examples/city-example2
                                     0))))))

