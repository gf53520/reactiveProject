package com.reactive.metrics

import akka.actor.Actor

/**
  * Created by guifeng on 2017/6/26.
  */

/** 模板模式, 可联想装饰者模式*/
trait ActorStack extends Actor {
  def wrappedReceive: Receive

  def receive: Receive = {
    case x => if (wrappedReceive.isDefinedAt(x)) wrappedReceive(x) else unhandled(x)
  }
}