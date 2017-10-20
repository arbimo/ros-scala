package rosscala.node

import java.util.concurrent.TimeoutException

import monix.eval.Task
import org.ros.internal.message.Message
import org.ros.internal.node.topic.SubscriberIdentifier
import org.ros.node.ConnectedNode
import org.ros.node.topic.{DefaultPublisherListener, PublisherListener, Publisher => RJPublisher}
import rosscala.message.ROSData
import rosscala.message.encoding.Convert

import scala.concurrent.duration._
import scala.concurrent.Promise

class Publisher[A](val pub: RJPublisher[Message])(implicit encode: Convert[A, Message], rosData: ROSData[A]) {

  def publish(msg: A)(implicit msgData: ROSData[A]): Unit = {
    require(msgData._TYPE == rosData._TYPE,
      s"Message type '${msgData._TYPE}' does not match publisher's type '${rosData._TYPE}'")
    pub.publish(encode(msg))
  }

}

object Publisher {

  def log(msg: String): Unit = {
    println(s"${System.currentTimeMillis()}: $msg")
  }

  def of[A](node: ConnectedNode, topic: String)(implicit env: Env, metadata: ROSData[A], encode: Convert[A,Message]):
   Task[Publisher[A]] = {

    Task.defer {
      env.log.info(s"Starting publisher [$topic] (on ${node.getName})")
      // add new publisher
      val pub = node.newPublisher[Message](topic, metadata._TYPE)

      // add listener to be notified of success/error of registration.
      val pubPromise: Promise[RJPublisher[Message]] = Promise()
      pub.addListener(new DefaultPublisherListener[Message] {
        override def onMasterRegistrationSuccess(publisher: RJPublisher[Message]) {
          env.log.info(s"Topic registration success: $topic")
          pubPromise.success(pub)
        }
        override def onMasterRegistrationFailure(publisher: RJPublisher[Message]) {
          env.log.error(s"Topic registration failure: $topic (on ${node.getName})")
          pubPromise.failure(new RuntimeException("Failed to register to master."))
        }
      })

      Task.fromFuture(pubPromise.future)
        // even if the node is successfully registered, existing client might require
        // some time to establish the connection
        .delayResult(500.milliseconds)
        .timeout(1.second)
        .onErrorRecoverWith {
          case _:TimeoutException =>
            Task.now(pub)
        }
        .map(new Publisher(_))
    }
      .memoize // make sure the above is on evaluated once
  }
}