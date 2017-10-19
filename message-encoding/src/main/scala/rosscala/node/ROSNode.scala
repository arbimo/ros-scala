package rosscala.node

import monix.eval.MVar
import monix.execution.Scheduler.Implicits.global
import org.ros.namespace.GraphName
import org.ros.node.{AbstractNodeMain, ConnectedNode}


class ROSNode(name: String, callback: MVar[ConnectedNode]) extends AbstractNodeMain {

  def getDefaultNodeName: GraphName = GraphName.of(name)

  override def onStart(node: ConnectedNode) {
    println("ROS node started.")
    callback.put(node).runAsync
  }
}
