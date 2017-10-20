package rosscala.repl

import rosscala.generation.RosUtils


object Main {
  def main(args: Array[String]): Unit = {
    // Break into debug REPL with

    val messagePkgs = Seq("std_msgs")
    val msgsImports = messagePkgs
      .map(name => s"import $$ivy.`org.ros-scala.messages::$name:${RosUtils.packageVersion(name)}-SNAPSHOT`")
      .mkString("\n")

    val predefCode =
      """
        |interp.repositories() ++= Seq(coursier.ivy.IvyRepository.fromPattern(
        |  "https://github.com/rosjava/rosjava_mvn_repo/raw/master/" +:
        |   coursier.ivy.Pattern.default
        |))
        |interp.repositories() ++= Seq(coursier.ivy.IvyRepository.fromPattern(
        |  "http://jcenter.bintray.com/" +:
        |   coursier.ivy.Pattern.default
        | ))
        |import rosscala._
        |import rosscala.defaults._
        |import rosscala.scripting._
        |
        """.stripMargin// +
        msgsImports
        """
          |import rosscala.messages._
        """.stripMargin
    println(predefCode)
    ammonite.Main(
      predefCode = predefCode
    ).run(
      "hello" -> hello,
      "fooValue" -> foo()
    )
  }
  def foo() = 1
}