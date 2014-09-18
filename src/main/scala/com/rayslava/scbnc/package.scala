package com.rayslava

import akka.actor.{ActorSystem, Props}
import com.rayslava.scbnc.irc.Client
import com.rayslava.scbnc.parser.Parser
import com.rayslava.scbnc.types.{DCMessage, LoginMessage}

package object scbnc {

  /** Squares it
    * @param Arg lolint
    * @return Int
    */
  def func (Arg: Integer): Integer = (Arg * 3) * (Arg * 3)

  def main(args: Array[String]) = {
    println("Hey there!")
    val system = ActorSystem("MainSys")

    val initialParser = system.actorOf(Props[Parser], "initial_parser")
    val ircClient = system.actorOf(Props(new Client("irc.freenode.net", 6667, initialParser)))

    ircClient ! "connect"

    Thread.sleep(2000L)

    ircClient ! LoginMessage("scbnc")

    Thread.sleep(10000L)

    ircClient ! DCMessage("bye")

    Thread.sleep(2000L)

    system.shutdown()

  }
}
