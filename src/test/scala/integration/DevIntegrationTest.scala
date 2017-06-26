import akka.http.scaladsl.server.directives._
import ContentTypeResolver.Default
import akka.actor.{ActorSystem, Props}
import fun.outerworld.Implicit._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.PathMatcher
import akka.http.scaladsl.server.Directives._
import fun.outerworld.Constants._
import fun.outerworld.cmd.CommandsExecutor
import ContentTypeResolver.Default
import akka.stream.ActorMaterializer
import akka.pattern.ask
import collection.mutable.Stack
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Failure, Success}

class DevIntegrationTest extends FlatSpec with Matchers with BeforeAndAfterAll{
  implicit val system = ActorSystem("test")

  override def beforeAll(){
    implicit val materializer = ActorMaterializer()
    val route =
      path("/scenarios/first" / Segment) { pathToFile =>
        getFromResourceDirectory(pathToFile) // uses implicit ContentTypeResolver
      }
    Http().bindAndHandle(route, "localhost", 8090)
  }

  override def afterAll(){
   system.terminate()
 }


  "A CommandsExecutor" should "process a file from a commands url" in {
    val commandsExecutor = system.actorOf(Props[CommandsExecutor], COMMANDS_EXECUTOR)

    val commandsFile = "http://localhost:8090/scenarios/first/first.commands"

    (commandsExecutor ? commandsFile) onComplete{
      case Success(AnyRef) => succeed
      case Failure(AnyRef) => fail("Couldn't process commands file " + commandsFile)
      case _ =>  fail("Couldn't process commands file " + commandsFile + ". Don't know what happened. ")
    }

  }
}
