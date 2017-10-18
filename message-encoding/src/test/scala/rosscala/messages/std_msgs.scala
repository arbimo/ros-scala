
package rosscala.messages.std_msgs

import rosscala.message._
import scala.collection.mutable.ArrayBuffer


case class Int64MultiArray(
                            var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                            var `data`: ArrayBuffer[scala.Long] = Default[ArrayBuffer[scala.Long]])

object Int64MultiArray {
  implicit val description: ROSData[Int64MultiArray] = new ROSData[Int64MultiArray] {
    override val _TYPE = "std_msgs/Int64MultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |int64[] data"""
  }
  implicit val default: Default[Int64MultiArray] = new Default[Int64MultiArray] {
    override def value = Int64MultiArray()
  }
}


case class ColorRGBA(
                      var `r`: scala.Float = Default[scala.Float],
                      var `g`: scala.Float = Default[scala.Float],
                      var `b`: scala.Float = Default[scala.Float],
                      var `a`: scala.Float = Default[scala.Float])

object ColorRGBA {
  implicit val description: ROSData[ColorRGBA] = new ROSData[ColorRGBA] {
    override val _TYPE = "std_msgs/ColorRGBA"
    override val _DEFINITION =
      """float32 r
        |float32 g
        |float32 b
        |float32 a"""
  }
  implicit val default: Default[ColorRGBA] = new Default[ColorRGBA] {
    override def value = ColorRGBA()
  }
}


case class Int8MultiArray(
                           var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                           var `data`: org.jboss.netty.buffer.ChannelBuffer = Default[org.jboss.netty.buffer.ChannelBuffer])

object Int8MultiArray {
  implicit val description: ROSData[Int8MultiArray] = new ROSData[Int8MultiArray] {
    override val _TYPE = "std_msgs/Int8MultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |int8[] data"""
  }
  implicit val default: Default[Int8MultiArray] = new Default[Int8MultiArray] {
    override def value = Int8MultiArray()
  }
}


case class Float64(
                    var `data`: scala.Double = Default[scala.Double])

object Float64 {
  implicit val description: ROSData[Float64] = new ROSData[Float64] {
    override val _TYPE = "std_msgs/Float64"
    override val _DEFINITION =
      """float64 data"""
  }
  implicit val default: Default[Float64] = new Default[Float64] {
    override def value = Float64()
  }
}


case class MultiArrayDimension(
                                var `label`: java.lang.String = Default[java.lang.String],
                                var `size`: scala.Int = Default[scala.Int],
                                var `stride`: scala.Int = Default[scala.Int])

object MultiArrayDimension {
  implicit val description: ROSData[MultiArrayDimension] = new ROSData[MultiArrayDimension] {
    override val _TYPE = "std_msgs/MultiArrayDimension"
    override val _DEFINITION =
      """string label
        |uint32 size
        |uint32 stride"""
  }
  implicit val default: Default[MultiArrayDimension] = new Default[MultiArrayDimension] {
    override def value = MultiArrayDimension()
  }
}


case class Byte(
                 var `data`: scala.Byte = Default[scala.Byte])

object Byte {
  implicit val description: ROSData[Byte] = new ROSData[Byte] {
    override val _TYPE = "std_msgs/Byte"
    override val _DEFINITION =
      """byte data"""
  }
  implicit val default: Default[Byte] = new Default[Byte] {
    override def value = Byte()
  }
}


case class Header(
                   var `seq`: scala.Int = Default[scala.Int],
                   var `stamp`: org.ros.message.Time = Default[org.ros.message.Time],
                   var `frame_id`: java.lang.String = Default[java.lang.String])

object Header {
  implicit val description: ROSData[Header] = new ROSData[Header] {
    override val _TYPE = "std_msgs/Header"
    override val _DEFINITION =
      """uint32 seq
        |time stamp
        |string frame_id"""
  }
  implicit val default: Default[Header] = new Default[Header] {
    override def value = Header()
  }
}


case class Int32MultiArray(
                            var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                            var `data`: ArrayBuffer[scala.Int] = Default[ArrayBuffer[scala.Int]])

object Int32MultiArray {
  implicit val description: ROSData[Int32MultiArray] = new ROSData[Int32MultiArray] {
    override val _TYPE = "std_msgs/Int32MultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |int32[] data"""
  }
  implicit val default: Default[Int32MultiArray] = new Default[Int32MultiArray] {
    override def value = Int32MultiArray()
  }
}


case class UInt16(
                   var `data`: scala.Short = Default[scala.Short])

object UInt16 {
  implicit val description: ROSData[UInt16] = new ROSData[UInt16] {
    override val _TYPE = "std_msgs/UInt16"
    override val _DEFINITION =
      """uint16 data"""
  }
  implicit val default: Default[UInt16] = new Default[UInt16] {
    override def value = UInt16()
  }
}


case class Float32MultiArray(
                              var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                              var `data`: ArrayBuffer[scala.Float] = Default[ArrayBuffer[scala.Float]])

object Float32MultiArray {
  implicit val description: ROSData[Float32MultiArray] = new ROSData[Float32MultiArray] {
    override val _TYPE = "std_msgs/Float32MultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |float32[] data"""
  }
  implicit val default: Default[Float32MultiArray] = new Default[Float32MultiArray] {
    override def value = Float32MultiArray()
  }
}


case class Int64(
                  var `data`: scala.Long = Default[scala.Long])

object Int64 {
  implicit val description: ROSData[Int64] = new ROSData[Int64] {
    override val _TYPE = "std_msgs/Int64"
    override val _DEFINITION =
      """int64 data"""
  }
  implicit val default: Default[Int64] = new Default[Int64] {
    override def value = Int64()
  }
}


case class MultiArrayLayout(
                             var `dim`: ArrayBuffer[MultiArrayDimension] = Default[ArrayBuffer[MultiArrayDimension]],
                             var `data_offset`: scala.Int = Default[scala.Int])

object MultiArrayLayout {
  implicit val description: ROSData[MultiArrayLayout] = new ROSData[MultiArrayLayout] {
    override val _TYPE = "std_msgs/MultiArrayLayout"
    override val _DEFINITION =
      """std_msgs/MultiArrayDimension[] dim
        |uint32 data_offset"""
  }
  implicit val default: Default[MultiArrayLayout] = new Default[MultiArrayLayout] {
    override def value = MultiArrayLayout()
  }
}


case class UInt32(
                   var `data`: scala.Int = Default[scala.Int])

object UInt32 {
  implicit val description: ROSData[UInt32] = new ROSData[UInt32] {
    override val _TYPE = "std_msgs/UInt32"
    override val _DEFINITION =
      """uint32 data"""
  }
  implicit val default: Default[UInt32] = new Default[UInt32] {
    override def value = UInt32()
  }
}


case class Float32(
                    var `data`: scala.Float = Default[scala.Float])

object Float32 {
  implicit val description: ROSData[Float32] = new ROSData[Float32] {
    override val _TYPE = "std_msgs/Float32"
    override val _DEFINITION =
      """float32 data"""
  }
  implicit val default: Default[Float32] = new Default[Float32] {
    override def value = Float32()
  }
}


case class UInt32MultiArray(
                             var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                             var `data`: ArrayBuffer[scala.Int] = Default[ArrayBuffer[scala.Int]])

object UInt32MultiArray {
  implicit val description: ROSData[UInt32MultiArray] = new ROSData[UInt32MultiArray] {
    override val _TYPE = "std_msgs/UInt32MultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |uint32[] data"""
  }
  implicit val default: Default[UInt32MultiArray] = new Default[UInt32MultiArray] {
    override def value = UInt32MultiArray()
  }
}


case class UInt16MultiArray(
                             var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                             var `data`: ArrayBuffer[scala.Short] = Default[ArrayBuffer[scala.Short]])

object UInt16MultiArray {
  implicit val description: ROSData[UInt16MultiArray] = new ROSData[UInt16MultiArray] {
    override val _TYPE = "std_msgs/UInt16MultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |uint16[] data"""
  }
  implicit val default: Default[UInt16MultiArray] = new Default[UInt16MultiArray] {
    override def value = UInt16MultiArray()
  }
}


case class UInt64(
                   var `data`: scala.Long = Default[scala.Long])

object UInt64 {
  implicit val description: ROSData[UInt64] = new ROSData[UInt64] {
    override val _TYPE = "std_msgs/UInt64"
    override val _DEFINITION =
      """uint64 data"""
  }
  implicit val default: Default[UInt64] = new Default[UInt64] {
    override def value = UInt64()
  }
}


case class UInt8MultiArray(
                            var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                            var `data`: org.jboss.netty.buffer.ChannelBuffer = Default[org.jboss.netty.buffer.ChannelBuffer])

object UInt8MultiArray {
  implicit val description: ROSData[UInt8MultiArray] = new ROSData[UInt8MultiArray] {
    override val _TYPE = "std_msgs/UInt8MultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |uint8[] data"""
  }
  implicit val default: Default[UInt8MultiArray] = new Default[UInt8MultiArray] {
    override def value = UInt8MultiArray()
  }
}


case class Int32(
                  var `data`: scala.Int = Default[scala.Int])

object Int32 {
  implicit val description: ROSData[Int32] = new ROSData[Int32] {
    override val _TYPE = "std_msgs/Int32"
    override val _DEFINITION =
      """int32 data"""
  }
  implicit val default: Default[Int32] = new Default[Int32] {
    override def value = Int32()
  }
}


case class String(
                   var `data`: java.lang.String = Default[java.lang.String])

object String {
  implicit val description: ROSData[String] = new ROSData[String] {
    override val _TYPE = "std_msgs/String"
    override val _DEFINITION =
      """string data"""
  }
  implicit val default: Default[String] = new Default[String] {
    override def value = String()
  }
}


case class Time(
                 var `data`: org.ros.message.Time = Default[org.ros.message.Time])

object Time {
  implicit val description: ROSData[Time] = new ROSData[Time] {
    override val _TYPE = "std_msgs/Time"
    override val _DEFINITION =
      """time data"""
  }
  implicit val default: Default[Time] = new Default[Time] {
    override def value = Time()
  }
}


case class Char(
                 var `data`: scala.Byte = Default[scala.Byte])

object Char {
  implicit val description: ROSData[Char] = new ROSData[Char] {
    override val _TYPE = "std_msgs/Char"
    override val _DEFINITION =
      """char data"""
  }
  implicit val default: Default[Char] = new Default[Char] {
    override def value = Char()
  }
}


case class Duration(
                     var `data`: org.ros.message.Duration = Default[org.ros.message.Duration])

object Duration {
  implicit val description: ROSData[Duration] = new ROSData[Duration] {
    override val _TYPE = "std_msgs/Duration"
    override val _DEFINITION =
      """duration data"""
  }
  implicit val default: Default[Duration] = new Default[Duration] {
    override def value = Duration()
  }
}


case class Bool(
                 var `data`: scala.Boolean = Default[scala.Boolean])

object Bool {
  implicit val description: ROSData[Bool] = new ROSData[Bool] {
    override val _TYPE = "std_msgs/Bool"
    override val _DEFINITION =
      """bool data"""
  }
  implicit val default: Default[Bool] = new Default[Bool] {
    override def value = Bool()
  }
}


case class Empty(
                )

object Empty {
  implicit val description: ROSData[Empty] = new ROSData[Empty] {
    override val _TYPE = "std_msgs/Empty"
    override val _DEFINITION =
      """"""
  }
  implicit val default: Default[Empty] = new Default[Empty] {
    override def value = Empty()
  }
}


case class UInt64MultiArray(
                             var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                             var `data`: ArrayBuffer[scala.Long] = Default[ArrayBuffer[scala.Long]])

object UInt64MultiArray {
  implicit val description: ROSData[UInt64MultiArray] = new ROSData[UInt64MultiArray] {
    override val _TYPE = "std_msgs/UInt64MultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |uint64[] data"""
  }
  implicit val default: Default[UInt64MultiArray] = new Default[UInt64MultiArray] {
    override def value = UInt64MultiArray()
  }
}


case class UInt8(
                  var `data`: scala.Byte = Default[scala.Byte])

object UInt8 {
  implicit val description: ROSData[UInt8] = new ROSData[UInt8] {
    override val _TYPE = "std_msgs/UInt8"
    override val _DEFINITION =
      """uint8 data"""
  }
  implicit val default: Default[UInt8] = new Default[UInt8] {
    override def value = UInt8()
  }
}


case class Int8(
                 var `data`: scala.Byte = Default[scala.Byte])

object Int8 {
  implicit val description: ROSData[Int8] = new ROSData[Int8] {
    override val _TYPE = "std_msgs/Int8"
    override val _DEFINITION =
      """int8 data"""
  }
  implicit val default: Default[Int8] = new Default[Int8] {
    override def value = Int8()
  }
}


case class ByteMultiArray(
                           var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                           var `data`: org.jboss.netty.buffer.ChannelBuffer = Default[org.jboss.netty.buffer.ChannelBuffer])

object ByteMultiArray {
  implicit val description: ROSData[ByteMultiArray] = new ROSData[ByteMultiArray] {
    override val _TYPE = "std_msgs/ByteMultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |byte[] data"""
  }
  implicit val default: Default[ByteMultiArray] = new Default[ByteMultiArray] {
    override def value = ByteMultiArray()
  }
}


case class Int16(
                  var `data`: scala.Short = Default[scala.Short])

object Int16 {
  implicit val description: ROSData[Int16] = new ROSData[Int16] {
    override val _TYPE = "std_msgs/Int16"
    override val _DEFINITION =
      """int16 data"""
  }
  implicit val default: Default[Int16] = new Default[Int16] {
    override def value = Int16()
  }
}


case class Int16MultiArray(
                            var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                            var `data`: ArrayBuffer[scala.Short] = Default[ArrayBuffer[scala.Short]])

object Int16MultiArray {
  implicit val description: ROSData[Int16MultiArray] = new ROSData[Int16MultiArray] {
    override val _TYPE = "std_msgs/Int16MultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |int16[] data"""
  }
  implicit val default: Default[Int16MultiArray] = new Default[Int16MultiArray] {
    override def value = Int16MultiArray()
  }
}


case class Float64MultiArray(
                              var `layout`: MultiArrayLayout = Default[MultiArrayLayout],
                              var `data`: ArrayBuffer[scala.Double] = Default[ArrayBuffer[scala.Double]])

object Float64MultiArray {
  implicit val description: ROSData[Float64MultiArray] = new ROSData[Float64MultiArray] {
    override val _TYPE = "std_msgs/Float64MultiArray"
    override val _DEFINITION =
      """std_msgs/MultiArrayLayout layout
        |float64[] data"""
  }
  implicit val default: Default[Float64MultiArray] = new Default[Float64MultiArray] {
    override def value = Float64MultiArray()
  }
}


