package org.ros.scala.message

import org.ros.internal.message.RawMessage
import scala.reflect.runtime.universe._
import org.ros.scala.util.MessageGenerator

abstract class AbsMsg extends org.ros.internal.message.Message {
  private def rm = scala.reflect.runtime.currentMirror

//  println( rm.classSymbol(this.getClass))
//  println(rm.classSymbol(this.getClass).toType.members)

  private def accessors = rm.classSymbol(this.getClass).toType.members.collect {
    case m: MethodSymbol if m.isGetter && m.isPublic => m
  }
  private val instanceMirror = rm.reflect(this)

  val _TYPE : String = rm.runtimeClass(rm.classSymbol(this.getClass)).getInterfaces.head.getField("_TYPE").get(null).toString
  val _DEFINITION : String = rm.runtimeClass(rm.classSymbol(this.getClass)).getInterfaces.head.getField("_DEFINITION").get(null).toString

  def toRawMessage : RawMessage = new RawMessageImpl(this)

  def getField(name: String) : Any = {
    accessors.find(m => m.name.toString == name || m.name.toString == MessageGenerator.nameToCamelCase(name)) match {
      case Some(method) => instanceMirror.reflectMethod(method).apply()
      case None => throw new RuntimeException("Unknown field: "+name)
    }
  }
}
