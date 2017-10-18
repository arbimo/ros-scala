package rosscala.generation

import scala.util.parsing.combinator.JavaTokenParsers

object MsgParser extends JavaTokenParsers {
  sealed trait MsgLine
  case class Variable(tipe: String, id: String) extends MsgLine
  case class Constant(tipe: String, id: String, value: String) extends MsgLine
  object EmptyLine extends MsgLine
  object ResponseRequestSeparator extends MsgLine

  override protected val whiteSpace = """(\s|#.*)+""".r

  def baseType: Parser[String] = repsep(ident, "/") ^^ { case x => x.mkString("/")}
  def tipe: Parser[String] =
    baseType~"[]" ^^ { case id~array => id+"[]"} |
      baseType~"["~wholeNumber~"]" ^^ {  case id~"["~size~"]" => "%s[%s]".format(id, size) } |
      baseType
  def id: Parser[String] = ident
  def variable: Parser[Variable] = tipe~id ^^ { case t~id => Variable(t, id) }
  def constant: Parser[Constant] = tipe~id~"="~value ^^ { case tipe~id~"="~value => Constant(tipe, id, value)}
  def value: Parser[String] = ident | wholeNumber
  def emptyLine: Parser[MsgLine] = "" ^^^ EmptyLine
  def separator: Parser[MsgLine] = "-"~rep("-") ^^^ ResponseRequestSeparator
  def singleLine: Parser[MsgLine] = constant | variable | separator | emptyLine
  def message: Parser[List[MsgLine]] = rep(variable | constant | emptyLine)

  def extractVariables(definition: String) : List[(String,String)] = {
    require(!hasResponseRequestSeparator(definition))
    definition.split("\n")
      .filter(line => !line.startsWith("#"))
      .filter(line => !(line == ""))
      .map(line => parseAll(singleLine, line).getOrElse(throw new RuntimeException("Unable to parse line \"%s\" in definition:\n%s".format(line, definition))))
      .filter(_.isInstanceOf[Variable])
      .map(_.asInstanceOf[Variable])
      .map(variable => (variable.tipe, variable.id))
//      .map(line => parseAll(variable, line).getOrElse(throw new RuntimeException("Unable to parse line \"%s\" in definition:\n%s".format(line, definition))))
      .toList
  }

  def hasResponseRequestSeparator(definition: String) : Boolean =
    definition.split("\n")
      .map(line => parseAll(singleLine, line).getOrElse(throw new RuntimeException("Unable to parse line \"%s\" in definition:\n%s".format(line, definition))))
      .contains(ResponseRequestSeparator)
}
