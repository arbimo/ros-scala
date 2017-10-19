package rosscala.node

import org.ros.internal.message.Message
import org.ros.node.topic.{Publisher => RJPublisher}
import rosscala.message.encoding.Convert

class Publisher[A](val pub: RJPublisher[Message])(implicit encode: Convert[A, Message]) {

  def publish(msg: A): Unit = {
    pub.publish(encode(msg))
  }

}
