package fun.outerworld.cmd


import akka.actor.{ActorRef, ActorSystem, Props}
import fun.outerworld.tracking.framework.{AllFine, UnexpectedException, WhatHappened}

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import fun.outerworld.Constants._
import fun.outerworld.cmd.CommandType._
import fun.outerworld.cmd.StubbingMode._
import fun.outerworld.session.Session
import akka.pattern.ask
import fun.outerworld.cmd
import fun.outerworld.Implicit._

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
  object NONE extends StubbingMode("none")

  val values = List(
    PLAYBACK,
    RECORD
  )

}
abstract class Command (val commandType: CommandType, val parameters: Map[String,String]=Map(), val fileNames: Seq[String]=Seq() ,val whatHappened: WhatHappened = AllFine()){

  def execute(implicit system: ActorSystem,ec: ExecutionContext): Future[Any]

}

case class DoNothingCommand (override val commandType: CommandType=DO_NOTHING, override val whatHappened: WhatHappened)  extends Command(commandType,whatHappened=whatHappened) {



  override def execute(implicit system: ActorSystem,ec: ExecutionContext): Future[Any] = {
    Future.successful(whatHappened)
  }
}

case class BeginSessionCommand (override val commandType: CommandType=BEGIN_SESSION,
                                override val parameters: Map[String,String]=Map(),
                                override val fileNames: Seq[String]=Seq() )  extends Command(commandType,parameters,fileNames) {



  def this (sessionId:String, scenarioId:String, mode:StubbingMode){
    this (BEGIN_SESSION,Map(SESSION  →  sessionId,
                            SCENARIO →  scenarioId,
                            MODE     →  mode.name))
  }

  def mode       = StubbingMode(parameters.getOrElse(MODE,"none"))
  def sessionId  = parameters.getOrElse(SESSION,"")
  def scenarioId = parameters.getOrElse(SCENARIO,"")

  override def execute(implicit system: ActorSystem,ec: ExecutionContext): Future[Any] = {


    mode match {
      case RECORD ⇒ {
        /// todo: need to catch an exception here in case session already exists.
        val session = system.actorOf(Props(new Session(scenarioId)), sessionId)
        (session ? mode)
      }
      case PLAYBACK ⇒ {
        system.actorSelection(sessionId).resolveOne().andThen {

          case Success(session: ActorRef) ⇒ session ? mode
          case Failure(e) ⇒ Future.failed(new UnexpectedException(e))
        }
      }
      case NONE ⇒ Future.failed(new UnexpectedException(new IllegalStateException("Trying to begin a session with a mode I can't recognise")))
    }
  }
}



