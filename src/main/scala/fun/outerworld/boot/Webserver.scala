package fun.outerworld.boot

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer

/**
  * Created by romeu on 15/06/17.
  */
object Webserver extends App {

    implicit val system = ActorSystem("outerworld-system")
    implicit val materializer = ActorMaterializer()




}
