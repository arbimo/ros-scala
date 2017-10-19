package rosscala.generation

import scala.sys.process._

object RosUtils {

  def packagesWithMessages(): Set[String] = {
    "rosmsg packages".!!
      .split("\\s+")
      .filter(_.nonEmpty)
      .toSet
  }

  def packageDependencies(pkg: String, retain: String => Boolean): Set[String] = {
    s"rospack depends1 $pkg".!!
      .split("\\s+")
      .filter(_.nonEmpty)
      .filter(retain)
      .toSet
  }

  def packageVersion(pkg: String): String =
    s"rosversion $pkg".!!
      .replaceAll("\\s", "")

  def packages: Set[Package] = {
    val pkgNames = packagesWithMessages()
    val retainDep = (pkg: String) => pkgNames.contains(pkg)
    pkgNames
      .map(p => Package.of(p))
  }

  def definitionOf(msg: String): String =
    s"rosmsg show $msg".!!

  def messagesOf(pkg: String): Set[Message] = {
    s"rosmsg package $pkg".!!
      .split("\\s+")
      .filter(_.nonEmpty)
      .map(msg => Message.from(msg, definitionOf(msg)))
      .toSet
  }

  case class Package(name: String, version: String, deps: Set[String])
  object Package {
    def of(name: String) : Package = {
      val pkgNames = packagesWithMessages()
      val retainDep = (pkg: String) => pkgNames.contains(pkg)
      Package(name, packageVersion(name), packageDependencies(name, retainDep))
    }

    def withDeps(name: String): Set[Package] = {
      val main = Package.of(name)
      main.deps.flatMap(Package.withDeps) + main
    }
  }

  def rosTypeToScalaType(rosType: String, localPackage: String) : String = rosType match {
        case "int8[]" | "byte[]" | "uint8[]" => "org.jboss.netty.buffer.ChannelBuffer"
        case x if x.endsWith("]") =>
          val baseType =
            if(x.endsWith("[]")) x.replace("[]", "")
            else x.substring(0, x.lastIndexOf("["))
          val scalaBaseType = rosTypeToScalaType(baseType, localPackage)
          if(hasArrayRepr(rosType))
            s"Array[$scalaBaseType]"
          else
            s"ArrayBuffer[$scalaBaseType]"
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
        case x if x.startsWith(localPackage+"/") => x.replace(localPackage+"/", "")
        case x => x.replaceAll("/", ".") // message
      }

  private def hasArrayRepr(rosType: String): Boolean = rosType match {
    case "int8" | "uint8" | "char" | "int16" | "uint16" | "int32" | "uint32" | "int64" |
         "uint64" | "float32" | "float64" | "bool" | "byte" =>
      true
    case _ => false
  }
}
