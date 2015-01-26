package org.ros.scala.node

import org.ros.message.MessageListener

class MessageForwarder[T](messagePasser: ROSMessagePasser, topic: String) extends MessageListener[T]{
  def onNewMessage(p1: T) {
    println("Forwarding: %s  from topic  %s".format(p1, topic))
    for(actor <- messagePasser.subscriptions(topic))
      println("TODO: Transfer")
//      actor ! ROSMsg(topic, p1) //TODO: make transformation to AbsMsg
  }
}
