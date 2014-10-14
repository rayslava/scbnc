package com.rayslava.scbnc

/**
 * Module with common classes for the whole project
 */
object types {
  /** One single text message
    *
    * @param text String with message
    * @param recipient Name of message recipient
    */
  case class Message(text: String, recipient: String) {
    override def toString = text
  }

  /**
   * Service message which will say a goodbye to server and ask to close a connection
   * @param quitMessage A goodbye message
   */
  case class DCMessage(quitMessage: String) {
    override def toString = "Disconnecting message '" + quitMessage + "'"
  }

  /**
   * Service message to join a conference or channel
   * @param conference Conference/channel name
   * @param password Password to join (if any), null by default
   */
  case class JoinMessage(conference: String, password: String = "");

  /**
   * Service message to leave a conference
   * @param conference Conference/channel name
   */
  case class LeaveMessage(conference: String);

  /**
   * Message to log to server
   * @param nick Nickname for login
   */
  case class LoginMessage(nick: String) {
    override def toString = "Login message as " + nick
  }

  /** Http link object
    *
    * Initiate a download request during creation and saves a data ID in @param Data field
    *
    * @param url --- link text, starting with "http://"
    */
  case class Link(url: String) {
    override def toString = url
  }

  case class ImageLink(url: String) {
    override def toString = "Image from " + url
  }
}
