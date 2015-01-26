package org.ros.scala.message

import org.reflections.Reflections
import scala.collection.JavaConverters._
import akka.actor.{ReceiveTimeout, Inbox, Props, ActorSystem}
import org.ros.scala.node.{ROSMsg, Subscribe, ROSMessagePasser}

object MessageConverter extends App {
   private val rm = scala.reflect.runtime.currentMirror

  private lazy val ref = new Reflections("org.ros.scala.message.generated")
  private lazy val msgImpls = ref.getSubTypesOf(classOf[AbsMsg]).asScala
  private lazy val mapping = msgImpls.map(c => getRosJavaInterface(c)-> c)

  println(mapping)
  val system = ActorSystem("FAPESystem")
  val passer = system.actorOf(Props[ROSMessagePasser], name = "passer")
  passer ! Subscribe("test")

  import akka.actor.ActorDSL._
  implicit val actorSystem: ActorSystem = system

  actor(new Act {
    passer ! Subscribe("test")
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

//  msgImpls.foreach(m => {
//    println(m)
//    println(m.getInterfaces.filter(x => x.getName != "scala.Product" && x.getName != "scala.Serializable").mkString("\n"))
//    println()
//  })

  def toRosScala(jMsg: org.ros.internal.message.Message) : AbsMsg = {
    if(jMsg.isInstanceOf[AbsMsg])
      return jMsg.asInstanceOf[AbsMsg]

    val interfaces = rm.runtimeClass(rm.classSymbol(jMsg.getClass)).getInterfaces.filter(x => x.getName != "org.ros.internal.message.GetInstance")
    assert(interfaces.size == 1)
    val i = interfaces.head
    val clazz = mapping.find( {case (interface, impl) => i.getName == interface } )
      .map{ case (interface, clazz) => clazz }
      .getOrElse(sys.error(s"No matching class for $i"))
//    println(clazz)

//    println(clazz.getConstructors.mkString("\n"))
//    val cm = rm.reflectClass(rm.classSymbol(clazz))
//    rm.classSymbol(clazz)

//    val definition = i.getField("_DEFINITION").get(null).toString
//    val fields = MsgParser.extractVariables(definition).map{case (typ, name) => name}
    val values = jMsg.toRawMessage.getFields.asScala.map(x => x.getValue[AnyRef])
    assert(clazz.getConstructors.size == 1)
    val ctor = clazz.getConstructors.head
//    val values = Seq(new java.lang.String("coucou"))

//    println("VALUES:  " + values)
//    println("CTOR  :  " + ctor)


    ctor.newInstance(values: _*).asInstanceOf[AbsMsg]
  }

}
