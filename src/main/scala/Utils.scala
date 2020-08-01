import java.util.concurrent._

import scala.util.DynamicVariable

object Utils {

  private val scheduler = new DefaultTaskScheduler
  private val forkJoinPool = new ForkJoinPool

  val numOfThreads = 2 * Runtime.getRuntime.availableProcessors()

  def task[T](body: => T): ForkJoinTask[T] = {
    scheduler.schedule(body)
  }

  private class DefaultTaskScheduler {
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
