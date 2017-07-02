package com.reactive.stream.core

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.javadsl.FramingTruncation
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source, Tcp}
import akka.util.ByteString

import scala.concurrent.Future
import scala.util.Random

/**
  * Created by guifeng on 2017/6/25.
  */

object Sender extends SLF4JLogging {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("sender")
    implicit val materializer = ActorMaterializer()

    val (host, port) = ("127.0.0.1", 8888)
    val serverConnection: Flow[ByteString, ByteString, Future[Tcp.OutgoingConnection]] =
      Tcp().outgoingConnection(host, port)
//    val stream = getClass.getClassLoader.getResourceAsStream("smallDataSet")
    val stream = getClass.getClassLoader.getResourceAsStream("bigDataSet")
    val it: () => Iterator[String] = () => scala.io.Source.fromInputStream(stream).getLines

    // ---- first source solution ---
    //    val source: Source[ByteString, Future[IOResult]] =
    //        FileIO.fromPath(Paths.get(s"/tmp/smallDataSet"))
    //      .via(Framing.delimiter(ByteString("\n"), 1000, false))
    //          .map { line =>
    //            log.info(s"line is ${line  }")
    //            ByteString(line + "\n") }

    // ---- second source solution ---
    val source: Source[ByteString, NotUsed] =
      Source.fromIterator(it)
          .map { line =>
            log.info(s"line is ${line}")
            ByteString(line + "\n")
          }

    val flow = Flow[ByteString].via(serverConnection)
        .via(Framing.delimiter(ByteString("\n"), 1000, false))

    source.via(flow).runForeach({ line =>
      log.info(s"Receive data from server: ${line.utf8String}")
    })
  }


  def createTestData: Unit = {
    val writer = new PrintWriter(new File("smallDataSet"))
    val shuffle = Random.shuffle((1 to 100000).toList)
    val data = shuffle.map { id =>
      val sex = if (Random.nextDouble() > 0.5) "male" else "female"
      val math = 100 * Random.nextDouble()
      val chinese = 100 * Random.nextDouble()
      val english = 100 * Random.nextDouble()
      s"${id},${sex},${math},${chinese},${english}"
    }.mkString("\n")
    writer.write(data)
    writer.close()
  }
}
