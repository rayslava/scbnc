package com.rayslava.scbnc.parser
/** This package implements functions for parsing received messages and
    extract all the interesting information from there
  */

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem
import scala.util.matching

/** One single text message
  *
  * @param text --- String with message
  */
case class Message(text: String) {
  override def toString = text
}

/** Just an http link
  * 
  * Needs to be parsed and classified
  * @param text --- link text, starting with "http://"
  */
case class Link(text: String);

class Parser extends Actor {
  val log = Logging(context.system, this)

  def parse(msg: Message) = {
    val linkRegex = new scala.util.matching.Regex(""".*(http://[^\s]+)(\s|$)""", "link")
    log.debug("Parsing " + msg)
    sender ! "ok"
    linkRegex.findAllIn(msg.text).matchData foreach {m => log.error(m.group("link"))}
    msg.text
  }

  def download(link: String) =
    log.debug("Download request for " + link)

  def receive = {
    case msg @ Message(text) => parse(msg)
  }
}
