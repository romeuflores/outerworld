package fun.outerworld.boot

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.HttpEntity

import scala.io.StdIn
/**
  * Created by romeu on 15/06/17.
  */
object Webserver {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("outerworld-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

}

}
