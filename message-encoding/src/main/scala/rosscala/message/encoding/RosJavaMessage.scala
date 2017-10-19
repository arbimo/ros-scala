package rosscala.message.encoding

import org.ros.internal.message.field.Field


trait GenMsg {

  val _TYPE: String
  val _DEFINITION: String

  def fields: Seq[Field]
}

class RJMessage(msg: GenMsg) extends org.ros.internal.message.Message {
  override def toRawMessage = new RawMessageImpl(msg)
}
