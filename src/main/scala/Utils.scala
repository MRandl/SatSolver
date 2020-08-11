import java.util.concurrent._

object Utils {
  
  private val forkJoinPool = new ForkJoinPool
  
  def task[T](body: => T): ForkJoinTask[T] = {
    val t = new RecursiveTask[T] {
      def compute = body
    }
    Thread.currentThread match {
      case wt: ForkJoinWorkerThread => t.fork()
      case _ => forkJoinPool.execute(t)
    }
    t
  }
}
