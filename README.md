# A Genetic Algorithm - With Binary numbers, TSP and Healthcare

Greetings, this is a project I've done while trying to learn Clojure.
It was done with Test driven development, where-as I figured out what I needed done, wrote a test for it, and then kept hammering at the function until it passed all my tests. Interestingly enough, it worked.

The project uses Leningen and has some dependencies like clojure.tools.trace, which I used for debugging.

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

#How to run?

Simply navigate to the ai_genalg.clj and run the REPL. From here you can call the function "genetic-algorithm" with the parameters.

One possible way of running it is:
  - (genetic-algorithm
  clojure-ai-genalg.city-examples/city-example2
  0.4
  mutator1
  10
  100)

After a bit, you should get a list of most potent individuals.

## ai-genalg.clj

Execution starts with the function "genetic-algorithm". It calls "init-population" once, and then calls "crossover-step", "mutate-step" and "kill-excess" every turn until it runs out of iterations.

### init-population

This function repeatedly spawns random individuals until it makes enough of them.

### crossover-step

This function splits the population in two halves and calls crossover-individuals on ordered elements of both halves. So first with first, second with second, etc. and returns a list of resulting individuals.

- crossover-individuals

crossover-individuals splits the binary representation in the middle and joins the opposite ends, for example:

00111
11010

00 | 111
11 | 010

00 + 010, 11 + 111

00010 and 11111


### mutate-step

This function calls the mutator function with chance on every element of the population and returns the resulting individuals.

You can define your own mutators, just make sure you return an individual.

## individual.clj
  
Functions:
  - spawn-individual - Three variants:
    - One spawns a random individual for a given city map.
    - Second spawns a specific individual given a decimal representation.
    - Third spawns a hollow individual.
      
  - get-random-decimal-rep-from-map - Gets a random valid path representation for a given city.
  - get-binary-rep-from-decimal-rep - Calculates a binary representation from a decimal representation.
  - binary-rep-to-dec - Calculates a decimal representation from a binary number.
  - decimal-rep-to-path - Calculates a path from given decimal representation. (Explained later)
  - rate-path - Rates a path for a given map of cities and path-vector.
  - bin-and-dec-matched? - Returns true if binary and decimal representation are corresponding. Eg. 5 == 101.

Individual specific functions:
  - is-valid? - Returns true if the individual decimal representation is valid. It can be invalid if the number is possible in binary, but has no path to represent. Eg. For map (0 1 2), number of combinations is 6. The minimum number of bits to represent all solutions of 6 is 3 (b110 = 6). However 111 is a possibility due to mutations, but is invalid.
  - rate-individual - rates the individual's path and returns an individual that has a cost.
  - force-fix - Fixes an individual. This function gives the individuals with invalid binary representations to be pulled back into the pool of valid solutions. It does a very simple thing: If any number is invalid, it means it's larger than the amount of possibilities. So by substracting the number of possibilities from the number, we get a number that is surely valid. This function will NOT work if you create an unrealistic individual.
  - heal-self - Individual can heal itself after a mutation or a crossover. Recovering values, rates, paths, basically becoming a fully functional individual without any inconsistencies.
  
## core_test.clj

I have tried renaming it and I broke everything. Then I left it alone.

I used this namespace for testing during my development. If anyone decides to work on this code, you can use my tests to make sure your things are most-likely working correctly still.

# Explanations
### decimal-rep-to-path explained
This requires some more explanation, the original writer of the paper has come up with a genial way to translate a decimal number to a single unique represntation of a path and the algorithm goes as follows:
  1. Have a list of all available cities. Eg (0 1 2 3), and we want a path for number 13.
  2. c is number of cities, so in this case c = 4.
  3. Counter i=1 to i<=c 
  4. If size of remaining available cities is 1, just add the remaining city to the end.  
    - q = DecimalNumber div NumberOfRemainingCombinationsForRemainingCities (as in: Factorial of c-i)
    - q = 13 / Fact(4-1) = 13 / 6 = 2
    - r = DecimalNumber mod NumberOfRemainingCombinationsForRemainingCities (as in: Factorial of c-i)
    - r = 13 % Fact(4-1) = 13 / 6 = 1
  5. Get city number located on the location q. For q=2, and list (0 1 2 3), the city is 2. Place this city in path list. Path list now is (2)
  6. Replace 13 with r. So new number is 1.
  7. Increment i and repeat.
    
  Example execution:

  Available cities: (0 1 2 3)
  Decimal number: 13
  Path: Unknown
  
  - Start
  - i=1, c=4, Available cities: (0 1 2 3), Decimal number: 13
    - q= 13 / Fact(4-1) = 13 / 6 = 2
    - r= 13 % Fact(4-1) = 13 % 6 = 1 
    - Get city at location 2 from (0 1 2 3) => 2. Place city in path => (2). Remove city from Available cities.
    - Avialable cities are now (0 1 3)
    - Decimal number = r
    
  - i=2, c=4, Available cities: (0 1 3), Decimal number: 1
    - q = 1 / Fact(4-2) = 1 / 2 = 0
    - r = 1 % Fact(4-2) = 1 % 2 = 1
    - Get city at location 0 from (0 1 3) => 0. Place city in path => (2 0). Remove city from available cities.
    - Decimal number = 1
    
  - i=3, c=4, Available cities: (1 3), Decimal number: 1
     - q = 1 / Fact(4-3) = 1 / 1 = 1
     - r = 1 / Fact(4-3) = 1 % 1 = 0
     - Get city from location 1 from (1 3) => 3, Place city in path => (2 0 3). Remove city from available cities.
     - Decimal number = 1
    
  - i=4, c=4, Available cities: (1), Decimal number: 1
     - One city left! Add it to path => (2 0 3 1)
    
    Done! For map (0 1 2 3) and 13, we get path (2 0 3 1)

#Conclusion

I like Clojure. At first, I was very much repulsed because I was so used to thinking in OOP. After some time, I realised there is some eerie charm about building everything from... simple things. So my train of thought basically went from "This looks like a thing that can do this-this and that" to "I need a function that has this input and that output" and basically ended up making a function for everything, and then used those functions to make bigger functions. Like Legos.

The algorithm also works, I am quite glad I finished it, since it required so much effort on thinking differently and overcoming so many practical issues. I am certain there is plenty of room for optimizations and new features, and I'll surely work on some of them as time goes on.

If you do, for some odd reason, decide to work on this algorithm, please give me a poke, let me know what you did. I'd love to have a look.

Credits to Amin Mohebifar for thinking of the TSP Binary representation.
Thanks to the professor for letting me do this thing and encouraging me to finish it.

And thank you for reading this far down. Seriously.

Kindest regards,
Aleksandar Ivanovic
