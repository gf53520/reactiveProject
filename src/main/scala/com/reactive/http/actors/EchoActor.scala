package com.reactive.http.actors

import akka.actor.{Actor, Props}
import com.reactive.http.protocol.Protocols._
import com.reactive.http.utils.EchoActorUtils

/**
  * Created by guifeng on 2017/6/30.
  */
class EchoActor extends Actor with EchoActorUtils{

  override def receive: Receive = {
    case request @EchoCommand(_) =>
      sender ! handEchoMsg(request)
  }
}

object EchoActor {
  def props = Props(new EchoActor)
  def name = "EchoActor"
}
