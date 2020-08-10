import scala.collection.immutable.HashMap
import Utils._

import scala.util.{Failure, Success}

/**
 * DpllSatSolver is a singleton object that exposes the following :
 *
 * - A type Literal that stores SAT literals (=boolean variables with polarities)
 *
 * - A case class Clause that encapsulates a Set of Literals (interpreted as joined by Or's)
 *
 * - A case class Formula that encapsulates a Set of Clauses (interpreted as joined by And's)
 *
 * - A method solve that attempts to find a solution to the given problem.
 *
 *
 * The solve method uses the original DPLL algorithm. This is quite basic compared to state-of-the-art ones,
 * but will do the trick for the purposes of this project, which is mainly self-educational.
 *
 * @author Mathis Randl <mathis.randl@epfl.ch>
 **/
object DpllSatSolver {

  type Literal    = Long // define -x == Not(x), 0 is reserved
  type Assignment = Map[Literal, Boolean]
  type Clause     = Set[Literal]
  type Formula    = Set[Clause] //formula is assumed to be in cnf format

  /**
   * @return all of the literals of the formula
   **/
  private def literalsOf(formula: Formula): Set[Literal] =
    formula.flatten

  /**
   * @return all of the literals, but without negations
   **/
  private def absoluteLiteralsOf(formula: Formula): Set[Literal] =
    literalsOf(formula) map (x => if (x < 0) then -x else x)

  /**
   * @return all the pure (that is, always appearing with the same polarity) literals
   **/
  private def pureLiteralsOf(formula: Formula): Set[Literal] =
    val lits = literalsOf(formula)
    lits filterNot (lit => lits contains -lit)

  /**
   * @return all literals that make up a clause all by themselves
   **/
  private def unitLiteralsOf(formula: Formula): Set[Literal] =
    (formula filter (x => x.size == 1)).flatten 

  private def assign(formula: Formula, literal: Literal, value: Boolean): Formula =
    val newLit: Literal = if (value) then literal else -literal
    formula filterNot (_.contains(newLit)) map (x => x - (-newLit))
    //get rid of the clauses that contain the literal (they're satisfied) and delete the opposite literals
  
  private def pickLiteral(formula: Formula): Literal =
    absoluteLiteralsOf(formula).head //todo  room for improvement : currently, branches are chosen according to their hash value.

  private def checkIfDone(formula: Formula): Option[Boolean] =
    if (formula.isEmpty)
      Some(true) //satisfiable with the given assignment
    else if (formula exists (_.isEmpty))
      Some(false) //unsatisfiable
    else
      None //god only knows, we need to keep working

  def solve(formula: Formula): Option[Assignment] =

    def run(formula: Formula, assignment: Assignment, threadNum: Int): Option[Assignment] =
      val unitLiterals = unitLiteralsOf(formula)
      val united = unitLiterals.foldLeft(formula)((f, l) => assign(f, l, true)) //no need to think hard for units
      val newAssignment = assignment ++ unitLiterals.map(l => if (l < 0) (-l, false) else (l, true))

      checkIfDone(united) match {
        case Some(true) => Some(newAssignment)
        case Some(false) => None
        case None =>
          val currHead = pickLiteral(united)
          val first  =  task(run(assign(united, currHead, true),  newAssignment + ((currHead, true)),  threadNum))
          val second =       run(assign(united, currHead, false), newAssignment + ((currHead, false)), threadNum)
          second orElse first.join //second goes first so that we only block if really needed
      }

    val pureLits = pureLiteralsOf(formula)
    val purified = pureLits.foldLeft(formula)((f, l) => assign(f, l, true))
    val firstAssignment = HashMap.from(pureLits.map(l => if (l < 0) (-l, false) else (l, true)))
    run(purified, firstAssignment, 1) map 
      (assigned => absoluteLiteralsOf(formula).foldLeft(assigned)((opa, l) => if (opa.isDefinedAt(l)) opa else opa + ((l, true))))
}