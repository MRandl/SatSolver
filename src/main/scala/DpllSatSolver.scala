import scala.collection.immutable.HashMap
import scala.util.{Failure, Success}
import Multithreading._

import scala.concurrent.Future


/**
 * DpllSatSolver is a singleton object that exposes the following :
 *
 * - A type Literal that stores SAT literals (=boolean variables with polarities)
 *
 * - A type Clause that equals a Set of Literals (interpreted as joined by Or's)
 *
 * - A type Formula that equals a Set of Clauses (interpreted as joined by And's). This means the program only supports
 * the CNF form, which is a usual constraint for SAT solvers
 *
 * - A method solve that attempts to find a solution to the given problem.
 *
 * The solve method uses the original DPLL algorithm. This is quite basic compared to state-of-the-art ones,
 * but will do the trick for the purposes of this project, which is mainly self-educational.
 *
 * @author Mathis Randl <mathis.randl@epfl.ch>
 **/
object DpllSatSolver {
  
  type Literal    = Long // define -x == Not(x), 0 and Long.MinValue are reserved
  type Assignment = Map[Literal, Boolean]
  type Clause     = Set[Literal]
  type Formula    = Set[Clause] //formula is assumed to be in cnf format

  /**
   * @param formula some Formula
   * @return all of the literals of the argument
   **/
  private def literalsOf(formula: Formula): Set[Literal] =
    formula.flatten

  /**
   * @param formula some Formula
   * @return all of the literals of the argument, but without negations
   **/
  private def absoluteLiteralsOf(formula: Formula): Set[Literal] =
    literalsOf(formula).map(x => if (x < 0) then -x else x)

  /**
   * @param formula some Formula
   * @return all the pure literals (that is, always appearing with the same polarity) of the argument
   **/
  private def pureLiteralsOf(formula: Formula): Set[Literal] =
    val lits = literalsOf(formula)
    lits.filterNot(literal => lits contains -literal)

  /**
   * @param formula some Formula
   * @return all literals that make up a clause all by themselves in the argument
   **/
  private def unitLiteralsOf(formula: Formula): Set[Literal] =
    formula.filter(_.size == 1).flatten

  /**
   * Assigns true to a literal within a formula and false to its opposite.
   * This takes polarity into account : for example, assigning true to -2 also assigns false to 2.
   *
   * @param formula some Formula containing the variable to assign
   * @param literal some Literal that will be assigned
   * @return a Formula that corresponds to the assignment
   * */
  private def assignTrue(formula: Formula, literal: Literal): Formula =
    formula.filterNot(_.contains(literal)).map(x => x - (-literal))
    //get rid of the clauses that contain the literal and delete the opposite literals

  /**
   * Returns some absolute literal within the formula.
   * @param formula some Formula
   * @return some literal guaranteed to be an absolute literal of the argument
   */
  private def pickLiteral(formula: Formula): Literal =
    absoluteLiteralsOf(formula).head

  private def checkIfDone(formula: Formula): Option[Boolean] =
    if (formula.isEmpty)
      Some(true) //satisfiable with the given assignment
    else if (formula.exists(_.isEmpty))
      Some(false) //unsatisfiable
    else
      None //god only knows, we need to keep working

  /**
   * Attempts to solve the given formula.
   * @return None when it is unsatisfiable, and Some(value) when it is satisfiable,
   *  using for example 'value' as an assignment
   */
  def solve(formula: Formula): Option[Assignment] =
    def run(formula: Formula, assignment: Assignment): Option[Assignment] =

      val unitLiterals = unitLiteralsOf(formula)
      val unitLess = unitLiterals.foldLeft(formula)((f, l) => assignTrue(f, l))
      val newAssignment = assignment ++ unitLiterals.map(l => if (l < 0) (-l, false) else (l, true))
      //that's a speedup : trivially get rid of all literals that appear only once.
      //this divides the work by two every time we find one, so definitely worth it

      checkIfDone(unitLess) match {
        case Some(true) => Some(newAssignment)
        case Some(false) => None
        case None =>
          val litH = pickLiteral(unitLess)
          val remote = task(run(assignTrue(unitLess,  litH), newAssignment + ((litH, true))))
          val local  =      run(assignTrue(unitLess, -litH), newAssignment + ((litH, false)))
          local orElse remote.join 
          //local is guaranteed to be done, so we check it first instead of blocking for remote
      }

    assert(!literalsOf(formula).exists(x => x == 0 || x == Long.MinValue), "Sat formula cannot contain illegal longs.")
    
    val pureLits = pureLiteralsOf(formula)
    val purified = pureLits.foldLeft(formula)((f, l) => assignTrue(f, l))
    val firstAssignment = HashMap.from(pureLits.map(l => if (l < 0) (-l, false) else (l, true)))
    
    run(purified, firstAssignment).map(assigned => 
      absoluteLiteralsOf(formula).foldLeft(assigned)((opa, l) => if (opa.contains(l)) opa else opa + ((l, true))))
}