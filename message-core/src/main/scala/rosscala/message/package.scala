package rosscala

import org.ros.internal.message.MessageBuffers
import org.ros.message.{Duration, Time}

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

package object message {


  trait ROSData[T] {
    def _TYPE: String
    def _DEFINITION: String
  }

  trait Default[A] {
    def value: A
  }
  object Default {

    def apply[A](implicit ev: Default[A]): A = ev.value

    private def immutableInstance[A](v: A) = new Default[A] {
      override val value: A = v
    }
    private def mutableInstance[A](v: => A) = new Default[A] {
      override def value: A = v
    }

    implicit val byte = immutableInstance[Byte](0.toByte)
    implicit val short = immutableInstance[Short](0.toShort)
    implicit val int = immutableInstance[Int](0)
    implicit val long = immutableInstance[Long](0l)
    implicit val float = immutableInstance[Float](0.0f)
    implicit val double = immutableInstance[Double](0.0)
    implicit val boolean = immutableInstance[Boolean](false)
    implicit val string = immutableInstance[String]("")
    implicit val time = mutableInstance[Time](new Time())
    implicit val duration = mutableInstance[Duration](new Duration())
    implicit val channelBuffer = mutableInstance[org.jboss.netty.buffer.ChannelBuffer](MessageBuffers.dynamicBuffer())

    implicit def array[A: scala.reflect.ClassTag] = mutableInstance[Array[A]](Array.empty[A])
    implicit def arrayBuffer[A] = mutableInstance[ArrayBuffer[A]](ArrayBuffer())
  }

}
