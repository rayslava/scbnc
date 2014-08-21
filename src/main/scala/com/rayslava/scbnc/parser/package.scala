package com.rayslava.scbnc.parser
/** This package implements functions for parsing received messages and
  * extract all the interesting information from there
  */

import akka.actor.Actor
import akka.event.Logging
import com.rayslava.scbnc.types.Message

/** Just an http link
  * 
  * Needs to be parsed and classified
  * @param text --- link text, starting with "http://"
  */
case class Link(text: String);

class Parser extends Actor {
  val log = Logging(context.system, this)

  def parse(msg: Message): Unit = {
    val linkRegex = """(http://[^\s]+)(\s|$)""".r
    log.debug("Parsing " + msg)
    linkRegex findAllIn msg.text foreach (
      _ match {
        case linkRegex(link, _) => download(link)
        case _ => log.debug("No links found")
      })
    sender ! msg.text
  }

  def download(link: String) = {
    log.debug("Download request for '" + link + "'")
  }

  def receive = {
    case msg @ Message(text) => parse(msg)
  }
}
