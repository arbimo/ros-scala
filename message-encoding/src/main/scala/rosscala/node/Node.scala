package rosscala.node

import monix.eval.{Coeval, MVar, Task}
import org.ros.internal.message.Message
import org.ros.node.ConnectedNode
import rosscala.message.ROSData
import rosscala.message.encoding.Convert

import scala.concurrent.duration._
import scala.collection.mutable

class Node(val cn: ConnectedNode) {

  @volatile private val publishersByName = mutable.Map[String, Task[Publisher[_]]]()

  def publisher[A](topic: String)
                (implicit ev: ROSData[A], encode: Convert[A,Message])
  : Task[Publisher[A]] = {
      // definition of default publisher, there is constant 500 milliseconds delay
      // to make sure it is available before being used
      val defaultPublisher = Task {
        new Publisher[A](cn.newPublisher[Message](topic, ev._TYPE))
      }.delayResult(500.milliseconds)
        .memoize

      publishersByName
        .getOrElseUpdate(topic, defaultPublisher)
        .asInstanceOf[Task[Publisher[A]]]
  }

}

object Node {

  @volatile private val nodesByName = mutable.Map[String, Task[Node]]()

  def get(name: String): Task[Node] = {
    nodesByName.getOrElseUpdate(name, {
      Task.deferFuture { ROSNode.start(name) }
        .map(new Node(_))
        .timeoutTo(2.seconds, Task.raiseError(new RuntimeException("Didn't get a connected node after two seconds.")))
        .memoize
    })
  }
}