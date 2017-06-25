package com.reactive.core

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.util.ByteString
import akka.stream.ActorMaterializer
import akka.stream.javadsl.{Framing, FramingTruncation}
import akka.stream.scaladsl.{Flow, Sink, Source, Tcp}
import com.reacative.protocol.StudentScore

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by guifeng on 2017/6/25.
  */
class Receiver(host: String, port: Int)(implicit val system: ActorSystem) extends SLF4JLogging{

  def run(): Unit = {

    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    val connections = Tcp().bind(host, port)

    val handler = Sink.foreach[Tcp.IncomingConnection]{ conn =>
      conn.flow.via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 1000,
        FramingTruncation.ALLOW))
          .map(_.utf8String) // mapping to an actor
          .map(_.split(","))
          .filter({ arr => arr(1) == "male"})
          .mapConcat(StudentScore(_).toList) // mapConcat => flatMap
          .to(Sink.foreach({ stu =>
        log.info(s"studentScore is: ${stu}")
//        Thread.sleep(100)
      }))
    }

    val binding = connections.to(Sink.ignore).run()

    binding.onComplete {
      case Success(b) =>
        println("Server started, listening on: " + b.localAddress)
      case Failure(ex) =>
        println(s"Server bind to $host:$port failed.", ex)
        system.terminate()
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
