import monix.eval.Task
import monix.execution.Scheduler
import org.ros.internal.message.Message
import rosscala.message.ROSData
import rosscala.message.encoding.Convert
import rosscala.node.Node

import scala.concurrent.Await
import scala.concurrent.duration._

package object rosscala {

  implicit val scheduler: Scheduler = monix.execution.Scheduler.Implicits.global

  implicit val defaultNode: Task[Node] = Node.get("test")


  implicit class TopicOps(topicName: String)
                         (implicit node: Task[Node], scheduler: Scheduler) {

    def ![A](msg: A)(implicit ev: ROSData[A], encode: Convert[A,Message]): Unit = {
      val f = node
        .flatMap(_.publisher[A](topicName))
        .map(_.publish(msg))
        .runAsync
      Await.result(f, 30.seconds)
    }
  }

}
