import java.util.concurrent._

import scala.util.DynamicVariable

object Utils {
  
  private val forkJoinPool = new ForkJoinPool
  
  private def schedule[T](body: => T): ForkJoinTask[T] = {
    val t = new RecursiveTask[T] {
      def compute = body
    }
    Thread.currentThread match {
      case wt: ForkJoinWorkerThread =>
        t.fork()
      case _ =>
        forkJoinPool.execute(t)
    }
    t
  }
  
  def task[T](body: => T): ForkJoinTask[T] =
    schedule(body)
}
