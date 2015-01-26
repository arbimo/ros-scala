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


sealed trait ROSScalaMessage

case class Subscribe(topic: String) extends ROSScalaMessage

case class Publish(topic: String, data: AbsMsg) extends ROSScalaMessage

case class ServiceRequest(service: String, data: AbsMsg, requestID: Int) extends ROSScalaMessage

case class ServiceResponse(service: String, data: AbsMsg, requestID: Int) extends ROSScalaMessage

case class ROSMsg(topic: String, data: AbsMsg) extends ROSScalaMessage

