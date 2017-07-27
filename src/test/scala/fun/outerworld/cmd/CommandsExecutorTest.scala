package fun.outerworld.cmd

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import fun.outerworld.Constants.COMMANDS_EXECUTOR
import fun.outerworld.boot.Webserver.system
import org.scalatest._

import fun.outerworld.Constants._
import scala.io.Source

/**
  * Created by romeu on 06/07/17.
  */


object Parameters{
  def apply() =  Map (COMMANDS_FILE →   "http://localhost:8090/scenarios/first/first.commands",
                      "sesssssion"  →   "mySessionName",
                      "scenaaaario" →   "meScenarioName")
}

class CommandsExecutorTest extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with FlatSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


 "CommandsExecutor" should "process a commands url" in {
   val commandsExecutor = system.actorOf(Props[CommandsExecutor], COMMANDS_EXECUTOR)
   commandsExecutor ! Parameters()
   expectMsg("BLAH")
 }


}

class MyParserTest extends FlatSpec with Matchers{
  "MyParser" should "parse a whole file" in {
    val filename = "scenarios/first/first.commands"
    val body = Source.fromResource(filename).mkString
    val commands = MyParser(body,Parameters())
    commands._1 should have size 7
    commands._2 should have size 5

  }

}