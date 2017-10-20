package rosscala.node

import org.apache.commons.logging.LogFactory

case class Env(
                nodeName: NodeName = Unique("rosscala"),
                log: Logger = Logger.default
              )

sealed trait NodeName {
  def name: String
}
case class Unique(prefix: String) extends NodeName {
  override def name: String = prefix +"_"+math.abs(util.Random.nextInt())
}
case class Fixed(name: String) extends NodeName

trait Logger {


  protected def errorImpl(msg: => String)
  protected def warningImpl(msg: => String)
  protected def infoImpl(msg: => String)

  def warning(msg: => String)(implicit file: sourcecode.File, line: sourcecode.Line) {
    warningImpl(f"$msg%-80s  $source")
  }
  def info(msg: => String)(implicit file: sourcecode.File, line: sourcecode.Line) {
    infoImpl(f"$msg%-80s  $source")
  }

  def error(msg: => String)(implicit file: sourcecode.File, line: sourcecode.Line) {
    errorImpl(f"$msg%-80s  $source")
  }

  private def source(implicit file: sourcecode.File, line: sourcecode.Line): String =
    file.value.split("src/main/scala/rosscala/") match {
      case Array(prefix, sourceFile) => s"($sourceFile:${line.value})"
      case _ => s"(${file.value}:${line.value})"
    }
}

object Logger {

  val default: Logger = new Logger {
    val log = LogFactory.getLog("ros-scala")

    override protected def errorImpl(msg: => String): Unit = log.error(msg)

    override protected def warningImpl(msg: => String): Unit = log.warn(msg)

    override protected def infoImpl(msg: => String): Unit = log.info(msg)
  }
}