import solvers.DpllSat._
import scala.collection.immutable.HashSet
import scala.language.implicitConversions

object Main {
  
  def main(args: Array[String]): Unit = {
    println(solve(Formula(HashSet(Clause(HashSet(1)), Clause(HashSet(-2, 1))))))
  }
}
