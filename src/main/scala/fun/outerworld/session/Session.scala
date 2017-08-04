package fun.outerworld.session


import akka.actor.{Actor, ActorLogging, ActorRef, Identify, Props}
import akka.pattern.ask
import akka.util.Timeout
import fun.outerworld.cmd.CommandType.BEGIN_SESSION
import fun.outerworld.cmd.{CommandType, StubbingMode}
import fun.outerworld.cmd.StubbingMode._
import fun.outerworld.scenario.Scenario

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class Session (val scenarioId:String) extends Actor with ActorLogging {

  var myMode:StubbingMode=DORMANT
  var sessionState: SessionState = _
  var myScenario: ActorRef = _

  override def receive: Receive = {

    case (commandType : CommandType, mode: StubbingMode)  ⇒ {
      val mySender = sender()
      (commandType,myMode,mode) match {
        case (_, modeFrom, modeTo) ⇒ if (modeFrom == modeTo) {
          // a warning that it is already in this mode

        }
        case (BEGIN_SESSION, modeFrom, modeTo) if (modeFrom != DORMANT) ⇒ {
          // Error - I can only begin a session in dormant mode
        }
        case (BEGIN_SESSION, DORMANT, RECORD) ⇒ {
          // Search for an existing actor for my scenarioId. In case it doesn't exist, create one.
          // I really wonder whether it shouldn't be the other way around: create and catch... But here it could be
          // more likely that a scenario exist for a
          implicit val timeout  = Timeout(50 milliseconds)
          context.system.actorSelection(scenarioId) ? Identify onComplete {
            case Success (scenario: ActorRef) ⇒ myScenario = scenario /// hmmm do I need to thrash the scenario?
            case Failure(_) ⇒  myScenario =context.system.actorOf(Props(new Scenario()), scenarioId)
          }
        }

      }
    }
  }
}

sealed case class SessionState(){

}

object SessionState{
  object RECORD extends SessionState
  object PLAYBACK extends SessionState
  object NONE extends SessionState


}