package fun.outerworld.cmd

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by romeu on 06/07/17.
  */
class CommandTypeTest extends FlatSpec with Matchers {
  "A CommandType" should "contain basic commands" in {
    val result = "(" + CommandType.values.mkString("|") + ")"
    result should include ("put/delay")
    result should include ("put/stub")

  }
}
