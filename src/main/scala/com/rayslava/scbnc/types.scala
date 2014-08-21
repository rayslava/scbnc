package com.rayslava.scbnc

/**
 * Created by v.barinov on 8/21/14.
 */
package object types {
  /** One single text message
    *
    * @param text --- String with message
    */
  case class Message(text: String) {
    override def toString = text
  }

}
