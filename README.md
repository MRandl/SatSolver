## SatSolver

### Introduction
This is a functional SAT solver written in Scala 3.

### Requirements
This project requires [Dotty](https://github.com/lampepfl/dotty), the experimental compiler for Scala 3.
For general ease of use, it is recommended to use the Scala plugin for Intellij IDEA, which bundles Dotty when asked, but the [sbt](https://dotty.epfl.ch/) command-line tool works fine as well.

### What can it do ?
This piece of code can solve decent SAT problems given in the standard Dimacs CNF format.
On my old i5-4590 (4 cores, no HT), it can handle anywhere from 50 to 70 variables almost instantly, and 200-250 variables in the time you need for a coffee. This is not competition-worthy but not ridiculous either.
It was engineered to scale well with high core-density processors, your mileage will thus vary depending on your hardware.
### How do I run it ?
Download it, compile it with Dotty and run with one argument indicating the path where the file is located. Four examples are included, use res/XXX.dimacs to run one of those.
### Can I reuse/modify this project ?
Sure, but keep it open source and cite your sources. See the license for more details.
