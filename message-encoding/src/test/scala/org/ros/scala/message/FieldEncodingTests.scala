package org.ros.scala.message

import org.ros.scala.message.encoding.{AsField, AsFields, Convert}
import rosscala.message.{Default, ROSData}
import shapeless.labelled.FieldType
import utest._
import shapeless.{::, HNil}

object FieldEncodingTests extends TestSuite {

  case class Point(x: Int, y: Int)

  case class Quaternion(var `x`: scala.Double = Default[scala.Double],
                        var `y`: scala.Double = Default[scala.Double],
                        var `z`: scala.Double = Default[scala.Double],
                        var `w`: scala.Double = Default[scala.Double])

  object Quaternion {
    implicit val metadata = new ROSData[Quaternion] {
      override val _TYPE = "geometry_msgs/Quaternion"
      override val _DEFINITION =
        """# This represents an orientation in free space in quaternion form.

float64 x
float64 y
float64 z
float64 w
"""
    }
  }

    case class Header(
    var `seq`: scala.Int = Default[scala.Int],
    var `stamp`: org.ros.message.Time = Default[org.ros.message.Time],
    var `frameId`: java.lang.String = Default[java.lang.String])

  val tests = Tests {
    "point" - {
      import shapeless.labelled._
      import shapeless.record._
      import shapeless._
      import shapeless.syntax.singleton._
      import shapeless.labelled._
      Convert[String,String]
      AsField.of("1" ->> "a")

//      println(AsFields[Point].apply(Point(1, 2)))

//      println(Default[Int :: HNil])
//      println(Default[Double :: FieldType[String,Int] :: HNil])
//      println(Default[Quaternion])
//      println(Default[Header])
    }
  }
}


