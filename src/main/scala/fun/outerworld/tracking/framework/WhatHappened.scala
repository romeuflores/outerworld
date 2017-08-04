package fun.outerworld.tracking.framework

import fun.outerworld.tracking.framework.ErrorCodes._
import fun.outerworld.tracking.framework.Severity._

/**
  * Created by romeu on 30/06/17.
  *
  * This is to be used as a tracker
  *
  * It should be suitable to describe what happened on each and every transaction,
  * be it a failure or a success.abstract
  */

sealed case class ErrorCodes(code:Int, name:String){
  override def toString: String = "[" +code + "]: "+name
}

object ErrorCodes {
  // not really an error code, but something good happened...
  object OK extends ErrorCodes (200, "OK")

  object WRONG_STUBBING_MODE extends ErrorCodes  (1, "Wrong stubbing mode.")
  object GENERIC_PARSING_ISSUE extends ErrorCodes(2, "Generic parsing issue")
  object GENERIC_EXCEPTION extends ErrorCodes(3, "Generic Exception")

  object SESSION_ALREADY_IN_MODE  extends ErrorCodes (4, "Session already in this mode.")

  val values = List(OK,
    WRONG_STUBBING_MODE,
    GENERIC_PARSING_ISSUE,
    GENERIC_EXCEPTION
  )

}

/**
  * This is the severity of things that happened. It shouldn't go down to TRACE.
  */
sealed case class Severity()
object Severity{
  object FINE extends Severity
  object INFO  extends Severity
  object WARNING  extends Severity
  object ERROR extends Severity
}

abstract class WhatHappened(val what: ErrorCodes, val payload:String, val severity:Severity = WARNING) extends Throwable{
  def whatElse: Seq[WhatHappened] =Seq()
  def this (parameters:Seq[String]){
    this (payload = "substitution goes here")
  }
  override def toString: String = {
    what + "; [additional]: " + payload + "\n" + whatElse.mkString("\n") /*+ {
      case whatElse.isEmpty =>  ""
      case _ => whatElse.mkString("\n")

    }*/
  }


}


// ****************** Parsing *****************************
case class WrongStubbingMode   (override val what: ErrorCodes = WRONG_STUBBING_MODE  , override val payload: String = "Use either 'record' or 'playback'", override val severity:Severity = ERROR) extends WhatHappened(what,payload,severity)
case class GenericParsingIssue (override val what: ErrorCodes = GENERIC_PARSING_ISSUE, override val payload: String) extends WhatHappened(what,payload)

// ****************** Session *****************************

case class SessionAlreadyInMode (override val what: ErrorCodes = SESSION_ALREADY_IN_MODE,override val payload: String = "Trying to {1} a session in {2} mode, but the session is already in this mode. ", override val severity:Severity = WARNING ) extends WhatHappened(what,payload,severity)


//******************** Others *****************************

case class UnexpectedException (override val what: ErrorCodes = GENERIC_EXCEPTION, override val payload:String) extends WhatHappened(what,payload){
  def this (e:Throwable){
    this(payload=e.getMessage)
  }
}

case class AllFine             (override val what: ErrorCodes = OK, override val payload: String="", override val severity:Severity = FINE) extends WhatHappened(what,payload,severity)
