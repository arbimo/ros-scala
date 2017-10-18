package org.ros.scala.message

import org.ros.internal.message.Message
import org.ros.internal.message.field._
import org.ros.message.{Duration, MessageIdentifier}
import shapeless.{<:!<, LabelledGeneric}

object FieldsEncoding {

  trait TypeOfField[A] {
    type Encoding
    val get: FieldType
  }
  object TypeOfField {
    type Aux[A, Encoding0] = TypeOfField[A] { type Encoding = Encoding0 }
    def instance[A](ft: FieldType): TypeOfField.Aux[A, A] = new TypeOfField[A] {
      override type Encoding = A
      val get: FieldType = ft
    }

    implicit val boolean: TypeOfField[Boolean] = instance(
      PrimitiveFieldType.BOOL)
    implicit val byte: TypeOfField[Byte] = instance(PrimitiveFieldType.INT8)
    implicit val short: TypeOfField[Short] = instance(PrimitiveFieldType.INT16)
    implicit val int: TypeOfField[Int] = instance(PrimitiveFieldType.INT32)
    implicit val long: TypeOfField[Long] = instance(PrimitiveFieldType.INT64)
    implicit val float: TypeOfField[Float] = instance(
      PrimitiveFieldType.FLOAT32)
    implicit val double: TypeOfField[Double] = instance(
      PrimitiveFieldType.FLOAT64)
    implicit val time: TypeOfField[org.ros.message.Time] = instance(
      PrimitiveFieldType.TIME)
    implicit val duration: TypeOfField[org.ros.message.Duration] = instance(
      PrimitiveFieldType.DURATION)
    implicit val string: TypeOfField[String] = instance(
      PrimitiveFieldType.STRING)

    implicit def message[A](
        implicit rosData: ROSData[A]): TypeOfField.Aux[A, Message] =
      new TypeOfField[A] {
        type Encoding = Message
        override val get = new MessageFieldType(
          MessageIdentifier.of(rosData._TYPE),
          null) // null for factory that should never be used
      }
  }

  trait FieldFactory[A] {
    def apply(k: String): Field
    def apply(k: String, v: A): Field = {
      val f = apply(k)
      f.setValue(v)
      f
    }
  }
  object FieldFactory {
    implicit def direct[A](
        implicit ft: TypeOfField.Aux[A, A]): FieldFactory[A] =
      ft.get.newVariableValue(_)

    implicit def message[A](implicit asMessage: AsMessage[A],
                            ft: TypeOfField[A]): FieldFactory[A] =
      new FieldFactory[A] {
        override def apply(k: String): Field = {
          ft.get.newVariableValue(k)
        }

        override def apply(k: String, v: A): Field = {
          val f = apply(k)
          f.setValue(asMessage(v))
          f
        }
      }

  }

  trait Default[A] {
    def value: A
  }
  object Default {

    def apply[A](implicit ev: Default[A]): A = ev.value

    implicit def fromFieldFactory[A](
                                      implicit fieldFactory: FieldFactory[A],
                                      ev: A <:!< Product): Default[A] = new Default[A] {
      def value: A = fieldFactory.apply("").getValue[A]
    }
  }

  trait AsField[A] {
    def apply(v: A): Field
  }
  object AsField {
    import shapeless.labelled.FieldType
    import shapeless.Witness

    implicit def fieldType[K, V](
        implicit witness: Witness.Aux[K],
        fieldFactory: FieldFactory[V]): AsField[FieldType[K, V]] =
      v => fieldFactory(witness.value.toString, v)
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

  trait AsMessage[A] {
    def apply(v: A): org.ros.internal.message.Message
  }
  object AsMessage {
    implicit def gen[A](implicit asFields: AsFields[A],
                        rOSData: ROSData[A]): AsMessage[A] = v => {
      val genMsg = new GenMsg {
        override def fields: Seq[Field] = asFields(v)
        override val _DEFINITION: String = rOSData._DEFINITION
        override val _TYPE: String = rOSData._TYPE
      }
      new RawMessageImpl(genMsg)
    }
  }

}
