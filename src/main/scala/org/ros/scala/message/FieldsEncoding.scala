package org.ros.scala.message

import org.ros.internal.message.field.{Field, PrimitiveFieldType}
import org.ros.message.Duration

object FieldsEncoding {

  trait FieldFactory[A] {
    def apply(k: String): Field
    def apply(k: String, v: A): Field = {
      val f = apply(k)
      f.setValue(v)
      f
    }
  }
  object FieldFactory {
    implicit val boolean: FieldFactory[Boolean] =
      PrimitiveFieldType.BOOL.newVariableValue(_)
    implicit val byte: FieldFactory[Byte] =
      PrimitiveFieldType.INT8.newVariableValue(_)
    implicit val short: FieldFactory[Short] =
      PrimitiveFieldType.INT16.newVariableValue(_)
    implicit val int: FieldFactory[Int] =
      PrimitiveFieldType.INT32.newVariableValue(_)
    implicit val long: FieldFactory[Long] =
      PrimitiveFieldType.INT64.newVariableValue(_)
    implicit val float: FieldFactory[Float] =
      PrimitiveFieldType.FLOAT32.newVariableValue(_)
    implicit val double: FieldFactory[Double] =
      PrimitiveFieldType.FLOAT64.newVariableValue(_)
    implicit val time: FieldFactory[org.ros.message.Time] =
      PrimitiveFieldType.TIME.newVariableValue(_)
    implicit val duration: FieldFactory[org.ros.message.Duration] =
      PrimitiveFieldType.DURATION.newVariableValue(_)
  }

  trait Default[A] {
    def value: A
  }
  object Default {
    def apply[A](implicit ev: Default[A]): A = ev.value

    implicit def fromFieldFactory[A](
        implicit fieldFactory: FieldFactory[A]): Default[A] = new Default[A] {
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

}
