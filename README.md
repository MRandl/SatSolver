## SatSolver

### Introduction
This is a SAT solver written in Scala 3. 
It uses only immutable collections and constant references, respecting the functional programming principles.

### Requirements
This project requires [Dotty](https://github.com/lampepfl/dotty), the experimental Scala3 compiler.
For general ease of use, it is recommended to use the Scala plugin for Intellij IDEA, which bundles Dotty when asked, but the [sbt](https://dotty.epfl.ch/) command-line tool works fine as well.

### What can it do ?
This piece of code can solve decent SAT problems given in the standard Dimacs CNF format. It can handle anywhere from 50 to 70 variables input in the time you need for a coffee. This is not competition-worthy but not ridiculous either.
### How do I run it ?
Download it, compile it with Dotty and run with one argument indicating the path where the file is located. Four examples are included, use res/XXX.dimacs to run one of those.
### Can I reuse/modify this project ?
Sure, but keep it open source and cite your sources. See the license for more details.
