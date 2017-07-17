package fun.outerworld.cmd

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers, WordSpecLike}

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by romeu on 06/07/17.
  */
class CommandsExecutorTest extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


}

class MyParserTest extends FlatSpec with Matchers{
  "MyParser" should "parse a whole file" in {
    val filename = "scenarios/first/first.commands"
    val body = Source.fromResource(filename).mkString
    val commands = MyParser(body)
    commands._1 should have size 7
    commands._2 should have size 5

  }
}