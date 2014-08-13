package com.rayslava

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

import com.rayslava.scbnc.parser

case class Vote(id: Int)

class MyActor extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case "test" => log.info("received test")
    case vote @ Vote(id) => log.info("VOTE " + vote.id)
    case msg @ parser.Message(text) => log.info("Chat message for parser '" + msg.text +"'")
    case _      => log.info("received unknown message")
  }
}

package object scbnc {

  /** Squares it
    * @param Arg lolint
    * @return Int
    */
  def test (Arg: Integer): Integer = (Arg * 3) * (Arg * 3)

  def main(args: Array[String]) = {
    println("Hey there!")
    val system = ActorSystem("MainSys")

    val mya = system.actorOf(Props[MyActor], "mya")
    val initial_parser = system.actorOf(Props[parser.Parser], "initial_parser")

    mya ! "test"

    println( test(4) )

    mya ! new Vote(12)

    val msg = new parser.Message("Lol")
    initial_parser ! msg

    system.shutdown()

  }
}