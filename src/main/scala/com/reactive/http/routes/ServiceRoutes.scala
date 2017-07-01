package com.reactive.http.routes

import akka.actor.ActorRef
import akka.pattern._
import akka.http.scaladsl.server.{Directive1, Directives, Route}
import akka.util.{ByteString, Timeout}

import scala.concurrent.duration._
import com.reactive.http.protocol.Protocols.{EchoCommand, EchoResponse}
import com.reactive.http.serializer.JsonSerializer

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by guifeng on 2017/6/30.
  */
class ServiceRoutes(worker: ActorRef, implicit val executionContext: ExecutionContext)
    extends Directives with JsonSerializer with ServiceExceptionHandler {

  implicit val timeout = Timeout(10.seconds)

  lazy val routes: Route = handleExceptions(serviceExceptionHandler)(echoRoute)

  def echoRoute: Route = {
    path("echo") {
      post {
        entity(as[EchoCommand]) { command =>
          log.info(s"[Echo]: Receive an request: ${command}")
          complete {
            for {
              response <- (worker ? command).mapTo[EchoResponse]
            } yield response match {
              case result: EchoResponse =>
                response
            }
          }
        }
      }
    }
  }

  // or others routes
}

