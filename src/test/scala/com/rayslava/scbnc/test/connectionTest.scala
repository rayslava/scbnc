package com.rayslava.scbnc.test

import java.net.InetSocketAddress

import akka.actor.{Props, ActorSystem}
import akka.io.Tcp.{Received, Connect, Close}
import akka.pattern.ask
import akka.testkit.{TestProbe, TestActorRef}
import akka.util.{ByteString, Timeout}
import com.rayslava.scbnc.irc.Client
import com.rayslava.scbnc.parser.Parser
import com.rayslava.scbnc.types._
import com.typesafe.config._
import org.specs2.execute.AsResult
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions

import scala.concurrent.duration._
import scala.util.{Success}

class connectionResponses extends Specification with NoTimeConversions with Mockito {
  implicit val system = ActorSystem("TestActorSystem", ConfigFactory.load())
  implicit val timeout = Timeout(5 seconds)

  val testserver = "localhost"
  val testport = 49999

  val listenProbe = TestProbe()
  val listener = TestActorRef(new Parser {
    override def parse(msg: Message) = listenProbe.ref
  })

  val tcpProbe = TestProbe()
  val actorRef = TestActorRef(new Client(testserver, testport, listener){
    override def tcp = tcpProbe.ref
  })
  val actor = actorRef.underlyingActor

  val line = "test line"
  val response = "sent"

  implicit def connectAsResult = new AsResult[Connect] {
    def asResult(r: =>Connect) = success
  }

  implicit def closeAsResult = new AsResult[Close.type] {
    def asResult(r: =>Close.type) = success
  }

  implicit def receivedAsResult = new AsResult[Received] {
    def asResult(r: =>Received) = success
  }

  "A request to connect" should {
    "request a connection from the Tcp Manager" in {
      actorRef ! "connect"
      tcpProbe.expectMsg(Connect(new InetSocketAddress(testserver, testport)))
    }
  }

  "Sending line to connection" should {
    "return 'send'" in {
      val future = actorRef ? new Message(line)
      val Success(result: String) = future.value.get
      result must be(response)
    }
  }

  "Send a disconnect message" should {
    "call disconnect function" in {
      actorRef ! new DCMessage(line)
      tcpProbe.expectMsg(Close)
    }
  }

  "Sending Recieved(Data)" should {
    "pass data to listener" in {
      actorRef ! new Received(ByteString(line))
      listenProbe.expectMsg(Received(ByteString(line)))
    }
  }

}