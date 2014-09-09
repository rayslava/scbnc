package com.rayslava.scbnc.irc

import akka.actor.Actor
import akka.event.Logging
import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

import com.rayslava.scbnc.types.{Message,DCMessage}

object Client {
  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[Client], remote, replies)
}

/**
 * IRC connection client
 */
class Client(server: String, port: Integer, listener: ActorRef) extends Actor {
  import Tcp.{Connect,CommandFailed,Connected,Register,Write,Received,Close,ConnectionClosed}
  import context.system

  val log = Logging(context.system, this)

  def tcp: ActorRef = IO(Tcp)

  /**
   * Connects to server:port
   */
  def connect = {
    val remote = new InetSocketAddress(server, port)
    tcp ! Connect(remote)
  }

  /**
   * Disconnect from server and send quitMessage
   * @param quitMessage Line to use as IRC quit message
   */
  def disconnect(quitMessage: String) = {
    log.debug("Disconnecting from server with '" + quitMessage + "'")
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
    case "connect" => connect

    case c @ Connected(remote, local) =>
      listener ! c
      val connection = sender()

      connection ! Register(self)
      context become {
        case data: ByteString =>
          log.debug("Received data: " + data)
          connection ! Write(data)
        case CommandFailed(w: Write) =>
          log.warning("Write failed, OS buffer was full")
        case Received(data) =>
          log.debug("Received data: " + data)
          listener ! Message(data.toString())
        case msg @ DCMessage(text) =>
          disconnect(msg.quitMessage)
          connection ! Close
        case _: ConnectionClosed =>
          log.debug("connection closed")
          context stop self
      }
  }
}