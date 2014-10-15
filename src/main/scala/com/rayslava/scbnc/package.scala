package com.rayslava

import akka.actor.{ActorSystem, Props}
import com.rayslava.scbnc.irc.Client
import com.rayslava.scbnc.parser.Parser
import com.rayslava.scbnc.types._
import com.typesafe.config.ConfigFactory

package object scbnc {

  /** Squares it
    * @param Arg lolint
    * @return Int
    */
  def func (Arg: Integer): Integer = (Arg * 3) * (Arg * 3)

  def main(args: Array[String]) = {
    val system = ActorSystem("MainSys")

    val ircConfig = ConfigFactory.load("irc")

    val initialParser = system.actorOf(Props[Parser], "initial_parser")
    val ircClient = system.actorOf(Props(new Client(ircConfig.getString("connection.server"), ircConfig.getInt("connection.port"), initialParser)))
    val channel = ircConfig.getString("connection.channel")

    ircClient ! "connect"

    Thread.sleep(2000L)

    ircClient ! LoginMessage("scbnc")

    Thread.sleep(1000L)

    ircClient ! JoinMessage(channel)

    Thread.sleep(1000L)

    ircClient ! Message("Hey there! scbnc is alive!", channel)
    Thread.sleep(1000L)
    ircClient ! Message("Я на полминутки, посоны", channel)
    Thread.sleep(30000L)
    ircClient ! Message("Ок, таймаут.", channel)
    ircClient ! LeaveMessage("#linux")
    ircClient ! DCMessage("bye")

    Thread.sleep(2000L)

    system.shutdown()

  }
}
