package fun.outerworld.cmd

/**
  * Created by romeu on 05/07/17.
  */

sealed case class CommandType(name:String){
  override def toString: String = name
}

object CommandType{

  object BEGIN_SESSION extends CommandType("begin/session")
  object END_SESSION extends CommandType("end/session")
  object PUT_STUB extends CommandType("put/stub")
  object DELETE_STUBS extends CommandType("delete/stubs")
  object PUT_DELAY extends CommandType("put/delay")
  object GET_RESPONSE extends CommandType("get/response")
  object DO_NOTHING extends CommandType("nothing")
  object BADLY_FORMED extends CommandType("badlyformed")

  val values = List(BEGIN_SESSION,
                    END_SESSION,
                    PUT_STUB,
                    DELETE_STUBS,
                    PUT_DELAY,
                    GET_RESPONSE
  )


}
case class Command (val commandType: CommandType, val parameters: Map[String,String], val filenames: Seq[String] ){

}