package rosscala.node

import monix.eval.{Coeval, MVar, Task}
import org.ros.internal.message.Message
import org.ros.node.ConnectedNode
import rosscala.message.ROSData
import rosscala.message.encoding.Convert

import scala.concurrent.duration._
import scala.collection.mutable

class Node(val cn: ConnectedNode) {

  private val publishersByName = mutable.Map[String, Task[Publisher[_]]]()

  def publisher[A](topic: String)
                (implicit env: Env, ev: ROSData[A], encode: Convert[A,Message])
  : Task[Publisher[A]] = {

    publishersByName.synchronized {
      publishersByName.getOrElseUpdate(topic, Publisher.of(cn, topic))
        .asInstanceOf[Task[Publisher[A]]]
    }
  }

}

object Node {

  private val nodesByName = mutable.Map[String, Task[Node]]()

  def get(implicit env: Env): Task[Node] = {
    nodesByName.synchronized {
      val nodeTask =
        Task.deferFuture {
          ROSNode.start(env.nodeName.name) }
          .map(new Node(_))
          .memoize

      nodesByName.getOrElseUpdate(env.nodeName.name, nodeTask)
    }
  }
}