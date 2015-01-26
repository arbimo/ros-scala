package org.ros.scala.node

import org.ros.message.MessageListener
import org.ros.scala.message.MessageConverter

class MessageForwarder[T <: org.ros.internal.message.Message](messagePasser: ROSMessagePasser, topic: String) extends MessageListener[T]{
  def onNewMessage(p1: T) {
//    println("Forwarding: %s  from topic  %s".format(p1, topic))
    for(actor <- messagePasser.subscriptions(topic)) {
//      println("TODO: Transfer")
      actor ! ROSMsg(topic, MessageConverter.toRosScala(p1))
    }
//      actor ! ROSMsg(topic, p1) //TODO: make transformation to AbsMsg
  }
}
