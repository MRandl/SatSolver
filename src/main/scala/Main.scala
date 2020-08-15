import DpllSatSolver._

import scala.io.Source.fromFile

object Main {
  
  def main(args: Array[String]): Unit = {
    println("Detected " + Runtime.getRuntime.availableProcessors + " cores.")
    val inputSeq = fromFile(if(args.size == 0) then "res/2-easy.dimacs" else args(0)).getLines().toSeq
    val a = System.currentTimeMillis()
    val res = InputManager.noSanitizeInputAndRun(inputSeq)
    println("Solving took : " + (System.currentTimeMillis() - a) + "ms")
    res match {
      case None => println("The given formula is unsatisfiable")
      case Some(value) => println("The given formula is satisfiable using the following mapping : " + value)
    }
  }
}
