package fun.outerworld.cmd

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import fun.outerworld.tracking.framework.AllFine
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class DoNothingCommandTest extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with FlatSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A DoNothingCommand" should "quickly return a success saying what happened " in {
    val objectUnderTest = DoNothingCommand(whatHappened = AllFine())
    val result = objectUnderTest.execute
    result.onComplete{
      case Success (s:AllFine) ⇒ print (s); succeed
      case _                   ⇒ fail
    }

  }


}
