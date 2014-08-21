package com.rayslava.scbnc

/**
 * Module with common classes for the whole project
 */
package object types {
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
    * @param text --- link text, starting with "http://"
    */
  case class Link(text: String) {
    val Data = Integral

    override def toString = text
  }

}