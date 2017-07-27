package fun.outerworld.boot

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import fun.outerworld.Constants._
import fun.outerworld.Implicit._
import fun.outerworld.cmd.CommandsExecutor
import akka.pattern.ask

import scala.util.{Failure, Success}
/**
  * Created by romeu on 15/06/17.
  */
object Webserver extends App{


    implicit val system = ActorSystem(OUTERWORLD_SYSTEM)
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end`
    implicit val executionContext = system.dispatcher
    val commandsExecutor = system.actorOf(Props[CommandsExecutor], COMMANDS_EXECUTOR)



    val route =
      path(STUBO_COMMANDS_PATH) {
        parameterMap {
          parameters =>
            onComplete(commandsExecutor ? parameters){
              case Success(responseMessage: String) => complete(StatusCodes.OK, responseMessage)
              case Failure(failure) => throw failure
              case _ =>  ???
            }
        }
      }
    Http().bindAndHandle(route, "localhost", 8080)



}
