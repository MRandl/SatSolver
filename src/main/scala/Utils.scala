import java.util.concurrent._

import scala.util.DynamicVariable

object Utils {
  
  val numOfThreads = 2 * Runtime.getRuntime.availableProcessors()
  
  val scheduler = new DynamicVariable(new DefaultTaskScheduler)

  val forkJoinPool = new ForkJoinPool

  def task[T](body: => T): ForkJoinTask[T] = {
    scheduler.value.schedule(body)
  }
  
  class DefaultTaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T] = {
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
  }
}
