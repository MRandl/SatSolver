import DpllSatSolver.solve

import scala.collection.immutable.HashSet

/**
 * A quick and dirty Dimacs CNF parser. Ill-formed inputs result in undefined behavior.
 * When the parser class is published for scala 3, I'll make a cleaner one
 * */
object InputManager {
  def noSanitizeInput(str: Iterable[String]) =
    val strings = str.filterNot(_.startsWith("c")).tail //ignore the first element and comments
    val clauses = strings.map(str => HashSet from (str.dropRight(1) split (" ") filterNot (_.isEmpty) map (_.toLong)) )
    DpllSatSolver.solve(HashSet.from(clauses))
}
