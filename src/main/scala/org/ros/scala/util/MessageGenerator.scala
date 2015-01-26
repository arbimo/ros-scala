package org.ros.scala.util

import scala.reflect.runtime.universe._
import java.util.ServiceLoader
import scala.collection.JavaConverters._
import org.reflections.Reflections

import org.ros.internal.message.{Message => Msg}
import org.ros.scala.message.MsgParser
import org.ros.internal.message.field.PrimitiveFieldType
import move_base_msgs.MoveBaseResult
import java.io.PrintWriter

object MessageGenerator extends App {

  val filename =
    if(args.size > 0) args(0)
    else "src/main/scala/generated-messages.scala"

  val containingPackage =
    if(args.size > 1) args(1)
    else "org.ros.scala.message.generated"

  case class Message(packag: String, name: String, fields: Seq[(String, String)])

  val need = typeOf[org.ros.internal.message.Message]
  println(need)
  for(x <- ServiceLoader.load(classOf[Msg]).asScala)
    println(" a " + x)



  val ref = new Reflections()

  val allMessageInterfaces = ref.getSubTypesOf(classOf[Msg]).asScala
    .filter(_.isInterface)
    .filter(hasField(_, "_TYPE"))
    .filter(!isService(_))
//    .filter(_.getMethods.size > 1) // only one method means either Service or empty message

  val allMessages = allMessageInterfaces.map(i => i.getField("_TYPE").get(null).toString).toSet
//  println(allMessages.mkString("\n"))
  println(allMessages.toList.sortBy((x:String) => x).mkString("\n"))

  val msgs = for(x <- allMessageInterfaces) yield {
    val typ = x.getField("_TYPE").get(null).toString
    val definition = x.getField("_DEFINITION").get(null).toString
    val packag =
      if(typ.contains("/")) typ.substring(0, typ.lastIndexOf("/"))
      else ""
    println("\n" + x.getName)
//    for(m <- x.getMethods)
//      println(m)
    val vars = MsgParser.extractVariables(definition)
    println(definition)
    for((t, v) <- vars) {
      println(s"$t  ${rosTypeToScalaType(t, packag, allMessages)}")
    }

    val fields = MsgParser.extractVariables(definition)
      .map {case (typ, name) => (rosTypeToScalaType(typ, packag, allMessages), nameToCamelCase(name)) }
    Message(packag, x.getSimpleName, fields)
  }

  val msgsByPackage = msgs.groupBy(m => m.packag)

  val pw = new PrintWriter(filename)
  pw.write(s"package $containingPackage\n\n")
  pw.write("import org.ros.scala.message.AbsMsg\n\n")
  pw.write("import scala.beans.BeanProperty\n\n")

//  for((pack, _) <- msgsByPackage) {
//    pw.write(s"import _root_.{$pack => j_$pack}\n")
//  }

  for((pack, msgs) <- msgsByPackage) {
    pw.write(s"package $pack {\n")
    pw.write("  " + msgs.map(msgToScala(_)).mkString("\n\n  "))
    pw.write("}\n\n")
  }

  pw.close()

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
      s"@BeanProperty var ${field._2}: ${field._1}"

    s"case class S${msg.name}(\n      ${msg.fields.map(fieldToArg(_)).mkString(",\n      ")})\n    extends AbsMsg with _root_.${msg.packag}.${msg.name}"
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
    case "byte" => "Byte"
    case "int8" => "Byte"
    case "uint8" => "Byte"
    case "char" => "Byte"
    case "int16" => "Short"
    case "uint16" => "Short"
    case "int32" => "Int"
    case "uint32" => "Int"
    case "int64" => "Long"
    case "uint64" => "Long"
    case "float32" => "Float"
    case "float64" => "Double"
    case "time" => "org.ros.message.Time"
    case "duration" => "org.ros.message.Duration"
    case "bool" => "Boolean"
    case "string" => "String"
    case "Header" => "_root_.std_msgs.Header"
    case x if knownMessages.contains(x) => "_root_."+x.replaceAll("/", ".")
    case x if knownMessages.contains(currentPackage+"/"+x) => ("_root_."+currentPackage+"/"+x).replaceAll("/", ".")
    case unmatch => throw new RuntimeException("Error, a type was not matched: "+unmatch)
  }

  private def isService(inter: Class[_]) : Boolean = {
    require(hasField(inter, "_DEFINITION"))
    val definition = inter.getField("_DEFINITION").get(null).toString
    MsgParser.hasResponseRequestSeparator(definition)
  }
}
