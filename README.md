# A Genetic Algorithm - With Binary numbers, TSP and Healthcare

Greetings, this is a project I've done while trying to learn Clojure.
It was done with Test driven development, where-as I figured out what I needed done, wrote a test for it, and then kept hammering at the function until it passed all my tests. Interestingly enough, it worked.

You will notice that some things are done well, and some are done less well. It is all but part of the progress while learning how to do things in Clojure.

The algorithm itself is loosely based on the paper "New binary representation in Genetic Algorithms for solving TSP by
mapping permutations to a list of ordered numbers" by Amin Mohebifar, with many new features I added on the fly.

#Outline

The algorithm follows the standard GA protocol:
  1. Make a population
  2. Crossover
  3. Mutate
  4. Select best individuals
  5. Limit population
  6. Back to 2. until some condition is met. In this case, number of iterations.

However, what makes the difference is the lifecycle of an individual. The individuals here have a chance to heal themselves from broken mutations, thus allowing them to live longer and potentially stumble upon being the solution. But more about free-healthcare later.

Every individual (represented by the record aptly named "Individual") represents a path with:
  - A decimal representation of the path (An integer)
  - A binary representation of the path (tied to the decimal representation) (A string of 0's and 1's)
  - The map of cities with costs (A 2D vector, with 0 on locations 0-0, 1-1, 2-2 etc.)
  - The actual path (Vector of cities that are visited in order)
  - The cost of the path (By reading the map)
  
## individual.clj
  
Functions
  - spawn-individual - Three variants:
    - One spawns a random individual for a given city map.
    - Second spawns a specific individual given a decimal representation.
    - Third spawns a hollow individual.
      
  - get-random-decimal-rep-from-map - Gets a random valid path representation for a given city.
  - get-binary-rep-from-decimal-rep - Calculates a binary representation from a decimal representation.
  - binary-rep-to-dec - Calculates a decimal representation from a binary number.
  - decimal-rep-to-path - Calculates a path from given decimal representation. This requires some more explanation, the original writer of the paper has come up with a genial way to translate a decimal number to a single unique represntation of a path and the algorithm goes as follows:
    1. Have a list of all available cities. Eg (0 1 2 3), and we want a path for number 13.
    2. c is number of cities, so in this case c = 4.
    3. Counter i=1 to i<=c 
    3.1 If size of remaining available cities is 1, just add the remaining city to the end.  
      q = DecimalNumber div NumberOfRemainingCombinationsForRemainingCities (as in: Factorial of c-i)
      q = 13 / Fact(4-1) = 13 / 6 = 2
      r = DecimalNumber mod NumberOfRemainingCombinationsForRemainingCities (as in: Factorial of c-i)
      r = 13 % Fact(4-1) = 13 / 6 = 1
    4. Get city number located on the location q. For q=2, and list (0 1 2 3), the city is 2. Place this city in path list. Path list now is (2)
    5. Replace 13 with r. So new number is 1.
    6. Increment i and repeat.
    
  Example execution:

  
