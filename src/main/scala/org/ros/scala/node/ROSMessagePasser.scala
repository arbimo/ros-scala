package org.ros.scala.node

import akka.actor.{ActorRef, Actor}
import akka.event.Logging
import org.ros.internal.loader.CommandLineLoader
import scala.collection.JavaConverters._
import org.ros.node.DefaultNodeMainExecutor
import scala.concurrent._
import org.ros.node.topic.Publisher
import org.ros.internal.message
import org.ros.scala.message.{MessageConverter, AbsMsg}
import org.ros.node.service.ServiceResponseListener
import org.ros.exception.{ServiceNotFoundException, RosRuntimeException, RemoteException}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class ROSMessagePasser extends Actor {
  val log = Logging(context.system, this)

  log.info("Loading ROSMessagePasser")

  // mimic the launch configuration in RosRun to start the node
  private val loader = new CommandLineLoader(List("org.ros.scala.node.ROSNode").asJava)
  private val nodeClassName = loader.getNodeClassName

  private val nodeConfiguration = loader.build()
  private val nodeMain = loader.loadClass(nodeClassName)

  private val nodeMainExecutor = DefaultNodeMainExecutor.newDefault()

  // start the node in another thread
  future {
    nodeMainExecutor.execute(nodeMain, nodeConfiguration)
  }

  // wait for the connected node future.
  private val node = Await.result(ROSNode.connectedNodeFuture, 10 seconds)

  protected[node] var subscriptions = Map[String, List[ActorRef]]()
  protected[node] var publishers = Map[String, Publisher[message.Message]]()

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.error(s"Restarting due to $reason after receiving $message")
    super.preRestart(reason, message)
  }

  override def postStop() {
    try {
      node.shutdown()
    } catch {
      case e: Exception => log.warning(s"Ros Node was not shutdown properly: $e")
    } finally {
      // reset promise so the passer can block on it when restarted
      ROSNode.connectedNodePromise = promise()
    }
    super.postStop()
  }

  def receive = {
    case Subscribe(topic, typ) => {
      if(!subscriptions.contains(topic)) {
        val subscriber = node.newSubscriber[message.Message](topic, typ)
        subscriber.addMessageListener(new MessageForwarder[message.Message](this, topic))
      }
      subscriptions = subscriptions.updated(topic, sender() :: subscriptions.getOrElse(topic, List()))
    }

    case AddPublisher(topic, typ) => {
      if(!publishers.contains(topic)) {
        val publisher = node.newPublisher[message.Message](topic, typ)
        publishers = publishers.updated(topic, publisher)
      }
    }

    case Publish(topic, msg: AbsMsg) => {
      if(!publishers.contains(topic)) {
        val publisher = node.newPublisher[message.Message](topic, msg._TYPE)
        publishers = publishers.updated(topic, publisher) //TODO: wait to make sure message is sent ...
      }
      val pub = publishers(topic)
      pub.publish(msg)
    }

    case ServiceRequest(service, msg, reqID) =>
      try {
        log.debug(s"Service request: $service $msg $reqID")
        val caller = sender()
        val typ =
          if (msg._TYPE.endsWith("Request")) msg._TYPE.substring(0, msg._TYPE.lastIndexOf("Request"))
          else sys.error(s"Could not deduce the service type from message type: ${msg._TYPE}")
        val client = node.newServiceClient[message.Message, message.Message](service, typ)

        client.call(msg, new ServiceResponseListener[message.Message] {
          def onFailure(p1: RemoteException) {
            log.error(s"Could not invoke service: $service. Remote exception: $p1")
            caller ! ServiceFailure(service, reqID)
          }

          def onSuccess(p1: message.Message) {
            val msg = MessageConverter.toRosScala(p1)
            log.debug(s"Service answer: $service - $msg")
            caller tell(ServiceResponse(service, MessageConverter.toRosScala(p1), reqID), self)
          }
        })
      } catch {
        case e: ServiceNotFoundException =>
          log.error(s"Service $service does not seem to exist: $e")
          sender() ! ServiceFailure(service, reqID)
        case e: RosRuntimeException =>
          log.error(s"Cannot invoke service $service : $e")
          e.printStackTrace()
          sender() ! ServiceFailure(service, reqID)
      }

    case x => log.error(s"Unrecognized message: $x")
  }
}
