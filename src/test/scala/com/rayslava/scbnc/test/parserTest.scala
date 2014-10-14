package com.rayslava.scbnc.test

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import com.rayslava.scbnc.parser._
import com.rayslava.scbnc.types._
import com.typesafe.config._
import org.mockito.Matchers._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class parsePlainText extends Specification with Mockito {
  implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())

  val actorRef = TestActorRef(new Parser)
  val actor = actorRef.underlyingActor

  val line = "Just plaintext line"
  val recipient = "recipient"

  "Parsing plain text line" should {
    "return this line" in {
      val obj = spy(actor)
      obj.parse(new Message(line, recipient))
      there was no(obj).download(anyObject())
    }
  }
}

class parseTextWithLink extends Specification with Mockito {
  implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())

  val link = "http://site.com"
  val text = "Single link "
  val recipient = "recipient"

  val actorRef = TestActorRef(new Parser)
  val actor = actorRef.underlyingActor

  "Parsing text with links" should {
    "call download for " + link + " and return the same line" in {
      val obj = spy(actor)
      obj.parse(new Message(text + link, recipient))
      there was one(obj).download(link)
    }
    "call download for each link if there are many" in {
      val obj = spy(actor)
      obj.parse(new Message(text + link + " " + link, recipient))
      there was two(obj).download(link)
    }
  }
}
