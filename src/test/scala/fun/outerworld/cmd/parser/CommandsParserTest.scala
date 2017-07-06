package fun.outerworld.cmd.parser


import fun.outerworld.cmd.CommandType
import org.scalatest._


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
      case Success(result:T,_) ⇒ assert (result.equals(expected))
      case _: NoSuccess ⇒  fail
    }
  }

  "A CommandsParser" should "parse a single word" in {
    val result = parse(word, "morethan99ssss")
    validateParsedResult(result, "morethan")

  }
  it should  "parse a number" in {
    val result = parse(number, "99oooo")
    validateParsedResult(result, 99)
  }
  it should "aha! parse a Command" in {
    val result = parse(command, "put/stub")
    validateParsedResult(result, CommandType.PUT_STUB)
  }
}
