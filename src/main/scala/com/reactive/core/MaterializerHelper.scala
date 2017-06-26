package com.reactive.core

import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}

/**
  * Created by guifeng on 2017/6/26.
  */

abstract class MaterializerHelper(system: ActorSystem) extends SLF4JLogging {

  val decider: Supervision.Decider = {
    case ex: Exception =>
      log.info("Stream occur an error.", ex)
      Supervision.Resume
    case _ => Supervision.Stop
  }

  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system)
        .withSupervisionStrategy(decider)
  )
}
