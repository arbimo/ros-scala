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



//  val result = Ctx.default.foreach(_ => println("AAAAAAAAAAAAAAAAAAAAAAa"))
//  Await.result(result, 10.seconds)
//  println(result)

//  defaultNode
//    .flatMap(_.publisher[Bool]("coucou"))
//    .map(_.publish(Bool(false)))
//    .runAsync

  "/mytopic" ! Header(seq = 3)
  "/mytopic" ! Header(seq = 4)
  println("done")

  Thread.sleep(2000)
  defaultNode.map(_.cn.shutdown()).runAsync
}
