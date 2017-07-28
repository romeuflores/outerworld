package fun.outerworld.session

import akka.actor.{Actor, ActorLogging}
import fun.outerworld.cmd.StubbingMode
import fun.outerworld.tracking.framework.AllFine

class Session (val scenario:String) extends Actor with ActorLogging {

  var myMode:StubbingMode=_

  override def receive: Receive = {
    case mode: StubbingMode ⇒ myMode=mode; sender() ! AllFine()
  }
}
