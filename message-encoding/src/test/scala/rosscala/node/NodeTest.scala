package rosscala.node

import monix.eval.{MVar, Task}
import org.ros.internal.message.Message
import org.ros.node.{ConnectedNode, DefaultNodeMainExecutor, NodeConfiguration}
import rosscala.message.ROSData
import rosscala.message.encoding.Convert
import rosscala.messages.std_msgs.{Bool, Header}

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration._

object NodeTest extends App {

  import rosscala._

  defaultNode
    .onErrorHandle(ex => {println(s"Error: $ex"); sys.exit(1)})
    .runAsync

  "/mytopic" ! Header(seq = 3)
  "/mytopic" ! Header(seq = 4)
  "/mytopic" ! Header(seq = 5)
  "/mytopic" ! Header(seq = 6)
  println("done")

  Thread.sleep(5000)
  defaultNode.map(_.cn.shutdown()).runAsync
  sys.exit(0)
}
