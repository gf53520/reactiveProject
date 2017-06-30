package com.reactive.stream.core

import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}

/**
  * Created by guifeng on 2017/6/26.
  */

trait MaterializerHelper extends SLF4JLogging {

  val decider: Supervision.Decider = {
    case ex: Exception =>
      log.info("Stream occur an error.", ex)
      Supervision.Resume
    case _ => Supervision.Stop
  }

  def getMaterializer(implicit system: ActorSystem) = ActorMaterializer(
    ActorMaterializerSettings(system)
        .withSupervisionStrategy(decider)
  )
}
