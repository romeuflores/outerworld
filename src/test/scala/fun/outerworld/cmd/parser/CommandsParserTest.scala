package fun.outerworld.cmd.parser


import fun.outerworld.cmd._
import fun.outerworld.tracking.framework.{GenericParsingIssue, WrongStubbingMode}
import org.scalatest._

import scala.collection.mutable.ListBuffer
import scala.io.Source


/**
  * Created by romeu on 05/07/17.
  */
//@RunWith(classOf[JUnitRunner])
class CommandsParserTest extends CommandsParser with FlatSpecLike with Matchers with BeforeAndAfterEach {

  var parser: CommandsParser = _

  override protected def beforeEach(): Unit = {
    parser= new CommandsParser()
  }

  def  validateParsedResult[T](parseResult:ParseResult[T], expected:T): Unit ={
    parseResult match {
      case Success(result,_) ⇒ assert (result.equals(expected))
      case NoSuccess (failure) ⇒  print (failure) ; fail
    }
  }

  "A CommandsParser" should "parse a single word" in {
    val result = parse(word, "morethan99ssss")
    validateParsedResult(result, "morethan99ssss")

  }
  it should  "parse a number" in {
    val result = parse(number, "99oooo")
    validateParsedResult(result, 99)
  }
  it should "aha! parse a Command" in {
    val result = parse(commandType, "put/stub")
    validateParsedResult(result, CommandType.PUT_STUB)
  }
  it should "parse a Command ignoring case" in {
    val result = parse(commandType, "BegiN/Session")
    validateParsedResult(result, CommandType.BEGIN_SESSION)
  }
  it should "parse a key=value expression as a named parameter" in {
    val result = parse(parameter, "scenario=run_Forrest-run")
    validateParsedResult(result, Map ("scenario" → "run_Forrest-run"))
  }
  it should "parse a list of key=value expressions as named parameters" in {
    val result = parse(parameters, "scenario=run_Forrest-run&session=coast_to_coast")
    validateParsedResult(result, Map ("scenario" → "run_Forrest-run", "session" → "coast_to_coast"))
  }
  it should "parse a sequence of comma separated values as file names which may include spaces" in {
    val result = parse(csvSeq, ",aGiven.textMatcher_1,a Given.response_1")
    validateParsedResult(result, Seq ("aGiven.textMatcher_1" , "a Given.response_1"))
  }
  /*it should "parse a whole put/stub line" in {
    val myCommandLine = "put/stub?session=coast_to_coast,aGiven.textMatcher_1,aGiven.response_1\n"
    val result = parse(commandLine, myCommandLine)
    val expectedCommand = Command(CommandType.PUT_STUB, Map("session" -> "coast_to_coast"), Seq("aGiven.textMatcher_1", "aGiven.response_1"))
    validateParsedResult(result, expectedCommand)
  }*/
  it should "parse a whole begin/session line" in {
    val myCommandLine = "begin/session?scenario=first&session=first_1&mode=record\n"
    val result = parse(commandLine, myCommandLine)
    val expectedCommand = BeginSessionCommand("first_1", "first", StubbingMode.RECORD )
    validateParsedResult(result, expectedCommand)
  }
  it should "parse a begin/session line as a DoNothingCommand, when no mode provided" in {
    val myCommandLine = "begin/session?scenario=first&session=first_1\n"
    val result = parse(commandLine, myCommandLine)
    result match {
      case Success(result: DoNothingCommand, _) ⇒ result.whatHappened shouldBe a[GenericParsingIssue]
      case NoSuccess(failure) ⇒ print(failure); fail
      case _ ⇒ ; fail
    }
  }
    it should "parse a begin/session line as a DoNothingCommand with a WrongStubbingMode, when mode is invalid" in {
      val myCommandLine = "begin/session?scenario=first&session=first_1&mode=xxx\n"
      val result = parse(commandLine, myCommandLine)
      result match {
        case Success(result:DoNothingCommand,_) ⇒ result.whatHappened shouldBe a [WrongStubbingMode]
        case NoSuccess (failure) ⇒  print (failure) ; fail
        case _ ⇒  ; fail
      }

  }
  /*it should "parse a whole end/session line" in {
    val myCommandLine = "end/session?session=coast_to_coast\n"
    val result = parse(commandLine, myCommandLine)
    val expectedCommand = Command(CommandType.END_SESSION, Map("session" -> "coast_to_coast"), Seq())
    validateParsedResult(result, expectedCommand)
  }*/

  it should  "parse a file line by line" in {
    val filename = "scenarios/first/first.commands"
    val commands:ListBuffer[Command]= ListBuffer()
    val failures:ListBuffer[Any]= ListBuffer()
    val lines = Source.fromResource(filename).getLines()
    for (line ← lines){
      val result = parse (commandLine, line)
      result match {
        case Success(result,_)    ⇒ commands.append(result)
        case NoSuccess (failure)  ⇒  failures.append(failure)
      }
    }
    commands should have size 7
    failures should have size 5
  }

  /* Will leave this out for now. Too much trouble trying to parse a whole file with line terminators.
  it should  "parse a whole file" in {
    val filename = "scenarios/first/first.commands"

    val body = Source.fromResource(filename).mkString
    val result =parse (tokens, body)

    print()
  }*/

}
