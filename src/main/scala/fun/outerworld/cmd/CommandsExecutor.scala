package fun.outerworld.cmd

import scala.util.{Failure, Success}
import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import scala.concurrent.Future
import akka.stream.ActorMaterializer
import akka.util.ByteString
import fun.outerworld.cmd.parser.CommandsParser
import fun.outerworld.message.framework.WhatHappened

import scala.collection.mutable.ListBuffer
import scala.io.Source


/**
  * This actor class will receive a string which points to a commands file in an http location.
  *
  */

class CommandsExecutor () extends Actor with ActorLogging {

  import context.dispatcher

  private implicit val system = context.system
  private implicit val materializer =  ActorMaterializer()

  /**
    * This should:
    * 1) parse the commands received
    * 2) Try and execute each command
    * @param commands
    * @return a future with details of the execution of each command

  protected def executeCommands (commands:String): Future[WhatHappened] ={
      //val commands: List[Command]  = parse(commands)

  }*/



  def receive: Receive = {
    case (cmdFile:String) if cmdFile.toLowerCase.startsWith("http") ⇒
      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = cmdFile))
      val mySender = sender
      responseFuture onComplete {
        case Success(httpResponse) ⇒
          httpResponse.entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach {
            body ⇒ MyParser(body.utf8String)
          }
        case Failure(f) ⇒ mySender ! f
        case _ ⇒ log.error("Unexpected error @ CommandsExecutor")
      }

    case resp @ HttpResponse(code, _, _, _) ⇒
      log.info("Request failed, response code: " + code)
      resp.discardEntityBytes()
    case _ ⇒
      log.error("Unexpected condition reached @ CommandsExecutor. Make sure you use only http(s) urls")

  }



}

object MyParser extends CommandsParser {
  def apply(body: String)= compile(body)

  def compile(body: String): (List[Command], List[Any]) = {
    val commands: ListBuffer[Command] = ListBuffer()
    val failures: ListBuffer[Any] = ListBuffer()
    val lines = body.split("\n")
    for (line ← lines){
      val result = parse (commandLine, line)
      result match {
        case Success(result,_) ⇒ commands.append(result)
        case NoSuccess (failure) ⇒  failures.append(failure)
      }

    }
    return (commands.toList,failures.toList)
  }
}

