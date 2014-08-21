package com.rayslava.scbnc

/**
 * Module with common classes for the whole project
 */
object types {
  /** One single text message
    *
    * @param text --- String with message
    */
  case class Message(text: String) {
    override def toString = text
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

  class ImageLink(url: String) extends Link(url) {
    override def toString = "Image from " + url
  }
}
