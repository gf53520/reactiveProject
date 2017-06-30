package com.reactive.stream

import java.util.concurrent.TimeUnit

import akka.event.slf4j.SLF4JLogging
import com.yammer.metrics.Metrics
import com.yammer.metrics.reporting.ConsoleReporter

/**
  * Created by guifeng on 2017/6/26.
  */

object TimerMetricsTest extends SLF4JLogging{

  // rate单位为s, 响应时间单位为微秒
  val timer = Metrics.newTimer(getClass, "request-handler-timer",
    TimeUnit.MILLISECONDS, TimeUnit.SECONDS)

  def handleRequest(): Unit = {
    val context = timer.time
    Thread.sleep(1)
    context.stop()
  }

  def main(args: Array[String]): Unit = {
    ConsoleReporter.enable(5, TimeUnit.SECONDS)
    for(i <- 0 until 1000)
      handleRequest()
    Thread.sleep(1000000)
  }
}