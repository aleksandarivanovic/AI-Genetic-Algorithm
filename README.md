# GA-Attempt

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
  - A decimal representation of the path
  - A binary representation of the path (tied to the decimal representation)
  - The map of cities with costs
  - The actual path (Vector of cities that are visited in order)
  - The cost of the path
  
## individual.clj
  
  Functions
    - spawn-individual - Three variants:
      - One spawns a random individual for a given city map.
      - Second spawns a specific individual given a decimal representation.
      - Third spawns a hollow individual.
      
    - 
