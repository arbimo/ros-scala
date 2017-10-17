package org.ros.scala

import org.ros.internal.message.{Message => JMsg}
import shapeless.ops.record.ToMap
import shapeless.{HList, LabelledGeneric}

package object message {

  trait ROSData[T] {
    def _TYPE: String
    def _DEFINITION: String
  }

  trait FullMsgType[Name <: String, Description <: String] {

    def _TYPE: Name
    def _DESCRIPTION: Description
  }
  trait MsgType extends FullMsgType[String, String]
  object MsgType {}

  trait Serializer[T] {
    def encode(t: T): JMsg
  }
  object Serializer {

    implicit def xx[ScalaMsg, Gen <: HList](
        implicit gen: LabelledGeneric.Aux[ScalaMsg, Gen],
        tmr: ToMap[Gen],
        ros: ROSData[ScalaMsg]) {

      def fields(msg: ScalaMsg): Map[String, Any] = {
        val m: Map[tmr.Key, tmr.Value] = tmr(gen.to(msg))
        m.map { case (k, v) => k.toString -> v }
      }

      new Serializer[ScalaMsg] {
        override def encode(t: ScalaMsg): JMsg = new AbsMsg {
          private val actualFields = fields(t)
          override def getField(key: String): Any = actualFields(key)

          override val _DEFINITION: String = ros._DEFINITION
          override val _TYPE: String = ros._TYPE
        }
      }
    }

  }
  trait Deserializer[T] {
    def extract(msg: JMsg): T
  }

}
