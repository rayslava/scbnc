package com.rayslava.scbnc.irc

import akka.actor.Actor
import akka.event.Logging
import com.rayslava.scbnc.types.{Message,DCMessage}

/**
 * IRC connection related procedures
 */
class Connection(server: String, port: Integer) extends Actor {
  val log = Logging(context.system, this)

  /**
   * Connects to server:port
   */
  def connect = {

  }

  /**
   * Disconnect from server and send quitMessage
   * @param quitMessage Line to use as IRC quit message
   */
  def disconnect(quitMessage: String) = {

  }

  /**
   * Send a text message to server
   * @param msg Message with text to send
   */
  def sendToServer(msg: Message) = {
    val line = msg.text

    sender ! "sent"
  }

  def receive = {
    case msg @ Message(text) => sendToServer(msg)
    case msg @ DCMessage(text) => disconnect(msg.quitMessage)
  }
}