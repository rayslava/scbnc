package com.rayslava.scbnc.test

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout
import com.rayslava.scbnc.irc.Connection
import com.rayslava.scbnc.types._
import com.typesafe.config._
import org.mockito.Matchers._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions

import scala.concurrent.duration._
import scala.util.{Random, Success}

class connectionResponses extends Specification with NoTimeConversions with Mockito {
  implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())
  implicit val timeout = Timeout(5 seconds)

  val testserver = "localhost"
  val testport = 49999

  val actorRef = TestActorRef(new Connection(testserver, testport))
  val actor = actorRef.underlyingActor

  val line = "test line"
  val response = "sent"

  "Sending line to connection" should {
    "return 'send'" in {

      val future = actorRef ? new Message(line)
      val Success(result: String) = future.value.get
      result must be(response)
    }
  }

  "Send a disconnect message" should {
    "call disconnect function" in {
      val obj = spy(actor)
      obj.receive(new DCMessage(line))
      there was one(obj).disconnect(line)
    }
  }
}