package com.rayslava.scbnc.test

import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions
import org.specs2.mock.Mockito
import org.mockito.Matchers._
import akka.testkit.TestActorRef
import akka.actor.ActorSystem
import com.typesafe.config._
import scala.concurrent.duration._
import scala.util.Success
import akka.util.Timeout
import akka.pattern.ask

import com.rayslava.scbnc.parser._
import com.rayslava.scbnc.types._

class parsePlainText extends Specification with Mockito {
  implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())

  val actorRef = TestActorRef(new Parser)
  val actor = actorRef.underlyingActor

  "Parsing plain text line" should {
    "return this line" in {
      val obj = spy(actor)
      obj.parse(new Message("Just plaintext line"))
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
      obj.parse(new Message(text + link))
      there was one(obj).download(link)
    }
    "call download for each link if there are many" in {
      val obj = spy(actor)
      obj.parse(new Message(text + link + " " + link))
      there was two(obj).download(link)
    }
  }
}

class parseTextFromMessage extends  Specification with NoTimeConversions {
  implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())
  implicit val timeout = Timeout(5 seconds)

  val link = "http://site.com"
  val text = "Single link "

  val actorRef = TestActorRef(new Parser)
  val actor = actorRef.underlyingActor

  "Sending text to parser" should {
    "return this line" in {

      val future = actorRef ? new Message(text)

      // hypothetical message stimulating a '42' answer
      val Success(result: String) = future.value.get
      result must be (text)
    }
  }
}