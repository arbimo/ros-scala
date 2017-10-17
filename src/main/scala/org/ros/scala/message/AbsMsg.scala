package org.ros.scala.message

import org.ros.internal.message.RawMessage
import org.ros.internal.message.field.Field
import org.ros.scala.message.Msg2.JMsg

import scala.reflect.runtime.universe._
import org.ros.scala.util.MessageGenerator
import shapeless.{HList, LabelledGeneric}
import shapeless.ops.record.ToMap

abstract class AbsMsg extends org.ros.internal.message.Message with GenMsg {
  def toRawMessage : RawMessage = new RawMessageImpl(this)
}

trait GenMsg {

  val _TYPE: String
  val _DEFINITION: String

  def fields: Seq[Field]
}

class RJMessage(msg: GenMsg) extends org.ros.internal.message.Message {
  override def toRawMessage = new RawMessageImpl(msg)
}

trait Converter[ScalaMsg, JavaCounterPart <: JMsg]

class ConverterImpl[ScalaMsg, Gen <: HList, JavaCounterPart <: JMsg](
          implicit gen: LabelledGeneric.Aux[ScalaMsg,Gen],
          tmr: ToMap[Gen],
          clazz: Class[JavaCounterPart]) {

  def fields(msg: ScalaMsg): Map[String, Any] = {
      val m: Map[tmr.Key, tmr.Value] = tmr(gen.to(msg))
      m.map { case (k, v) => k.toString -> v }
    }

  def fromFields(fields: Map[String,Any]): Option[ScalaMsg] = {
    None
  }


  // clazz.getDeclaredField("_TYPE") //TODO: check if this work directly
  val _TYPE : String = clazz.getInterfaces.head.getField("_TYPE").get(null).toString
  val _DEFINITION : String = clazz.getInterfaces.head.getField("_DEFINITION").get(null).toString


}

object Msg2 {

  type JMsg = org.ros.internal.message.Message {
    val _TYPE: String
    val _DEFINITION: String
  }

}

object Mappable {
  implicit class ToMapOps[A](val a: A) extends AnyVal {
    import shapeless._
    import ops.record._

    def toMap[L <: HList](implicit
      gen: LabelledGeneric.Aux[A, L],
      tmr: ToMap[L]
    ): Map[String, Any] = {
      val m: Map[tmr.Key, tmr.Value] = tmr(gen.to(a))
      m.map { case (k, v) => k.toString -> v }
    }
  }
}