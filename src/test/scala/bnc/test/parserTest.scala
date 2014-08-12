package com.rayslava.scbnc.parser.test

import org.scalatest._
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

/* Firstly let's test just functions, without actor behavior */
class parserFuncTest extends FlatSpec with Matchers {
  implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())
  implicit val timeout = Timeout(5 seconds)
  val actorRef = TestActorRef(new Parser)
  val p = actorRef.underlyingActor

  "Single link http://site.com" should "respond with link" in {
    p.parse(new Message("Single link http://site.com")) should be ("http://site.com")
  }

  "Parsing line without links" should "return this line" in {
    p.parse(new Message("Just plaintext line")) should be ("Just plaintext line")
  }

}


class parserActorTest extends FlatSpec with Matchers {
  implicit val system = ActorSystem("MyActorSystem", ConfigFactory.load())
  implicit val timeout = Timeout(5 seconds)

  /* Firstly let's test just functions, without actor behavior */

  val actorRef = TestActorRef(new Parser)

  // hypothetical message stimulating a '42' answer
  val future = actorRef ? new Message("Line to parse")
  val Success(response: String) = future.value.get
  response should be("ok")
}
