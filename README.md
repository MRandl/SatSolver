## SatSolver

### Introduction
This is a parallel and functional SAT solver written in Scala 3.

### Requirements
This project requires [Dotty](https://github.com/lampepfl/dotty), the experimental compiler for Scala 3. If you intend on running this outside of an IDE, you may want to use SBT or Bloop as Scala build tools, as this project is not maintained for the others.

### What can it do ?
This piece of code can solve decent SAT problems given in the standard Dimacs CNF format.
On my old i5-4590, it can handle anywhere from 50 to 70 variables almost instantly, and 200-250 variables in the time you need for a coffee. This is hence not meant to be used in any serious context.

### How do I run it ?
Download it, compile it with Dotty and run with one argument indicating the path where the Dimacs file to solve is located. Four examples are included, use res/\<something\>.dimacs to run one of those.

### Can I reuse/modify this project ?
Sure, but keep it open source and cite your sources. See the license for more details.
