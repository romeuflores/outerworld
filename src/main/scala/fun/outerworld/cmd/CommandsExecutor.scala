package fun.outerworld.cmd

import scala.util.{Failure, Success}
import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import scala.concurrent.Future
import akka.stream.ActorMaterializer
import akka.util.ByteString
import fun.outerworld.cmd.parser.CommandsParser
import fun.outerworld.tracking.framework.WhatHappened

import scala.collection.mutable.ListBuffer
import scala.io.Source
import fun.outerworld.Constants._

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
    * @param httpPath The http path from there to get the files from
    * @return a future with details of the execution of each command
    **/
  private def execute(commands: List[Command], httpPath: String ): Future[WhatHappened] = {

    for ( command ←  commands){

    }

  }





  def receive: Receive = {
    case (parameters:Map[String,String]) if {parameters.getOrElse(COMMANDS_FILE,EMPTY_STRING).toLowerCase.startsWith(HTTP)} ⇒
      val fullUrl = parameters.get(COMMANDS_FILE).get
      val httpPath = fullUrl.substring(0,fullUrl.lastIndexOf("/"))
      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri=fullUrl))
      val remainingParameters = parameters  - (COMMANDS_FILE)// + ("oo", COMMANDS_FILE.split("\/\\s+$").mkString)

      val mySender = sender
      responseFuture onComplete {
        case Success(httpResponse) ⇒
          httpResponse.entity.dataBytes.runFold(ByteString(EMPTY_STRING))(_ ++ _).foreach {
            body ⇒ {
              val commands=MyParser(body.utf8String,remainingParameters)_1
              execute(commands,httpPath)

            }
          }
        case Failure(f) ⇒ mySender ! f
        case _ ⇒ log.error("Unexpected error @ CommandsExecutor")
      }

    case resp @ HttpResponse(code, _, _, _) ⇒
      log.info("Request failed, response code: " + code)
      resp.discardEntityBytes()
    case _ ⇒
      // todo: add this to tracker
      log.error("Unexpected condition reached @ CommandsExecutor. Make sure you use only http(s) urls")

  }



}

object MyParser extends CommandsParser {
  def apply(body: String,parameters:Map[String,String])= compile(body,parameters)

  def compile(body: String,parameters:Map[String,String]): (List[Command], List[Any]) = {
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

