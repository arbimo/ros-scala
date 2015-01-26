package org.ros.scala.node

import akka.actor.ActorRef
import java.util.concurrent.ConcurrentHashMap
import org.ros.message.MessageListener
import org.ros.namespace.GraphName
import org.ros.node.{ConnectedNode, AbstractNodeMain}


class ROSNode extends AbstractNodeMain {
  var cn : ConnectedNode = null

  def getDefaultNodeName: GraphName = GraphName.of("fape/messages") // TODO: parameterize

  override def onStart(node: ConnectedNode) {
    println("ROS Node onStart")
    this.cn = node
    ROSNode.connectedNodePromise.success(node)
  }
}

import scala.concurrent.{ future, promise }

object ROSNode {
  val connectedNodePromise = promise[ConnectedNode]()
  val connectedNodeFuture = connectedNodePromise.future
}