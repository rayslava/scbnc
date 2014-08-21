package com.rayslava.scbnc

import akka.actor.Actor
import akka.event.Logging
import com.rayslava.scbnc.types.Link

/**
 * Actor to download objects
 */
class Downloader extends Actor {
  val log = Logging(context.system, this)

  def checkType(url: String) = {
    log.debug("Performing MIME type detection for " + url)
  }

  def download(url: String) = {
    checkType(url) match {
      case _ => log.debug("Can't detect type. Assuming binary data")
    }
    sender ! url //TODO: TEST ONLY! NOT IMPLEMENTED YET
  }

  def receive = {
    case msg @ Link(url) => download(url)
  }
}
