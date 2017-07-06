package fun.outerworld.message.framework
import fun.outerworld.Implicit._
/**
  * Created by romeu on 30/06/17.
  *
  * This trait defines the messages exchange as our actors talk to each other.
  *
  * I wonder why I am implementing this - it should exist somewhere already
  */
trait WhatHappened {
  def whatElse: List[WhatHappened]
  def code: Int
  def payload: String

  override def toString: String = {
    "code: " +code + "; payload: " + payload /*+ {
      case whatElse.isEmpty =>  ""
      case _ => whatElse.mkString("\n")

    }*/
  }


}