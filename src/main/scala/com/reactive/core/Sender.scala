package com.reactive.core

import java.io.{File, PrintWriter}
import java.nio.file.Paths

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

//case class StudentScore(id: String, sex: String, math: Double, chinese: Double, english: Double)

object Sender extends SLF4JLogging{
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("sender")
    implicit val materializer = ActorMaterializer()

    val (host, port) = ("127.0.0.1", 8888)
    val serverConnection: Flow[ByteString, ByteString, Future[Tcp.OutgoingConnection]] =
      Tcp().outgoingConnection(host, port)
//    val projectDir = System.getProperty("user.dir")
    val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(Paths.get(s"/tmp/data.txt"))
        .via(Framing.delimiter(ByteString("\n"), 1000, false))
        .map { line =>
            log.info(s"line is ${line.utf8String  }")
          ByteString(line + "\n") }

    val sink = Sink.onComplete({
      r => log.info("Completed with: " + r)
    })

    val flow = source.via(serverConnection)
        .to(sink)

    flow.run()
  }



  def createTestData: Unit ={
    val writer = new PrintWriter(new File("data.txt"))
    val shuffle = Random.shuffle((1 to 100000).toList)
    val data = shuffle.map { id =>
      val sex = if(Random.nextDouble() > 0.5) "male" else  "female"
      val math = 100 * Random.nextDouble()
      val chinese = 100 * Random.nextDouble()
      val english = 100 * Random.nextDouble()
      s"${id},${sex},${math},${chinese},${english}"
    }.mkString("\n")
    writer.write(data)
    writer.close()
  }


}
