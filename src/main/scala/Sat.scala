import scala.annotation.tailrec
import scala.language.implicitConversions
/**
 * Sat is a singleton object that exposes :
 *  1) a trait Formula and its subclasses, and
 *  2) a solve() method to search for a valid assignment to a given Formula.
 * @author mathis.randl@epfl.ch
 * */
object Sat {
  
  type Assignment = List[(String, Boolean)]
  
  sealed trait Formula {
    def asConst = asInstanceOf[Const]
  }
  
  final case class Variable(name: String)        extends Formula {override def toString = name}
  final case class And(f : Formula, s : Formula) extends Formula {override def toString = "(" + f + " And " + s + ")"}
  final case class Or (f : Formula, s : Formula) extends Formula {override def toString = "(" + f + " Or "  + s + ")"}
  final case class Not(formula: Formula)         extends Formula {override def toString = "Not(" + formula + ")"}
  final case class Const(boolean: Boolean)       extends Formula {override def toString = boolean.toString}

  implicit def stringToVariable(s : String) : Formula = Variable(s)
  
  private def freeVarsOf(formula: Formula): List[String] =
    @tailrec def run(workStack: List[Formula], resultQueue: List[String]): List[String] = workStack match {
      case Nil => resultQueue
      case Variable(name)     :: tail => run(tail, name :: resultQueue) //if leaf, pop and enqueue
      case Or(first, second)  :: tail => run(second  :: first :: tail, resultQueue) //otherwise, push branches
      case And(first, second) :: tail => run(second  :: first :: tail, resultQueue)
      case Not(formula)       :: tail => run(formula :: tail, resultQueue)
      case Const(_)           :: tail => run(tail, resultQueue)
    }
    run(List(formula), Nil).distinct
  
  private def nodeUpdate(original: Formula, a: Formula, b: Formula, f: Formula => Formula, isAnd: Boolean) : Formula =
    val x = f(a)
    val y = f(b)
    if((x eq a) && (y eq b)) //if we don't need to allocate a whole new branch, we reuse the old one
      original
    else
      if(isAnd) And(x, y) else Or(x, y)
  
  private def loopEliminate(formula: Formula) : Formula =
    def eliminate(formula: Formula) : Formula = formula match {
      case And(a, Const(b)) => if(b) eliminate(a) else Const(false)
      case And(Const(b), a) => if(b) eliminate(a) else Const(false)
      case Or (a, Const(b)) => if(b) Const(true) else eliminate(a)
      case Or(Const(b), a)  => if(b) Const(true) else eliminate(a)
      case Not(Const(a))    => Const(!a)
      case And(a, b)        => nodeUpdate(formula, a, b, eliminate, true)
      case Or (a, b)        => nodeUpdate(formula, a, b, eliminate, false)
      case Not(a)           => val x = eliminate(a); if(x ne a) Not(x) else formula
      case Variable(_) | Const(_) => formula
    }
    val next = eliminate(formula)
    if(next eq formula) next else loopEliminate(next)

  def toCnf(formula: Formula) : Formula =
    def negFix(formula: Formula) : Formula = formula match {
      case Not(Not(x))    => negFix(x)
      case Not(Const(x))  => Const(!x)
      case Not(And(x, y)) => Or (negFix(Not(x)), negFix(Not(y)))
      case Not(Or(x, y))  => And(negFix(Not(x)), negFix(Not(y)))
      case Not(x)         => val a = negFix(x); if(a ne x) Not(a) else formula
      case Or(x, y)       => nodeUpdate(formula, x, y, negFix, false)
      case And(x, y)      => nodeUpdate(formula, x, y, negFix, true)
      case Variable(_) | Const(_) => formula
    }
    def dist(formula: Formula) : Formula = formula match {
      case Or(x, And(y, z)) => And(Or(x, y), Or(x, z))
      case Or(And(y, z), x) => And(Or(y, x), Or(z, x))
      case Or (x, y)        => nodeUpdate(formula, x, y, dist, false)
      case And(x, y)        => nodeUpdate(formula, x, y, dist, true)
      case Not(x)           => val a = dist(x); if(a ne x) Not(a) else formula
      case Variable(_) | Const(_) => formula
    }
    val b = negFix(dist(formula))
    if(b eq formula) b else toCnf(b)
  
  def assign(formula: Formula, varName : String, value: Boolean) : Formula = formula match {
    case Variable(name)     => if(name == varName) Const(value) else formula
    case And(first, second) => nodeUpdate(formula, first, second, assign(_, varName, value), true)
    case Or (first, second) => nodeUpdate(formula, first, second, assign(_, varName, value), false)
    case Not(something)     => val x = assign(something, varName, value); if(x ne something) Not(x) else formula
    case Const(boolean)     => formula
  }
  
  def solve(a: Formula): Option[Assignment] =
    def run(form : Formula, assignment : Assignment, freeVars : List[String]) : Option[Assignment] =
      freeVars match {
        case Nil => Option.when(loopEliminate(form).asConst.boolean)(assignment)
        case curVar :: restOfVars =>
          run(assign(form, curVar, true),  (curVar, true)  +: assignment, restOfVars) orElse
          run(assign(form, curVar, false), (curVar, false) +: assignment, restOfVars)
      }
    run(a, Nil, freeVarsOf(a))
}