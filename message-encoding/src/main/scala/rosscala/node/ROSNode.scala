package rosscala.node

import monix.eval.MVar
import monix.execution.Scheduler.Implicits.global
import org.ros.namespace.GraphName
import org.ros.node
import org.ros.node.{AbstractNodeMain, ConnectedNode, DefaultNodeMainExecutor, NodeConfiguration}

import scala.concurrent.{Future, Promise}


class ROSNode(name: String, callback: Promise[ConnectedNode]) extends AbstractNodeMain {

  def getDefaultNodeName: GraphName = GraphName.of(name)

  override def onStart(node: ConnectedNode) {
    println("ROS node started.")
    callback.success(node)
  }

  override def onError(node: org.ros.node.Node, throwable: Throwable) {
    println(s"Ros node failure: $throwable")
    callback.failure(throwable)
  }
}

object ROSNode {

  private val nodeMainExecutor = DefaultNodeMainExecutor.newDefault()

  def start(name: String): Future[ConnectedNode] = {
    val connectedNodePromise: Promise[ConnectedNode] = Promise()
    nodeMainExecutor.execute(new ROSNode(name, connectedNodePromise), NodeConfiguration.newPublic("localhost"))
    connectedNodePromise.future
  }

}
