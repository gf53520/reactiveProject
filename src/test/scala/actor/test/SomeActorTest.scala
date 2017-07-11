package actor.test

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by guifeng on 2017/7/11.
  */

class TestActor extends Actor {
  val i = 10

  override def receive: Receive = {
    case fun: (Int => Any) =>
      sender ! fun(10)
  }
}


object SomeActorTest {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("a")

    implicit val timeout: Timeout = Timeout(15L, TimeUnit.SECONDS)

    val c = 10000
    val fun: (Int) => Any = (value: Int) => value + "aaaaaa"
    val fun2: (Int) => Any = (value: Int) => value + 100000

    val actor = system.actorOf(Props(new TestActor))

    val future = (actor ? fun).mapTo[String]

    future onComplete{
      case Success(res) => println(s" res: ${res}")
      case Failure(ex) => ex.printStackTrace()
    }

    val future2 = (actor ? fun2).mapTo[Int]

    future2 onComplete{
      case Success(res) => println(s" res: ${res}")
      case Failure(ex) => ex.printStackTrace()
    }
  }
}
