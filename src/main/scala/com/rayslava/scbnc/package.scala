package com.rayslava

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging
import com.rayslava.scbnc.parser.Parser
import com.rayslava.scbnc.types.Message

case class Vote(id: Int)

class MyActor extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case "test" => log.info("received test")
    case vote @ Vote(id) => log.info("VOTE " + vote.id)
    case msg @ Message(text) => log.info("Chat message for parser '" + msg.text +"'")
    case _      => log.info("received unknown message")
  }
}

package object scbnc {

  /** Squares it
    * @param Arg lolint
    * @return Int
    */
  def func (Arg: Integer): Integer = (Arg * 3) * (Arg * 3)

  def main(args: Array[String]) = {
    println("Hey there!")
    val system = ActorSystem("MainSys")

    val mya = system.actorOf(Props[MyActor], "mya")
    val initial_parser = system.actorOf(Props[Parser], "initial_parser")

    mya ! "test"

    println( func(4) )

    mya ! new Vote(12)

    val msg = new Message("Lol")
    initial_parser ! msg

    system.shutdown()

  }
}
