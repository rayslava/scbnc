package com.rayslava.scbnc.test

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout
import com.rayslava.scbnc.Downloader
import com.rayslava.scbnc.types._
import com.typesafe.config._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions

import scala.concurrent.duration._
import scala.util.Success

class downloadCommon extends Specification with Mockito {
  implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())

  val link = "http://site.com"

  val actorRef = TestActorRef(new Downloader)
  val actor = actorRef.underlyingActor

  "Downloading url" should {
    "check MIME type" in {
      val obj = spy(actor)
      obj.download(link)
      there was one(obj).checkType(link)
    }
  }
}

  class downloadActor extends  Specification with NoTimeConversions {
    implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())
    implicit val timeout = Timeout(5 seconds)

    val link = "http://site.com"

    val actorRef = TestActorRef(new Downloader)
    val actor = actorRef.underlyingActor

    "Sending link to downloader" should {
      "return downloaded object" in {
        val future = actorRef ? new Link(link)
        val Success(result: String) = future.value.get
        result must be(link)
      }
    }
  }
