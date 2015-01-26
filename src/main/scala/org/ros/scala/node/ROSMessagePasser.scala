package org.ros.scala.node

import akka.actor.{ActorRef, Actor}
import akka.event.Logging
import org.ros.internal.loader.CommandLineLoader
import scala.collection.JavaConversions._
import org.ros.node.DefaultNodeMainExecutor
import scala.concurrent._
import org.ros.node.topic.Publisher
import org.ros.internal.message
import org.ros.scala.message.AbsMsg
import adream_actions.{Pick, PickResponse}
import org.ros.node.service.ServiceResponseListener
import org.ros.exception.RemoteException
import scala.concurrent.duration._

class ROSMessagePasser extends Actor {
  val log = Logging(context.system, this)

  log.info("Loading ROSMessagePasser")

  // mimic the launch configuration in RosRun to start the node
  val loader = new CommandLineLoader(asJavaList(List("org.ros.scala.node.ROSNode")))
  val nodeClassName = loader.getNodeClassName
  println("Loading class: " + nodeClassName)
  val nodeConfiguration = loader.build()
  val nodeMain = loader.loadClass(nodeClassName)
  println(nodeMain)
  val nodeMainExecutor = DefaultNodeMainExecutor.newDefault()

  import scala.concurrent.ExecutionContext.Implicits.global
  // start the node in another thread
  val f = future {
    nodeMainExecutor.execute(nodeMain, nodeConfiguration)
  }

  // wait for the connected node future.
  val node = Await.result(ROSNode.connectedNodeFuture, 10 seconds)

  var subscriptions = Map[String, List[ActorRef]]()
  var publishers = Map[String, Publisher[message.Message]]()

  for((topic, tipe) <- List(("/pr2/nav_goal", std_msgs.String._TYPE), ("/test3", geometry_msgs.Pose._TYPE))) {
    val publisher = node.newPublisher[message.Message](topic, tipe)
    publishers = publishers.updated(topic, publisher)
  }

  def receive = {
    case Subscribe(topic) => {
      log.info("Received subscribe for: "+topic)
      if(!subscriptions.contains(topic)) {
        val subscriber = node.newSubscriber[std_msgs.String](topic, std_msgs.String._TYPE)
        subscriber.addMessageListener(new MessageForwarder[std_msgs.String](this, topic))
      }
      subscriptions = subscriptions.updated(topic, sender() :: subscriptions.getOrElse(topic, List()))
    }
    case Publish(topic, msg: AbsMsg) => {
      if(!publishers.contains(topic)) {
        val publisher = node.newPublisher[message.Message](topic, msg._TYPE)
        publishers = publishers.updated(topic, publisher)
      }
      val pub = publishers(topic)
      println(msg)
      println(msg.toRawMessage.getFields.head.getValue)
//      val msg = pub.newMessage()
//      msg.setData(data)
      pub.publish(msg)
    }
    case ServiceRequest(service, msg, reqID) =>
      val caller = sender()
      val client = node.newServiceClient[message.Message, message.Message]("pr2/picker/pick", Pick._TYPE)

      client.call(msg, new ServiceResponseListener[message.Message] {
        def onFailure(p1: RemoteException) {
          log.warning("Could not invoke service: " + service)
        }

        def onSuccess(p1: message.Message) {
          log.error("TODO: Received answer : " + p1)
//          caller tell(ServiceResponse(service, SString(p1.getStatus.toString), reqID), self) //TODO
        }
      })
    case x => println(x)
  }
}
