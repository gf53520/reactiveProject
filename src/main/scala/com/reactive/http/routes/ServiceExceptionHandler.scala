package com.reactive.http.routes

import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.Directives._

/**
  * Created by guifeng on 2017/6/30.
  */

trait ServiceExceptionHandler extends SLF4JLogging{
  val serviceExceptionHandler = ExceptionHandler {
    case ex: Exception =>
      extractUri { uri =>
        log.error(s"Calling the uri: ${uri.toRelative} failed.", ex)
        complete(HttpResponse(InternalServerError, entity = HttpEntity(Option(ex.getLocalizedMessage).getOrElse("unknown"))))
      }
  }
}
