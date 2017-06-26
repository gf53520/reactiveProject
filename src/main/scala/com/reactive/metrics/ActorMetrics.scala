package com.reactive.metrics

import java.util.concurrent.TimeUnit

import com.yammer.metrics.Metrics
import com.yammer.metrics.reporting.ConsoleReporter

/**
  * Created by guifeng on 2017/6/26.
  */

trait ActorMetrics extends ActorStack {

  private val metricReceiveTimer = Metrics.newTimer(getClass, "receive-handler-timer",
    TimeUnit.MILLISECONDS, TimeUnit.SECONDS)

  override def receive: Receive = {
    case x =>
      val context = metricReceiveTimer.time()
      try {
        super.receive(x)
      } finally {
        context.stop()
      }
  }
}