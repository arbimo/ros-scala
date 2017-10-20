package rosscala.node

import monix.eval.MVar
import monix.execution.Scheduler.Implicits.global
import org.ros.namespace.GraphName
import org.ros.node
import org.ros.node.{AbstractNodeMain, ConnectedNode, DefaultNodeMainExecutor, NodeConfiguration}

import scala.concurrent.{Future, Promise}


class ROSNode(name: String, callback: Promise[ConnectedNode])(implicit env: Env) extends AbstractNodeMain {

  def getDefaultNodeName: GraphName = GraphName.of(name)

  override def onStart(node: ConnectedNode) {
    env.log.info(s"Node connected: $name")
    callback.success(node)
  }

  override def onError(node: org.ros.node.Node, throwable: Throwable) {
    env.log.error(s"Failed: $name with: $throwable")
    callback.failure(throwable)
  }
}

object ROSNode {

  private val nodeMainExecutor = DefaultNodeMainExecutor.newDefault()

  def start(name: String)(implicit env: Env): Future[ConnectedNode] = {
    val connectedNodePromise: Promise[ConnectedNode] = Promise()
    env.log.info(s"Starting up node '$name'")
    nodeMainExecutor.execute(new ROSNode(name, connectedNodePromise), NodeConfiguration.newPublic("localhost"))
    connectedNodePromise.future
  }

}
