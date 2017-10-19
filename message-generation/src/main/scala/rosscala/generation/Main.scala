package rosscala.generation

import better.files._
import better.files.Dsl._
import scopt._
import RosUtils._
import scala.sys.process._

import scala.util.{Failure, Success, Try}

case class Config(
                 rosScalaVersion: String,
                 packages: Set[String] = Set(),
                 targetPackage: String = "rosscala.messages",
                 processDependencies: Boolean = true,
                 buildDirectory: File = file"/tmp/msgs"
                 )

object Main extends App {
  val rosScalaVersion = "0.1-SNAPSHOT"

  val parser = new scopt.OptionParser[Config]("scopt") {
    head("rosscala-msg-gen", rosScalaVersion)

    opt[Seq[String]]('p', "pkgs")
      .valueName("<package1>,<package2>...")
      .action( (x,c) =>
        c.copy(packages = x.toSet) ).text("jars to include")
  }

  parser.parse(args, Config(rosScalaVersion)) match {
    case Some(conf) =>

      val packages =
        if(conf.packages.isEmpty) {
          println("Scanning packages containing messages.")
          RosUtils.packages
        } else if(conf.processDependencies)
          conf.packages.flatMap(Package.withDeps)
        else
          conf.packages.map(Package.of(_))


      case class Acc(units: Seq[CompilationUnit] = Seq(), failed: Set[String] = Set())

      val units = order(packages).foldLeft(Acc())((acc, p) => {
        print(f"Processing package ${p.name}%-27s")
        acc match {
          case Acc(_, failed) if p.deps.exists(acc.failed.contains) =>
            println("abandoned (dependency failed previously")
            acc.copy(failed = failed + p.name)
          case Acc(prev, failed) =>
            Try {
              val msgs = messagesOf(p.name)
              CompilationUnit(p, msgs)
            } match {
              case Success(unit) =>
                println("done")
                acc.copy(units = acc.units :+ unit)
              case Failure(ex) =>
                println("failed: " + ex.getLocalizedMessage)
                acc.copy(failed = acc.failed + p.name)
            }
        }
      }).units

      val full = AllPackages(units)
      println(s"Generating source in ${conf.buildDirectory.pathAsString}")
      generateSources(full)(conf)

      println("Compiling and publishing to local ivy store.")
      compileAndPublish(conf)

    case None =>
      sys.exit(1)
  }

  def generateSources(packages: AllPackages)(implicit cfg: Config): Unit = {
    // clean up any previous content
    assert(cfg.buildDirectory.pathAsString.startsWith("/tmp"), "Build directory not in /tmp, wont erase anything there")
    cfg.buildDirectory.createDirectories()
    cfg.buildDirectory.children.foreach(_.delete())


    (cfg.buildDirectory / "build.sbt") < packages.buildConfig

    for(unit <- packages.units) {
      val dir = cfg.buildDirectory / unit.pkg.name
      dir.createDirectory()
      dir / s"${unit.pkg.name}.scala" < unit.scalaSource
    }
  }

  def compileAndPublish(implicit cfg: Config): Unit = {
    val cmd = Process("sbt publishLocal", cfg.buildDirectory.toJava)
    cmd.!(ProcessLogger.apply(fout = _=>{}, ferr=println))
  }

  def order(pkgs: Set[Package], prev: Seq[Package] = Seq()): Seq[Package] = {
    pkgs.find(p => p.deps.forall(dep => !pkgs.exists(p2 => p2.name == dep))) match {
      case _ if pkgs.isEmpty =>
        prev
      case Some(next) =>
        order(pkgs - next, prev :+ next)
      case None =>
        sys.error("Circular dependency in packages.")
    }
  }
}



case class Message(packag: String, name: String, fields: Seq[(String, String)], typ: String, description: String) {

  def scalaSource: String = {
    def fieldToArg(field: (String, String)) = {
      val fieldName = field._2
      val fieldType = rosTypeToScalaType(field._1, packag)
      s"var `$fieldName`: $fieldType = Default[$fieldType]"
    }

    s"""case class $name(
       |  ${fields.map(fieldToArg(_)).mkString(",\n  ")})
       |
       |object $name {
       |  implicit val description: ROSData[$name] = new ROSData[$name] {
       |    override val _TYPE = "$typ"
       |    override val _DEFINITION =
       |      \"\"\"${description.replace("\n", "\n        #")}\"\"\"
       |  }
       |  implicit val default: Default[$name] = new Default[$name] {
       |    override def value = $name()
       |  }
       |}
       |""".stripMargin.replace("#","|")
  }
}
object Message {
  def from(typ: String, definition: String): Message = {
    val (packag, name) = typ.split("/") match {
      case Array(x, y) => (x, y)
      case _ => sys.error(s"Could not parse message type: $typ")
    }
    val nonNestedDefinition = definition.split("\n").filterNot(_.startsWith(" ")).mkString("\n")
    val fields = MsgParser.extractVariables(nonNestedDefinition)
//      .map {case (typ, name) => (rosTypeToScalaType(typ, packag, allMessages), nameToCamelCase(name)) }
    Message(packag, name, fields, typ, nonNestedDefinition)
  }
}


case class CompilationUnit(pkg: Package, msgs: Set[Message]) {

  def scalaHeader(implicit cfg: Config): String =
  s"""
    |package ${cfg.targetPackage}.${pkg.name}
    |
    |import rosscala.message._
    |import scala.collection.mutable.ArrayBuffer
    |
    |import ${cfg.targetPackage}._
    |
    |
    |""".stripMargin

  def content(implicit cfg: Config): String = {
    val sb = new StringBuilder
    for(msg <- msgs) {
      sb ++= msg.scalaSource
      sb ++= "\n\n"
    }
    sb.toString()
  }

  def scalaSource(implicit cfg: Config): String = {
    scalaHeader + content
  }

  def sbtConfig(implicit cfg: Config): String =
  s"""
    |lazy val ${pkg.name} = project.in(file("${pkg.name}"))
    |  .settings(name := "${pkg.name}")
    |  .settings(version := "${pkg.version}-SNAPSHOT")
    |  .settings(commonSettings: _*)
    |  .dependsOn(${pkg.deps.mkString(", ")})
    |""".stripMargin
}

case class AllPackages(units: Seq[CompilationUnit]) {

  private def sbtHeader(implicit cfg: Config): String =
    s"""
      |name := "msgs-build"
      |
      |lazy val commonSettings = Seq(
      |  organization := "org.ros-scala.messages",
      |  crossPaths := true,
      |  scalaVersion := "2.12.3",
      |
      |  resolvers ++= Seq(
      |    "ROS Java" at "https://github.com/rosjava/rosjava_mvn_repo/raw/master",
      |    "JCenter" at "http://jcenter.bintray.com"),
      |  libraryDependencies += "org.ros-scala" %% "message_core" % "${cfg.rosScalaVersion}"
      |)
      |
      |lazy val root = project.in(file(".")).
      |  aggregate(${units.map(_.pkg.name).mkString(", ")}).
      |  settings(
      |    publish := {},
      |    publishLocal := {}
      |  )
    """.stripMargin

  def buildConfig(implicit cfg: Config): String = {
    val sb = new StringBuilder
    sb ++= sbtHeader
    for(unit <- units) {
      sb ++= unit.sbtConfig
    }
    sb.toString()
  }
}