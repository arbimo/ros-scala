import monix.eval.{Coeval, Task}
import monix.execution.Scheduler
import org.ros.internal.message.Message
import rosscala.message.ROSData
import rosscala.message.encoding.Convert
import rosscala.node.{Env, Node}

import scala.concurrent.Await
import scala.concurrent.duration._

package object rosscala {



  object defaults {
    implicit val scheduler: Scheduler = monix.execution.Scheduler.Implicits.global

    implicit val env = Env()

    implicit val defaultNode: Task[Node] = Node.get(env)


  }

  object utils {
    implicit class TaskOps[A](t: Task[A]) {

      /** Block until we get the result.
        * Should be used with great care as normally we should not block any thread.
        * The purpose of this definition is to provide easier defaults for REPL and scripts.
        * However it should not be used anywhere in the main source code.
        */
      def await: A = {
        t.runSyncMaybe(defaults.scheduler) match {
          case Right(a) => a
          case Left(future) => Await.result(future, Int.MaxValue.seconds)
        }
      }

      def logError(msg: => String)(implicit env: Env): Task[A] =
        t.onErrorHandleWith(ex => {
          env.log.error(msg+": "+ex.getLocalizedMessage)
          Task.raiseError(ex)
        })
    }
  }

  object scripting {
    import utils._

    /** Provides additional operations that treat strings as topics. */
    implicit class TopicOps(topicName: String)
                           (implicit env: Env, node: Task[Node], scheduler: Scheduler) {

      def ![A](msg: A)(implicit ev: ROSData[A], encode: Convert[A,Message]): Unit = {
        node
          .flatMap(_.publisher[A](topicName))
          .map(_.publish(msg))
          .logError(s"Could not publish to [$topicName]")
          .await
      }
    }
  }

}
