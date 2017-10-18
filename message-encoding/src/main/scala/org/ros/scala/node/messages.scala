package org.ros.scala.node

import org.ros.scala.message.AbsMsg


sealed trait ROSScalaMessage

case class AddPublisher(topic: String, typ: String) extends ROSScalaMessage

case class Subscribe(topic: String, typ: String) extends ROSScalaMessage

case class Publish(topic: String, data: AbsMsg) extends ROSScalaMessage

case class ServiceRequest(service: String, data: AbsMsg, requestID: Int) extends ROSScalaMessage

case class ServiceResponse(service: String, data: AbsMsg, requestID: Int) extends ROSScalaMessage

case class ServiceFailure(service: String, requestID: Int) extends ROSScalaMessage

case class ROSMsg(topic: String, data: AbsMsg) extends ROSScalaMessage

