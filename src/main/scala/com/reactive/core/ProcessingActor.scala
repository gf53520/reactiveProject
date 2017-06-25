package com.reactive.core

import akka.persistence.PersistentActor
import akka.stream.actor.{ActorSubscriber, MaxInFlightRequestStrategy, RequestStrategy}

/**
  * Created by guifeng on 2017/6/25.
  */
class ProcessingActor extends PersistentActor with ActorSubscriber {

  override def persistenceId: String = "ReceiverActor1"

  var inFlight = 0

  override protected def requestStrategy: RequestStrategy = {
    new MaxInFlightRequestStrategy(10) {
      override def inFlightInternally = inFlight
    }

  }

  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = {
    case _ =>
  }

}
