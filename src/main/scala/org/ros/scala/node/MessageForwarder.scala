package org.ros.scala.node

import org.ros.message.MessageListener
import org.ros.scala.message.MessageConverter

/**
 * A MessageListener that forwards all messages to scala actors that subscribed to the topic.
 * The subscribers are retrieved from the message passer.
 * @param messagePasser Message passer that keeps track of the subscribers for the topic.
 * @param topic Topic on which this listener is listening.
 * @tparam T
 */
class MessageForwarder[T <: org.ros.internal.message.Message](messagePasser: ROSMessagePasser, topic: String) extends MessageListener[T]{
  def onNewMessage(p1: T) {
    for(actor <- messagePasser.subscriptions(topic)) {
      actor ! ROSMsg(topic, MessageConverter.toRosScala(p1))
    }
  }
}
