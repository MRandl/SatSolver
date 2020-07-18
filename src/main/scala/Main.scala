import Sat._
import scala.language.implicitConversions

object Main {
  def main(args: Array[String]): Unit = {
    val toSolve = toCnf(Or(And(And(And(Or(Not("a"), "b"), And("a", Not("c"))), Or(Not("c"), "f")), And("a", "e")), Not(Or(And("g", "a"), And("f", "g")))))
    println("Is the following assignment solvable ? : " + toSolve)
    solve(toSolve) match {
      case Some(value) => println("It can be solved using the following assignment : " + value.reverse.toString.drop(5).dropRight(1))
      case None => println("It cannot possibly be solved.")
    }
  }
}
