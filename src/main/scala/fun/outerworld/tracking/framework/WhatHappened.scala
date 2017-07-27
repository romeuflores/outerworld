package fun.outerworld.tracking.framework

import fun.outerworld.tracking.framework.ErrorCodes._

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
  object GENERIC_PARSING_ISSUE extends ErrorCodes(2, "")

  val values = List(OK,
    WRONG_STUBBING_MODE,
    GENERIC_PARSING_ISSUE)

}

abstract class WhatHappened(val what: ErrorCodes, val payload:String) extends Throwable{
  def whatElse: Seq[WhatHappened] =Seq()

  override def toString: String = {
    what + "; [additional]: " + payload + "\n" + whatElse.mkString("\n") /*+ {
      case whatElse.isEmpty =>  ""
      case _ => whatElse.mkString("\n")

    }*/
  }


}

case class WrongStubbingMode  (override val what: ErrorCodes = WRONG_STUBBING_MODE  , override val payload: String = "Use either 'record' or 'playback'") extends WhatHappened(what,payload)
case class GenericParsingIssue(override val what: ErrorCodes = GENERIC_PARSING_ISSUE, override val payload: String) extends WhatHappened(what,payload)
case class AllFine            (override val what: ErrorCodes = OK, override val payload: String="") extends WhatHappened(what,payload)
