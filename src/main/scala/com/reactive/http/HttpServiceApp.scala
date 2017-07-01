package com.reactive.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.reactive.http.actors.EchoActor
import com.reactive.http.routes.ServiceRoutes

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by guifeng on 2017/7/1.
  */
object HttpServiceApp {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("HttpServiceApp")
    implicit val materializer = ActorMaterializer()

    val echoActor = system.actorOf(EchoActor.props, EchoActor.name)
    val serviceRoute = new ServiceRoutes(echoActor, system.dispatcher)

    import system.dispatcher

    val bindingFuture: Future[Http.ServerBinding] = Http(system).bindAndHandle(serviceRoute.routes, "localhost", 9999)

    bindingFuture.onComplete {
      case Success(b) =>
        println("Server started, listening on: " + b.localAddress)
      case Failure(ex) =>
        println(s"Server bind failed.", ex)
        system.terminate()
    }
  }
}
