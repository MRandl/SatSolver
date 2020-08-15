import DpllSatSolver.solve

import scala.collection.immutable.{HashSet, Iterable}

/**
 * A quick and dirty Dimacs CNF parser. Ill-formed inputs throw errors relative to the
 * grammar rule violation (e.g., parsing 'foo' as a number will throw a 
 * typecasting-related exception)
 * */
object InputManager {
  def runOnInput(iteStr: Iterable[String]) =
    
    val clauses : Iterable[Set[Long]] = 
      iteStr
        .filterNot(_.head.isLetter) //remove comments and the first (useless) line
        .map(str => 
          HashSet.from(             //then for each line, read its numbers as Longs and put them in a set
            str.split(" ").filter(!_.isEmpty).map(_.toLong)
          ).filter(x => x != 0 && x != Long.MinValue) //remove the trailing zeros and illegal numbers
        )
        
    DpllSatSolver.solve(HashSet.from(clauses))
}
