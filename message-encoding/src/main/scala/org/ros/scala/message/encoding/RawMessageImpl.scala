package org.ros.scala.message.encoding

import java.util

import org.jboss.netty.buffer.ChannelBuffer
import org.ros.internal.message
import org.ros.internal.message.RawMessage
import org.ros.internal.message.field.Field
import org.ros.message.{Duration, MessageIdentifier, Time}

import scala.collection.JavaConverters._

class RawMessageImpl(msg: GenMsg) extends RawMessage {
  
  private val fieldMap: Map[String, Field] = msg.fields.map(f => (f.getName, f)).toMap
  private def getField(name: String) = fieldMap(name)

  def setChannelBuffer(p1: String, p2: ChannelBuffer) { throw new NotImplementedError("Setters are not functional yet.") }

  def setUInt8Array(p1: String, p2: Array[Byte]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setUInt8(p1: String, p2: Byte) { throw new NotImplementedError("Setters are not functional yet.") }

  def setUInt64Array(p1: String, p2: Array[Long]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setUInt64(p1: String, p2: Long) { throw new NotImplementedError("Setters are not functional yet.") }

  def setUInt32Array(p1: String, p2: Array[Int]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setUInt32(p1: String, p2: Int) { throw new NotImplementedError("Setters are not functional yet.") }

  def setUInt16Array(p1: String, p2: Array[Short]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setUInt16(p1: String, p2: Short) { throw new NotImplementedError("Setters are not functional yet.") }

  def setTimeList(p1: String, p2: util.List[Time]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setTime(p1: String, p2: Time) { throw new NotImplementedError("Setters are not functional yet.") }

  def setStringList(p1: String, p2: util.List[String]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setString(p1: String, p2: String) { throw new NotImplementedError("Setters are not functional yet.") }

  def setMessageList(p1: String, p2: util.List[message.Message]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setMessage(p1: String, p2: message.Message) { throw new NotImplementedError("Setters are not functional yet.") }

  def setInt8Array(p1: String, p2: Array[Byte]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setInt8(p1: String, p2: Byte) { throw new NotImplementedError("Setters are not functional yet.") }

  def setInt64Array(p1: String, p2: Array[Long]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setInt64(p1: String, p2: Long) { throw new NotImplementedError("Setters are not functional yet.") }

  def setInt32Array(p1: String, p2: Array[Int]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setInt32(p1: String, p2: Int) { throw new NotImplementedError("Setters are not functional yet.") }

  def setInt16Array(p1: String, p2: Array[Short]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setInt16(p1: String, p2: Short) { throw new NotImplementedError("Setters are not functional yet.") }

  def setFloat64Array(p1: String, p2: Array[Double]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setFloat64(p1: String, p2: Double) { throw new NotImplementedError("Setters are not functional yet.") }

  def setFloat32Array(p1: String, p2: Array[Float]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setFloat32(p1: String, p2: Float) { throw new NotImplementedError("Setters are not functional yet.") }

  def setDurationList(p1: String, p2: util.List[Duration]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setDuration(p1: String, p2: Duration) { throw new NotImplementedError("Setters are not functional yet.") }

  def setCharArray(p1: String, p2: Array[Short]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setChar(p1: String, p2: Short) { throw new NotImplementedError("Setters are not functional yet.") }

  def setByteArray(p1: String, p2: Array[Byte]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setByte(p1: String, p2: Byte) { throw new NotImplementedError("Setters are not functional yet.") }

  def setBoolArray(p1: String, p2: Array[Boolean]) { throw new NotImplementedError("Setters are not functional yet.") }

  def setBool(p1: String, p2: Boolean) { throw new NotImplementedError("Setters are not functional yet.") }

  def getBool(p1: String): Boolean = getField(p1).asInstanceOf[Boolean]

  def getBoolArray(p1: String): Array[Boolean] = getField(p1).asInstanceOf[Array[Boolean]]

  def getByte(p1: String): Byte = getField(p1).asInstanceOf[Byte]

  def getByteArray(p1: String): Array[Byte] = getField(p1).asInstanceOf[Array[Byte]]

  def getChar(p1: String): Short = getField(p1).asInstanceOf[Short]

  def getCharArray(p1: String): Array[Short] = getField(p1).asInstanceOf[Array[Short]]

  def getDefinition: String = msg._DEFINITION

  def getDuration(p1: String): Duration = getField(p1).asInstanceOf[Duration]

  def getDurationList(p1: String): util.List[Duration] = getField(p1).asInstanceOf[util.List[Duration]]

  def getFields: util.List[Field] = msg.fields.asJava
  // {
//    val fields = MsgParser.extractVariables(msg._DEFINITION)
//    (for((tipe, name) <- fields) yield {
//      if(PrimitiveFieldType.existsFor(tipe)) { // primitive type
//        val vf = ValueField.newVariable(PrimitiveFieldType.valueOf(tipe.toUpperCase), name)
//        vf.setValue(getField(name))
//        vf
//      } else {
//        println(s"message: $tipe, $name")
//        // this should be a message
//        val mft = new MessageFieldType(getIdentifier, null) // TODO: null for factory ...
//        val field = mft.newVariableValue("name")
//        field.setValue(getField(name))
//        field
//      }
//    }).asJava
//  }

  def getFloat32(p1: String): Float = getField(p1).asInstanceOf[Float]

  def getFloat32Array(p1: String): Array[Float] = getField(p1).asInstanceOf[Array[Float]]

  def getFloat64(p1: String): Double = getField(p1).asInstanceOf[Double]

  def getFloat64Array(p1: String): Array[Double] = getField(p1).asInstanceOf[Array[Double]]

  def getIdentifier: MessageIdentifier = MessageIdentifier.of(msg._TYPE)

  def getInt16(p1: String): Short = getField(p1).asInstanceOf[Short]

  def getInt16Array(p1: String): Array[Short] = getField(p1).asInstanceOf[Array[Short]]

  def getInt32(p1: String): Int = getField(p1).asInstanceOf[Int]

  def getInt32Array(p1: String): Array[Int] = getField(p1).asInstanceOf[Array[Int]]

  def getInt64(p1: String): Long = getField(p1).asInstanceOf[Long]

  def getInt64Array(p1: String): Array[Long] = getField(p1).asInstanceOf[Array[Long]]

  def getInt8(p1: String): Byte = getField(p1).asInstanceOf[Byte]

  def getInt8Array(p1: String): Array[Byte] = getField(p1).asInstanceOf[Array[Byte]]

  def getMessage[T <: message.Message](p1: String): T = getField(p1).asInstanceOf[T]

  def getMessageList[T <: message.Message](p1: String): util.List[T] = getField(p1).asInstanceOf[util.List[T]]

  def getName: String = msg._TYPE.substring(msg._TYPE.lastIndexOf("/") + 1)

  def getPackage: String = msg._TYPE.substring(0, msg._TYPE.lastIndexOf("/"))

  def getString(p1: String): String = getField(p1).asInstanceOf[String]

  def getStringList(p1: String): util.List[String] = getField(p1).asInstanceOf[util.List[String]]

  def getTime(p1: String): Time = getField(p1).asInstanceOf[Time]

  def getTimeList(p1: String): util.List[Time] = getField(p1).asInstanceOf[util.List[Time]]

  def getType: String = msg._TYPE

  def getUInt16(p1: String): Short = getField(p1).asInstanceOf[Short]

  def getUInt16Array(p1: String): Array[Short] = getField(p1).asInstanceOf[Array[Short]]

  def getUInt32(p1: String): Int = getField(p1).asInstanceOf[Int]

  def getUInt32Array(p1: String): Array[Int] = getField(p1).asInstanceOf[Array[Int]]

  def getUInt64(p1: String): Long = getField(p1).asInstanceOf[Long]

  def getUInt64Array(p1: String): Array[Long] = getField(p1).asInstanceOf[Array[Long]]

  def getUInt8(p1: String): Short = getField(p1).asInstanceOf[Short]

  def getUInt8Array(p1: String): Array[Short] = getField(p1).asInstanceOf[Array[Short]]

  def getChannelBuffer(p1: String): ChannelBuffer = getField(p1).asInstanceOf[ChannelBuffer]

  def toRawMessage: RawMessage = this
}