package fun.outerworld.cmd.parser

import fun.outerworld.cmd.{Command, CommandType}

import scala.util.parsing.combinator.RegexParsers

/**
  * Created by romeu on 05/07/17.
  */
class CommandsParser extends RegexParsers{
  // Parser overrides
  override def skipWhitespace = true
  override val whiteSpace = "[ \t\r\f]+".r

  // tokens
  def word: Parser[String]                  = """[a-z]+""".r ^^ { _.toString }
  def number: Parser[Int]                   = """(0|[1-9]\d*)""".r ^^ { _.toInt }
  def command: Parser[CommandType]          = {"(" + CommandType.values.mkString("|") + ")"}.r ^^  {s ⇒ CommandType(s)}




}
