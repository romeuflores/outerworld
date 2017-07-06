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
  object DELETE_STUBW extends CommandType("delete/stubS")
  object PUT_DELAY extends CommandType("put/delay")


  val values = List(BEGIN_SESSION,
                    END_SESSION,
                    PUT_STUB,
                    PUT_DELAY)


}
case class Command (val commandType: CommandType, val parameters: Map[String,String]){

}