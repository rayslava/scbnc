package com.rayslava.scbnc.test

import org.specs2.specification._
import org.specs2.mutable._
import com.rayslava.scbnc
import com.rayslava.scbnc.types._

class bncTest extends Specification {
  "func (4)" should {
    "be 144" in {
      scbnc.func(4) must be_== (144)
    }
  }
}

class typeTest extends  Specification {
  "Message with text" should {
    "be printed as 'text'" in {
      val text = "text"
      val msg = new Message(text)
      msg.toString() must be_==(text)

      val dcmsg = new DCMessage(text)
      dcmsg.toString() must be_==("Disconnecting message '" + text + "'")
    }
  }
  "Link" should {
    "be printed as Link: 'link'" in {
      val url = "http://link.test"

      val l = Link(url)
      l.toString() must be_==(url)

      val il = ImageLink(url)
      il.toString() must be_==("Image from " + url)
    }
  }
}