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
  val parser = listener

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
    tcp ! Close
  }

  /**
   * Send a text message to server
   * @param msg Message with text to send
   */
  def sendToServer(msg: ByteString) = {
    tcp ! Write(msg)

    sender() ! "sent"
  }

  /**
   * Perform finishing jobs after connection is closed from another side
   * @return
   */
  def connectionClosed = {
    log.info("Connection closed")
  }

  // $COVERAGE-OFF$
  def receive = {
    case "connect" => connect

    case c @ Connected(remote, local) => {
      context become {
        case msg @ Message(text) =>
          sendToServer(ByteString(msg.toString))
        case data: ByteString =>
          log.debug("Received data: " + data)
          sendToServer(data)
        case Received(data) =>
          log.debug("Received data: " + data)
          parser ! data
        case msg @ DCMessage(text) =>
          disconnect(msg.quitMessage)
        case CommandFailed(w: Write) =>
          log.warning("Write failed, OS buffer was full")
        case _: ConnectionClosed =>
          connectionClosed
          context stop self
      }
    }
  }
  // $COVERAGE-ON$
}