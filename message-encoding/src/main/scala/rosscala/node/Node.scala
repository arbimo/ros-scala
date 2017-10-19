package rosscala.node

import monix.eval.{MVar, Task}
import org.ros.internal.message.Message
import org.ros.node.{ConnectedNode, DefaultNodeMainExecutor, NodeConfiguration}
import rosscala.message.ROSData
import rosscala.message.encoding.Convert
import scala.concurrent.duration._


import scala.collection.mutable

class Node(val cn: ConnectedNode) {

  private val publishersByName = mutable.Map[String, Task[Publisher[_]]]()

  def publisher[A](topic: String)
                (implicit ev: ROSData[A], encode: Convert[A,Message])
  : Task[Publisher[A]] = {
    publishersByName.synchronized {

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

}

object Node {

  private val nodeMainExecutor = DefaultNodeMainExecutor.newDefault()

  private val nodesByName = mutable.Map[String, Task[Node]]()

  def get(name: String): Task[Node] = {
    nodesByName.synchronized {
      nodesByName.getOrElseUpdate(name, {
        val connectedNodePromise = MVar.empty[ConnectedNode]
        Task {
          nodeMainExecutor.execute(new ROSNode(name, connectedNodePromise), NodeConfiguration.newPublic("localhost"))
        }.flatMap(_ => connectedNodePromise.read)
          .map(new Node(_))
          .memoize
      })
    }
    val y = MVar.empty[ConnectedNode]

    Task {
      nodeMainExecutor.execute(new ROSNode("test", y), NodeConfiguration.newPublic("localhost"))
    }.flatMap(_ => y.read)
      .map(new Node(_))
      .memoize
  }
}