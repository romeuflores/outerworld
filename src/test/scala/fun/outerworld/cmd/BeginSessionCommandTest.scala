package fun.outerworld.cmd

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import fun.outerworld.cmd.StubbingMode.RECORD
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class BeginSessionCommandTest extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with FlatSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A BeginSessionCommand" should "begin a session " in {
    val objectUnderTest = new BeginSessionCommand("mySessionId", "myScenarioId", StubbingMode.RECORD)

    assert (objectUnderTest.mode       == StubbingMode.RECORD)
    assert (objectUnderTest.sessionId  == "mySessionId")
    assert (objectUnderTest.scenarioId == "myScenarioId")

    val result = objectUnderTest.execute
    result.onComplete{
      case Success (s)          ⇒ print (s); succeed
      case _                    ⇒ fail
    }

  }


}
