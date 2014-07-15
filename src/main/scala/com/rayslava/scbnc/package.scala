package com.rayslava

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

case class Vote(id: Int)

class MyActor extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case "test" => log.info("received test")
    case vote @ Vote(id) => log.error("VOTE " + vote.id)
    case _      => log.info("received unknown message")
  }
}


package object scbnc {

  /** Squares it
    * @param Arg lolint
    * @return Int
    */
  def test (Arg: Integer) = (Arg * 4) * (Arg * 3)

  def main(args: Array[String]) = {
    println("Hey there!")

    println( test(4) )

	val system = ActorSystem("MainSys")

    val mya = system.actorOf(Props[MyActor], "mya")

    mya ! "test"

    mya ! new Vote(12)

    system.shutdown()

  }
}
