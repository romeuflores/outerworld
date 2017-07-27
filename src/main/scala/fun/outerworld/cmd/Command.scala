package fun.outerworld.cmd


import akka.actor.{ActorRef, ActorSystem, Props}
import fun.outerworld.tracking.framework.{AllFine, WhatHappened}

import scala.concurrent.{Await, Future, Promise}
import fun.outerworld.Constants._
import fun.outerworld.cmd.CommandType._
import fun.outerworld.cmd.StubbingMode.{PLAYBACK, RECORD}
import fun.outerworld.session.Session
import akka.pattern.ask
import scala.concurrent.duration._
import scala.util.{Failure, Success}
/**
  * Created by romeu on 05/07/17.
  */

sealed case class CommandType(name:String){
  override def toString: String = name
}

object CommandType{

  object BEGIN_SESSION extends CommandType("begin/session")
  object END_SESSION extends CommandType("end/session")
  object PUT_STUB extends CommandType("put/stub")
  object DELETE_STUBS extends CommandType("delete/stubs")
  object PUT_DELAY extends CommandType("put/delay")
  object GET_RESPONSE extends CommandType("get/response")
  object DO_NOTHING extends CommandType("nothing")
  object BADLY_FORMED extends CommandType("badlyformed")

  val values = List(BEGIN_SESSION,
                    END_SESSION,
                    PUT_STUB,
                    DELETE_STUBS,
                    PUT_DELAY,
                    GET_RESPONSE
  )

}

sealed case class StubbingMode(name:String){
  override def toString: String = name
}

object StubbingMode{
  object RECORD extends StubbingMode("record")
  object PLAYBACK extends StubbingMode("playback")

  val values = List(
    PLAYBACK,
    RECORD
  )

}
abstract class Command (val commandType: CommandType, val parameters: Map[String,String]=Map(), val fileNames: Seq[String]=Seq() ){

  def execute[T >: WhatHappened] (implicit system:ActorSystem) : Future[T]

}

case class DoNothingCommand (override val parameters: Map[String,String], override val fileNames: Seq[String], val whatHappened: WhatHappened)  extends Command(DO_NOTHING,parameters, fileNames){
  override def execute[T >: WhatHappened](implicit system: ActorSystem): Future[T] = {
    val p= Promise[WhatHappened]()
    p.trySuccess(AllFine())
    return p.future

  }

case class BeginSessionCommand (val sessionId:String, val scenarioId:String, val mode:StubbingMode)  extends Command(BEGIN_SESSION) {
  override def execute[T >: WhatHappened](implicit system: ActorSystem): Future[T] = {

    val session:ActorRef = mode match {
      case RECORD     ⇒ {
        system.actorOf(Props(new Session(scenarioId)), sessionId)
      }
      case PLAYBACK   ⇒ {
        Await.result(system.actorSelection(sessionId).resolveOne(), 1 second)
      }
    }
    session ? mode
  }


}

