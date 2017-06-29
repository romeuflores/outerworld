package integration

import akka.http.scaladsl.server.directives._
import akka.actor.{ActorSystem, Props}
import fun.outerworld.Implicit._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import fun.outerworld.cmd.CommandsExecutor
import ContentTypeResolver.Default
import akka.stream.ActorMaterializer
import akka.pattern.ask

import scala.concurrent.duration._
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import fun.outerworld.Constants._


import scala.concurrent.Await



/*import org.junit.runner.RunWith
@RunWith(classOf[JUnitRunner])*/

class DevIntegrationTest extends FlatSpec with Matchers with BeforeAndAfterAll{
  implicit val system = ActorSystem(OUTERWORLD_SYSTEM)


  override def beforeAll(){
    implicit val materializer = ActorMaterializer()
    val route =
      pathPrefix("scenarios") {
        // Todo: replace getFromDirectory by getFromResourcesDirectory
        getFromDirectory("/home/romeu/outerworld/build/resources/test/scenarios")
      }
    Http().bindAndHandle(route, "localhost", 8090)
  }

  override def afterAll(){
   system.terminate
 }


  "A CommandsExecutor" should "process a file from a commands url" in {
    val commandsExecutor = system.actorOf(Props[CommandsExecutor], COMMANDS_EXECUTOR)

    val commandsFile = "http://localhost:8090/scenarios/first/first.commands"
    val response = Await.result(commandsExecutor ? commandsFile, 5 minutes )
    //todo: improve this assertion
    assert(response!=null)
    /*(commandsExecutor ? commandsFile) onComplete{
      case Success(AnyRef) => succeed
      case Failure(AnyRef) => fail("Couldn't process commands file " + commandsFile)
      case _ =>  fail("Couldn't process commands file " + commandsFile + ". Don't know what happened. ")
    }*/

  }
}
