package fun.outerworld.cmd

import akka.actor.{Actor, ActorLogging}




class CommandsExecutor () extends Actor with ActorLogging {


  override def receive: Receive = {
    case cmdFile:String ⇒ {

    }


  }

}
