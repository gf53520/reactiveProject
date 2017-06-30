package com.reactive.stream.core

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.util.ByteString
import akka.stream.javadsl.{Framing, FramingTruncation}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source, Tcp}
import com.reactive.stream.protocol.StudentScore

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by guifeng on 2017/6/25.
  */

class Receiver(host: String, port: Int)(implicit val system: ActorSystem)
    extends MaterializerHelper with SLF4JLogging {

  import system.dispatcher

  implicit val materializer = getMaterializer(system)

  private val frameDecoder = Framing.delimiter(ByteString("\n"), maximumFrameLength = 1024)

  def run(): Unit = {
    val connections: Source[Tcp.IncomingConnection, Future[Tcp.ServerBinding]] = Tcp().bind(host, port)
    parserStudentHandler(connections, system)
    // echoHandler(connections)
  }

  def parserStudentHandler(connections: Source[Tcp.IncomingConnection, Future[Tcp.ServerBinding]],
                           system: ActorSystem): Unit = {
    val handler = Sink.foreach[Tcp.IncomingConnection] { conn =>
      println(s"New connection from: ${conn.remoteAddress}")
      conn.flow.via(frameDecoder)
          .map(_.utf8String.split(",")) // mapping to an actor
          .filter { arr => arr(1) == "male" }
          .mapConcat(arr => StudentScore(arr).toList) // mapConcat => flatMap
          .to(Sink.foreach({ stu =>
        log.info(s"studentScore is: ${stu}")
        //Thread.sleep(100)
      }))
    }

    val binding: Future[Tcp.ServerBinding] = connections.to(handler).run()
    binding.onComplete {
      case Success(b) =>
        println("Server started, listening on: " + b.localAddress)
      case Failure(ex) =>
        println(s"Server bind to $host:$port failed.", ex)
        system.terminate()
    }
  }

  def echoHandler(connections: Source[Tcp.IncomingConnection, Future[Tcp.ServerBinding]]): Unit = {
    val echoHandler = Sink.foreach[Tcp.IncomingConnection] {
      _.flow.join(Flow[ByteString]).run()
    }
    connections runForeach { connection =>
      println(s"New connection from: ${connection.remoteAddress}")
      val echo = Flow[ByteString]
          .via(frameDecoder)
          .map(_.utf8String + "\n")
          .map(ByteString(_))
      connection.handleWith(echo)
    }
  }
}

object Receiver {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("receiver")
    val (host, port) = ("127.0.0.1", 8888)
    val receiver = new Receiver(host, port)
    receiver.run()
  }
}
