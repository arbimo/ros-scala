package org.ros.scala.message;

object MessageConverter extends App {

  private def rm = scala.reflect.runtime.currentMirror
  private lazy val ref = new Reflections("org.ros.scala.message.generated")
//  private lazy val msgImpls = ref.getSubTypesOf(classOf[AbsMsg]).asScala
  private lazy val mapping = msgImpls.map(c => getRosJavaInterface(c)-> c)

  println(mapping)
  val system = ActorSystem("FAPESystem")
  val passer = system.actorOf(Props[ROSMessagePasser], name = "passer")
//  passer ! Subscribe("test10")

  import akka.actor.ActorDSL._
  implicit val actorSystem: ActorSystem = system

  actor(new Act {
    passer ! Subscribe("test10", "nav_msgs/GetMapAction")
    become {
      case ReceiveTimeout => {
        //handle the timeout
        context.stop(self)
      }
      case ROSMsg(topic, msg) => {
        println(msg)
//        println(toRosScala(msg))
//        context.stop(self)
      }
    }
  })

  private def getRosJavaInterface(c: Class[_]) : String = {
    val interfaces = c.getInterfaces.filter(x => x.getName != "scala.Product" && x.getName != "scala.Serializable")
    assert(interfaces.size == 1)
    interfaces.head.getName
  }

  /**
   * Recursively transforms a ros-java message to its ros-scala counter part
   * @param jMsg A ros-java message
   * @return The equivalent ros-scala message
   */
  def toRosScala(jMsg: org.ros.internal.message.Message) : AbsMsg = {
    if(jMsg.isInstanceOf[AbsMsg])
      return jMsg.asInstanceOf[AbsMsg]

//    println(rm)

    // find the ros-java interface that this message implements (e.g. std_msgs.String)
    val interfaces = rm.runtimeClass(rm.classSymbol(jMsg.getClass)).getInterfaces.filter(x => x.getName != "org.ros.internal.message.GetInstance")
    assert(interfaces.size == 1)
    val interface = interfaces.head

    // find the corresponding ros-scala class
    val clazz = mapping.find( {case (interfaceName, impl) => interface.getName == interfaceName } )
      .map{ case (interface, clazz) => clazz }
      .getOrElse(sys.error(s"No matching class for $interface"))

    // extract all variable values of the message
    val values = jMsg.toRawMessage.getFields.asScala
      .filter(x => !x.isConstant)
      .map(x => x.getValue[AnyRef])

    // recursively convert all message values to their ros-scala counterpart
    val valuesAsRosScala = values map {
      case m: AbsMsg => m
      case m: org.ros.internal.message.Message => toRosScala(m)
      case l: java.util.List[_] =>
        val l2 = new util.LinkedList[Any]()
        for(m <- l.asScala)
          if(m.isInstanceOf[org.ros.internal.message.Message])
            l2.add(toRosScala(m.asInstanceOf[org.ros.internal.message.Message]))
          else
            l2.add(m)
        l2
      case m => m
    }

    assert(clazz.getConstructors.size == 1)
    val constructor = clazz.getConstructors.head

    constructor.newInstance(valuesAsRosScala: _*).asInstanceOf[AbsMsg]
  }

}
