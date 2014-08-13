package com.rayslava.scbnc.parser.test

import org.specs2.mock.Mockito
import org.specs2.specification._
import org.specs2.matcher._
import org.specs2.mutable._
import org.mockito.Matchers._
import org.mockito.Mockito.doReturn
import org.mockito.Mock
import com.rayslava.scbnc.parser._
import com.typesafe.config._
import akka.testkit._
import scala.util.{Try, Success, Failure}
import akka.util.Timeout
import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.pattern.ask
import org.specs2.specification._

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
