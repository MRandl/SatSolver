import DpllSatSolver._

import scala.collection.immutable.HashSet

/**
 * A quick and dirty Dimacs CNF parser. Ill-formed inputs result in undefined behavior.
 * */
object InputManager {
  def noSanitizeInput(str: Iterable[String]) =
    val strings = str.filterNot(_.startsWith("c")).tail //ignore the first element and comments
    val clauses = strings.map(str => Clause(HashSet from (str.dropRight(1).split(" ").filterNot(_.isEmpty).map(_.toLong)) ))
    DpllSatSolver.solve(Formula(HashSet.from(clauses)))
}
