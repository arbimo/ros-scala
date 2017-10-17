package org.ros.scala.message

import org.ros.scala.message.FieldsEncoding.AsFields
import utest._

object FieldEncodingTests extends TestSuite {

  case class Point(x: Int, y: Int)

  val tests = Tests {
    "point" - {
      println(AsFields[Point].apply(Point(1, 2)))
    }
  }
}
