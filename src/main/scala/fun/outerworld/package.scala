package fun.outerworld

import akka.actor.{ActorContext, Props}
import akka.pattern.AskableActorSelection
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by romeu on 25/06/17.
  */
package object Constants {

  // ##################### Actor system name(s) ################
  val OUTERWORLD_SYSTEM="outerworld-system"

  // ##################### Actor name(s) #######################
  val COMMANDS_EXECUTOR="commands-executor"



  // ##################### HTTP routes ##########################
  val STUBO_COMMANDS_PATH="/stubo/api/exec/cmds"

  // ##################### HTTP parameter names #################
  val COMMANDS_FILE="cmdFile"

}
package object Implicit {

  implicit class StringAsActor(val actorPath: String)(implicit val context:ActorContext, implicit val props:Props)  {
    def ask(message: Any)(implicit timeout: Timeout): Future[Any] = {
      try {
        new AskableActorSelection(context.actorSelection(actorPath)) ? message

      } catch {
        case t:Throwable ⇒ {
          Future (t)
        }
      }

    }
    def ?(message: Any)(implicit timeout: Timeout): Future[Any] = ask(message)(timeout)
  }

  implicit val timeout = Timeout(15.seconds)


  implicit def elvisOperator[T](alt: =>T) = new {
    def ?:[A >: T](pred: A) = if (pred == null) alt else pred
  }
}

