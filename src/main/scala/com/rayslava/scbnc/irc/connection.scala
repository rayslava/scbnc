package com.rayslava.scbnc.irc

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.io.{IO, Tcp}
import akka.util.ByteString
import com.rayslava.scbnc.types.{DCMessage, LoginMessage, Message}

/**
 * IRC connection client
 */
class Client(server: String, port: Integer, listener: ActorRef) extends Actor {
  import akka.io.Tcp.{CommandFailed, Connect, Connected, ConnectionClosed, Received, Register, Write}
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
   * Send a text message to server
   * @param msg Message with text to send
   */
  def sendToServer(msg: String, connection: ActorRef): Unit = {
    sendToServer(ByteString(msg), connection)
  }


  /**
   * Send a text message to server
   * @param msg Message with text to send
   */
  def sendToServer(msg: ByteString, connection: ActorRef) = {
    log.debug("Writing \"" + msg.decodeString("US-ASCII") + "\"")
    connection ! Write(msg)
  }

  /**
   * Perform finishing jobs after connection is closed from another side
   * @return
   */
  def connectionClosed = {
    log.info("Connection closed")
  }

  /**
   * Register a connection accordingly 1459
   *
   * @param nickname Bot nickname shown on channel
   * @param username Username for irc registration
   * @param connection Connection actor ref
   * @param password IRC connection password
   * @param mode IRC connection mode
   * PASS <password>
   * NICK <nickname>
   * USER <user> <mode> <unused> <realname>
   *
   */
  def registerConnection(nickname: String, username: String, connection: ActorRef, password: String = "*", mode: Integer = 0) = {
    val helloString = ByteString("PASS " + password + "\r\n")
    val nickString = ByteString("NICK " + nickname + "\r\n")
    val userString = ByteString("USER " + username + " " + mode.toString() + " * :" + username + " " + username + "\r\n")

    log.debug("Logging in as " + nickname)

    sendToServer(helloString, connection)
    sendToServer(nickString, connection)
    sendToServer(userString, connection)
  }

  // $COVERAGE-OFF$
  def receive = {
    case "connect" => connect

    case c @ Connected(remote, local) => {
      val connection = sender()
      log.debug("Connected to " + remote)
      connection ! Register(self)
      context become {
        case msg @ Message(text) =>
          sendToServer(ByteString(msg.toString), connection)
        case data: ByteString =>
          log.debug("Received " + data.length + " bytes of data")
          sendToServer(data, connection)
        case Received(data) =>
          log.debug("Received Received() with " + data.length + " bytes of payload")
          parser ! data
        case msg @ DCMessage(text) =>
          log.debug("Disconnecting from server with '" + msg.quitMessage + "'")
          sendToServer("QUIT :" + msg.quitMessage + "\r\n", connection)
        case CommandFailed(w: Write) =>
          log.warning("Write failed, OS buffer was full")
        case LoginMessage(nick: String) =>
          registerConnection(nick, nick, connection)
        case _: ConnectionClosed =>
          connectionClosed
          context stop self
      }
    }
  }
  // $COVERAGE-ON$
}