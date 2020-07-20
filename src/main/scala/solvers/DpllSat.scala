package solvers

import scala.collection.IterableOnce
import scala.collection.immutable.HashMap

object DpllSat {
  
  type Literal = Long // define -x == Not(x), 0 is reserved
  type Assignment = Map[Literal, Boolean]
  
  final case class Clause(literals : Set[Literal]){
    override def toString : String = "(" + literals.map(_.toString).mkString(" Or ") + ")"
  }
  final case class Formula(clauses : Set[Clause]){
    override def toString : String = "(" + clauses.map(_.toString).mkString(" And ") + ")"
  }
  
  /**
   * @return all of the literals of the formula
   * */
  private def literalsOf(formula: Formula) : Set[Literal] = 
    formula.clauses flatten (clause => clause.literals)
  
  /**
   * @return all of the literals, but without negations
   * */
  private def absoluteLiteralsOf(formula: Formula) : Set[Literal] =
    literalsOf(formula) map (x => if(x < 0) then -x else x)
  
  /**
   * @return all the pure (that is, always appearing with the same polarity) literals
   * */
  private def pureLiteralsOf(formula: Formula) : Set[Literal] =
    val lits = literalsOf(formula)
    lits filterNot (x => lits.contains(-x))
  
  /**
   * @return all literals that make up a clause all by themselves
   * */
  private def unitLiteralsOf(formula: Formula) : Set[Literal] =
    formula.clauses filter (x => x.literals.size == 1) flatten (clause => clause.literals)
  
  private def assign(formula : Formula, literal: Literal, value : Boolean) : Formula =
    val newLit : Literal = if(value) then literal else -literal
    Formula(formula.clauses filterNot (_.literals.contains(newLit)) map (x => Clause(x.literals - (-newLit))))
    //get rid of the clauses that contain the literal (they're satisfied) and delete the opposite literals
  
  private def checkIfDone(formula: Formula) : Option[Boolean] =
    if(formula.clauses.isEmpty)
      Some(true) //satisfiable with the given assignment
    else if (formula.clauses exists (_.literals.isEmpty))
      Some(false) //unsatisfiable
    else
      None //god only knows, we need to keep working
  
  def solve(formula: Formula) : Option[Assignment] =
    
    def run(formula: Formula, assignment: Assignment) : Option[Assignment] =
      val unitlits = unitLiteralsOf(formula)
      val united   = unitlits.foldLeft(formula)((f, l) => assign(f, l, true))
      val newAssignment = assignment ++ unitlits.map(l => if(l < 0) (-l, false) else (l, true))
      val checkDone = checkIfDone(united)
      checkDone match {
        case Some(true)  => Some(newAssignment)
        case Some(false) => None
        case None => 
          val currHead = absoluteLiteralsOf(united).head
          run(assign(united, currHead, true), newAssignment + ((currHead, true))) orElse run(assign(united, currHead, false), newAssignment + ((currHead, false)))
      }
      ???

    val purelits : Set[Literal] = pureLiteralsOf(formula)
    val purified : Formula = purelits.foldLeft(formula)((f, l) => assign(f, l, true))
      //note : we don't assign true to all variables, but to the literals : for example, -3 is true <=> 3 is false
    val assignment = HashMap.from(purelits.map(l => if(l < 0) (-l, false) else (l, true)))
    run(purified, assignment)
}
