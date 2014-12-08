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
  val msgRegex = """^:([^\s]+)\s(\w+)\s([^\s]+)\s(:[^\s]+)?\r\n""".r

  /**
   * Parses a text message extracting all the interesting data from inside
   * @param msg Message to parse
   */
  def parse(msg: Message): Unit = {
    val linkRegex = """(http://[^\s]+)(\s|$)""".r
    val text = msg.text
    log.debug("Parsing " + text)

    if (msgRegex.findFirstMatchIn(text).nonEmpty) {
      /* IRC message */

      val matched = msgRegex.findFirstMatchIn(text)
      matched match {
         case Some(m) =>
           log.debug("Catched an IRC message. From: " +
            m.group(1) + " Command: " + m.group(2) + " To: " + m.group(3) + " With text: '" + m.group(4) + "'")
         case None =>
           log.error("Shouldn't get here")
      }
    }

    linkRegex findAllIn text foreach {
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
