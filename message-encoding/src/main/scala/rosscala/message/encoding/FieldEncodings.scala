package rosscala.message.encoding

import org.ros.internal.message.Message
import org.ros.internal.message.field._
import org.ros.message.MessageIdentifier
import rosscala.message.ROSData

trait Encoding[A] {
  type EncodedAs
  val fieldType: FieldType
}
object Encoding {
  type Aux[A, Encoding0] = Encoding[A] { type EncodedAs = Encoding0 }
  def instance[A](ft: FieldType): Encoding.Aux[A, A] = new Encoding[A] {
    override type EncodedAs = A
    val fieldType: FieldType = ft
  }

  implicit val boolean: Aux[Boolean, Boolean] = instance(
    PrimitiveFieldType.BOOL)
  implicit val byte: Aux[Byte, Byte] = instance(PrimitiveFieldType.INT8)
  implicit val short: Aux[Short, Short] = instance(PrimitiveFieldType.INT16)
  implicit val int: Aux[Int, Int] = instance(PrimitiveFieldType.INT32)
  implicit val long: Aux[Long, Long] = instance(PrimitiveFieldType.INT64)
  implicit val float: Aux[Float, Float] = instance(PrimitiveFieldType.FLOAT32)
  implicit val double: Aux[Double, Double] = instance(
    PrimitiveFieldType.FLOAT64)
  implicit val time: Aux[org.ros.message.Time, org.ros.message.Time] = instance(
    PrimitiveFieldType.TIME)
  implicit val duration
    : Aux[org.ros.message.Duration, org.ros.message.Duration] = instance(
    PrimitiveFieldType.DURATION)
  implicit val string: Aux[String, String] = instance(PrimitiveFieldType.STRING)

  implicit def message[A](
      implicit rosData: ROSData[A]): Encoding.Aux[A, Message] =
    new Encoding[A] {
      override type EncodedAs = Message
      override val fieldType = new MessageFieldType(
        MessageIdentifier.of(rosData._TYPE),
        null) // null for factory that should never be used
    }
}

trait AsField[A] {
  def apply(v: A): Field
}
object AsField {
  import shapeless.labelled.FieldType
  import shapeless.Witness

  def apply[A](implicit ev: AsField[A]): AsField[A] = ev

  implicit def fieldWithConversion[K, From, To](
      implicit witness: Witness.Aux[K],
      fieldType: Encoding.Aux[From, To],
      convert: Convert[From, To]): AsField[FieldType[K, From]] =
    v => {
      val field = fieldType.fieldType.newVariableValue(witness.value.toString)
      field.setValue(convert(v))
      field
    }

  def of[A](v: A)(implicit ev: AsField[A]): Field = ev(v)
}

trait AsFields[A] {
  def apply(v: A): List[Field]
}
object AsFields {
  import shapeless._

  def apply[A](implicit ev: AsFields[A]): AsFields[A] = ev

  implicit def hnil[A <: HNil]: AsFields[A] = _ => Nil
  implicit def hlist[H, T <: HList](implicit asField: AsField[H],
                                    asFields: AsFields[T]): AsFields[H :: T] =
    (l: H :: T) => asField(l.head) :: asFields(l.tail)

  implicit def generic[A, B](implicit gen: LabelledGeneric.Aux[A, B],
                             asFields: AsFields[B]): AsFields[A] =
    (v: A) => asFields(gen.to(v))
}

trait Convert[From, To] {
  def apply(v: From): To
}
object Convert {
  import org.ros.internal.message.Message

  def apply[A, B](implicit cv: Convert[A, B]): Convert[A, B] = cv

  implicit def id[A]: Convert[A, A] = v => v

  implicit def gen[A](implicit asFields: AsFields[A],
                      rOSData: ROSData[A]): Convert[A, Message] = v => {
    val genMsg = new GenMsg {
      override def fields: Seq[Field] = asFields(v)
      override val _DEFINITION: String = rOSData._DEFINITION
      override val _TYPE: String = rOSData._TYPE
    }
    new RawMessageImpl(genMsg)
  }
}
