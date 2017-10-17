package org.ros.scala.util

import scala.reflect.runtime.universe._
import java.util.ServiceLoader
import scala.collection.JavaConverters._
import org.reflections.Reflections

import org.ros.internal.message.{Message => Msg}
import org.ros.scala.message.MsgParser
import org.ros.internal.message.field.PrimitiveFieldType
import java.io.PrintWriter

object MessageGenerator extends App {

  val filename =
    if(args.size > 0) args(0)
    else "src/main/scala/generated-messages.scala"

  val containingPackage =
    if(args.size > 1) args(1)
    else "org.ros.scala.message.generated"

  case class Message(packag: String, name: String, fields: Seq[(String, String)], typ: String, description: String)

//  val need = typeOf[org.ros.internal.message.Message]
//  println(need)
//  for(x <- ServiceLoader.load(classOf[Msg]).asScala)
//    println(" a " + x)



  val ref = new Reflections()

  val allMessageInterfaces = ref.getSubTypesOf(classOf[Msg]).asScala
    .filter(_.isInterface)
    .filter(hasField(_, "_TYPE"))
    .filter(!isService(_))
//    .filter(_.getMethods.size > 1) // only one method means either Service or empty message

  val allMessages = allMessageInterfaces.map(i => i.getField("_TYPE").get(null).toString).toSet
//  println(allMessages.mkString("\n"))

  println("The following messages will be generated: ")
  println("    " + allMessages.toList.sortBy((x:String) => x).mkString("\n    "))

  val msgs = for(x <- allMessageInterfaces) yield {
    val typ = x.getField("_TYPE").get(null).toString
    val definition = x.getField("_DEFINITION").get(null).toString
    val packag =
      if(typ.contains("/")) typ.substring(0, typ.lastIndexOf("/"))
      else ""

//    println("\n" + x.getName)
//    for(m <- x.getMethods)
//      println(m)
//    val vars = MsgParser.extractVariables(definition)
//    println(definition)
//    for((t, v) <- vars) {
//      println(s"$t  ${rosTypeToScalaType(t, packag, allMessages)}")
//    }

    val fields = MsgParser.extractVariables(definition)
      .map {case (typ, name) => (rosTypeToScalaType(typ, packag, allMessages), nameToCamelCase(name)) }
    Message(packag, x.getSimpleName, fields, typ, definition)
  }

  val msgsByPackage = msgs.groupBy(m => m.packag)

  val pw = new PrintWriter(filename)
  pw.write(s"package $containingPackage\n\n")
  pw.write("import org.ros.scala.message._\n")
  pw.write("import org.ros.scala.message.FieldsEncoding.Default\n\n")

//  for((pack, _) <- msgsByPackage) {
//    pw.write(s"import _root_.{$pack => j_$pack}\n")
//  }

  for((pack, msgs) <- msgsByPackage if pack.contains("std") || pack.contains("geom")) {
    pw.write(s"package $pack {\n")
    pw.write("  " + msgs.map(msgToScala(_)).mkString("\n\n  "))
    pw.write("}\n\n")
  }

  pw.close()

  println("Messages were written to "+filename)

  def nameToCamelCase(name: String) : String = {
    var nextToUpper = false
    var out = ""
    for(c <- name.toCharArray) {
      if(c == '_') {
        nextToUpper = true
      } else if(nextToUpper) {
        out += c.toUpper
        nextToUpper = false
      } else {
        out += c
        nextToUpper = false
      }
    }
    out
  }

  private def msgToScala(msg: Message) : String = {
    def fieldToArg(field: (String,String)) =
      s"var `${field._2}`: ${field._1}" + (if(field._1.startsWith("scala")) s" = Default[${field._1}]" else "")

    s"""case class ${msg.name}(
       |    ${msg.fields.map(fieldToArg(_)).mkString(",\n    ")})
       |
       |  object ${msg.name} {
       |    implicit val metadata = new ROSData[${msg.name}] {
       |      override val _TYPE = "${msg.typ}"
       |      override val _DEFINITION = \"\"\"${msg.description}\"\"\"
       |    }
       |  }
       |  """.stripMargin
  }
  private def hasField(msg: Class[_], name: String) : Boolean =
    try {
      msg.getField(name)
      true
    } catch {
      case e: NoSuchFieldException => false
    }

  private def isPrimitiveType(t: String) = PrimitiveFieldType.existsFor(t)

  private def getPrimitiveJavaType(t: String) = PrimitiveFieldType.valueOf(t.toUpperCase).getJavaTypeName

  private def rosTypeToScalaType(rosType: String, currentPackage: String, knownMessages: Set[String]) : String = rosType match {
    case "int8[]" | "byte[]" | "uint8[]" => "org.jboss.netty.buffer.ChannelBuffer"
    case x if x.endsWith("]") =>
      val baseType =
        if(x.endsWith("[]")) x.replace("[]", "")
        else x.substring(0, x.lastIndexOf("["))
      if(!isPrimitiveType(baseType) || baseType == "string" || baseType == "time" || baseType == "duration")
        "java.util.List[%s]".format(rosTypeToScalaType(baseType, currentPackage, knownMessages))
      else
        "Array[%s]".format(rosTypeToScalaType(baseType, currentPackage, knownMessages))
    case "byte" => "scala.Byte"
    case "int8" => "scala.Byte"
    case "uint8" => "scala.Byte"
    case "char" => "scala.Byte"
    case "int16" => "scala.Short"
    case "uint16" => "scala.Short"
    case "int32" => "scala.Int"
    case "uint32" => "scala.Int"
    case "int64" => "scala.Long"
    case "uint64" => "scala.Long"
    case "float32" => "scala.Float"
    case "float64" => "scala.Double"
    case "time" => "org.ros.message.Time"
    case "duration" => "org.ros.message.Duration"
    case "bool" => "scala.Boolean"
    case "string" => "java.lang.String"
    case "Header" => "std_msgs.Header"
    case x if knownMessages.contains(x) => x.replaceAll("/", ".")
    case x if knownMessages.contains(currentPackage+"/"+x) => (currentPackage+"/"+x).replaceAll("/", ".")
    case unmatch => throw new RuntimeException("Error, a type was not matched: "+unmatch)
  }

  private def isService(inter: Class[_]) : Boolean = {
    require(hasField(inter, "_DEFINITION"))
    val definition = inter.getField("_DEFINITION").get(null).toString
    MsgParser.hasResponseRequestSeparator(definition)
  }
}
