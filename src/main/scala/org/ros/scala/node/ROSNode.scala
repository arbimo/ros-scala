package org.ros.scala.node

import org.ros.namespace.GraphName
import org.ros.node.{ConnectedNode, AbstractNodeMain}
import scala.concurrent.promise

class ROSNode extends AbstractNodeMain {
  var cn : ConnectedNode = null

  def getDefaultNodeName: GraphName = GraphName.of("fape/messages") // TODO: parameterize

  override def onStart(node: ConnectedNode) {
    println("ROS Node onStart")
    this.cn = node
    ROSNode.connectedNodePromise.success(node)
  }
}

object ROSNode {
  protected[node] var connectedNodePromise = promise[ConnectedNode]()
  def connectedNodeFuture = connectedNodePromise.future
}