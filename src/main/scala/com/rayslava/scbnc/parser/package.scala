package com.rayslava.scbnc.parser
/** This package implements functions for parsing received messages and
  * extract all the interesting information from there
  */

import akka.actor.Actor
import akka.event.Logging
import akka.util.ByteString
import com.rayslava.scbnc.types.Message

class Parser extends Actor {
  val unknown = "Unknown"
  val log = Logging(context.system, this)
  val storage = sender()

  /**
   * Parses a text message extracting all the interesting data from inside
   * @param msg
   */
  def parse(msg: Message): Unit = {
    val linkRegex = """(http://[^\s]+)(\s|$)""".r
    log.debug("Parsing " + msg.text)
    linkRegex findAllIn msg.text foreach {
      case linkRegex(link, _) => download(link)
    }
  }

  def download(link: String) = {
    log.debug("Download request for '" + link + "'")
  }

  // $COVERAGE-OFF$
  def receive = {
    case msg @ Message(text, _) => parse(msg)
    case data: ByteString => parse(Message(data.decodeString("US-ASCII"), unknown))
    case _ => log.info("Unexpected object to parse")
  }
  // $COVERAGE-ON$
}
