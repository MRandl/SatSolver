## SatSolver

### Introduction
This is a SAT solver written in Scala 3. 

### Requirements
This project requires [Dotty](https://github.com/lampepfl/dotty), the experimental Scala 3 compiler.
For general ease of use, it is recommended to use the Scala plugin for Intellij IDEA, which bundles Dotty when asked to, but the [sbt](https://dotty.epfl.ch/) command-line tool works fine as well.

##

### What can it do now ?
This piece of code can solve a SAT problem given directly in its internal representation.

### What will it do when it is done ?
It will be able to solve a SAT problem where the input is given in JSON format, using an efficient algorithm (conflict-driven or DPLL) and separating workloads on a given number of processor cores.

##
### Future releases 
This project is still WIP. Ideally, it would correct the following current problems :
* The algorithm is for now quite basic, it's not DPLL and definitely not conflict-driven.
* There is no input support for now (input needs to be hardcoded and recompiled). I could write a quick and dirty parser, but I would like to do it the Scala way, using the Parser class. I am not experienced enough to use this class yet.
* The algorithm does not support parallel computations yet.  This will be addressed last.