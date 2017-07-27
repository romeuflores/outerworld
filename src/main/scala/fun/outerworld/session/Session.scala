package fun.outerworld.session

import akka.actor.{Actor, ActorLogging}

class Session (val scenario:String) extends Actor with ActorLogging {
  override def receive: Receive = ???
}
