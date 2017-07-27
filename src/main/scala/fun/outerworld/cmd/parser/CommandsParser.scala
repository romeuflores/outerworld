package fun.outerworld.cmd.parser

import fun.outerworld.cmd.CommandType.BEGIN_SESSION
import fun.outerworld.cmd._
import fun.outerworld.Constants._
import fun.outerworld.tracking.framework.{GenericParsingIssue, WrongStubbingMode}

import scala.util.parsing.combinator.RegexParsers

/**
  * Created by romeu on 05/07/17.
  * This is a very simple parser. It does lexical and syntactical analysis in a very straightforward way.
  * @See https://github.com/scala/scala-parser-combinators
  * @See https://enear.github.io/2016/03/31/parser-combinators/
  * @See ISBN 1292024348
  */
class CommandsParser extends RegexParsers{
  // Parser overrides
  override def skipWhitespace = true
  override val whiteSpace = "[ \t]+".r

  // tokens
  def word         : Parser[String]            = """(?i)[a-z0-9_\\-]+""".r ^^ { _.toString }
  def filename     : Parser[String]            = """(?i)[a-z0-9_\\-\\.\s]+""".r ^^ { _.toString }
  def number       : Parser[Int]               = """(0|[1-9]\d*)""".r ^^ { _.toInt }
  def commandType  : Parser[CommandType]       = {"(?i)(" + CommandType.values.mkString("|") + ")"}.r ^^  {s ⇒ CommandType(s.toLowerCase)}
  def stubbingMode : Parser[StubbingMode]      = {"(?i)(" + StubbingMode.values.mkString("|") + ")"}.r ^^  {s ⇒ StubbingMode(s.toLowerCase)}

  def equals       : Parser[String]            = """="""
  def quotation    : Parser[String]            = """?"""
  def comma        : Parser[String]            = ""","""
  def ampersand    : Parser[String]            = """&"""
  def lineEnd      : Parser[String]            = """(\n|\r\n|\n\r|$)""".r
  //def fileEnd     : Parser[String]            = """$""".r
  def comments     : Parser[String]            = """#.*""".r

  //expressions
  def parameter         : Parser[Map[String,String]]      = word ~ equals ~ word                ^^ {  case word1 ~ _ ~ word2 ⇒  Map(word1->word2) }
  def parameters        : Parser[Map[String,String]]      = parameter ~ ampersand ~ parameters  ^^ {  case parameter ~ _ ~ parameters ⇒  parameters ++ parameter } | parameter

  def csvSeq            : Parser[Seq[String]]             = comma ~ filename ~ csvSeq           ^^ {  case _ ~ filename ~ list ⇒ filename +: list }        | comma ~ filename ^^ { case _ ~ filename ⇒ Seq(filename)}

  //def commentsLine      : Parser[Command]                 = comments ~ (lineEnd)        ^^ {  case comments ~ _ ⇒  Command(CommandType.DO_NOTHING, Map(),Seq(comments))}
  //def badlyFormedLine   : Parser[Command]                 = """.*""" ~ (lineEnd)        ^^ {  case badlyFormed ~ _ ⇒  Command(CommandType.BADLY_FORMED, Map(),Seq(badlyFormed))}
  //def emptyLine         : Parser[Command]                 = (lineEnd)                   ^^ {  case _ ⇒  Command(CommandType.DO_NOTHING, Map(),Seq())}

  def commandLine       : Parser[Command]                 = commandType ~ quotation ~ parameters.? ~ csvSeq.? ~ comments.? ~ (lineEnd) ^^ {
    case commandType ~ _ ~ parameters ~ csvSeq ~ _ ~ _    ⇒  commandType match {
      case BEGIN_SESSION if (parameters.isDefined
        && (parameters.get.get(SESSION).isDefined)
        && (parameters.get.get(SCENARIO).isDefined)
        && (parameters.get.get(MODE).isDefined)) ⇒ {
        val sMode = parse(stubbingMode, parameters.get.get(MODE).get)
        sMode match {
          case Success(mode, _) ⇒ BeginSessionCommand(parameters.get.get(SESSION).get, parameters.get.get(SCENARIO).get, mode)
          case _                ⇒ DoNothingCommand(parameters.getOrElse(Map()), csvSeq.getOrElse(Seq()),WrongStubbingMode())
        }
      }

        //todo: improve this  either return the command type information; or improve parsing mechanism
      case _                                              ⇒ DoNothingCommand(parameters.getOrElse(Map()), csvSeq.getOrElse(Seq()), GenericParsingIssue(payload="Not possible to execute command: [" + commandType + "]. Parameters: [" + parameters.getOrElse("none")+ "]"))
    }
  }

  def tokens            : Parser [List[Command]]          = {phrase(rep1(commandLine))}
}
