package com.rayslava.scbnc.parser.test

import org.specs2.specification._
import org.specs2.mutable._
import org.specs2.mock.Mockito
import org.mockito.Matchers._
import akka.testkit.TestActorRef
import akka.actor.ActorSystem
import com.typesafe.config._

import com.rayslava.scbnc.parser._

class parsePlainText extends Specification with Mockito {
  implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())

  val actorRef = TestActorRef(new Parser)
  val actor = actorRef.underlyingActor

  "Parsing plain text line" should {
    "return this line" in {
      val obj = spy(actor)
      obj.parse(new Message("Just plaintext line")) must be ("Just plaintext line")
      there was no(obj).download(anyObject())
    }
  }
}

class parseTextWithLink extends Specification with Mockito {
  implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())

  val link = "http://site.com"
  val text = "Single link "

  val actorRef = TestActorRef(new Parser)
  val actor = actorRef.underlyingActor

  "Parsing text with links" should {
    "call download for " + link + " and return the same line" in {
      val obj = spy(actor)
      obj.parse(new Message(text + link)) must be_== (text + link)
      there was one(obj).download(link)
    }
    "call download for each link if there are many" in {
      val obj = spy(actor)
      obj.parse(new Message(text + link + " " + link)) must be_== (text + link + " " + link)
      there was two(obj).download(link)
    }
  }
}
