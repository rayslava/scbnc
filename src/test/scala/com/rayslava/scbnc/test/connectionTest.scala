package com.rayslava.scbnc.test

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.Tcp._
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import akka.util.{ByteString, Timeout}
import com.rayslava.scbnc.irc.Client
import com.rayslava.scbnc.parser.Parser
import com.rayslava.scbnc.types.{Message, _}
import com.typesafe.config._
import org.specs2.mock.Mockito
import org.specs2.mutable.{Specification, _}
import org.specs2.time.NoTimeConversions

import scala.concurrent.duration._

class connectionRTest(_system: ActorSystem) extends TestKit(_system) with Mockito {

}

/* A tiny class that can be used as a Specs2 'context'. */
abstract class TKSpec2 extends TestKit(ActorSystem())
with After
with ImplicitSender {
  // make sure we shut down the actor system after all tests have run
  def after = system.shutdown()
}

class connectionResponses extends Specification with NoTimeConversions with Mockito {
  implicit val system = ActorSystem("TestActorSystem", ConfigFactory.load())
  implicit val timeout = Timeout(5 seconds)

  val testserver = "localhost"
  val testport = 49999

  val listener = TestActorRef(new Parser)

  val user = "user"
  val line = "test line"
  val response = "sent"
  val connectmsg = "connect"
  val crlf = "\r\n"
  val disconnectmsg = DCMessage(line)
  val remoteAddr = new InetSocketAddress(testserver, testport)
  val localAddr = new InetSocketAddress(testserver, testport)

  "A request to connect" should {
    "request a connection from the Tcp Manager" in new TKSpec2 {
      within(1 second) {
        val tcpProbe = TestProbe()
        val actorRef = TestActorRef(new Client(testserver, testport, listener) {
          override def tcp = tcpProbe.ref
        })

        actorRef ! connectmsg
        tcpProbe.expectMsgType[Connect] must be equalTo Connect(new InetSocketAddress(testserver, testport))
      }
    }
  }

  "Sending line to connection" should {
    "send data to tcp" in new TKSpec2 {
      val tcpProbe = TestProbe()
      val actorRef = TestActorRef(new Client(testserver, testport, listener) {
        override def tcp = tcpProbe.ref
      })
      tcpProbe.send(actorRef, Connected(remoteAddr, localAddr))
      tcpProbe.expectMsgType[Register] must be equalTo Register(actorRef)
      tcpProbe.send(actorRef, new Message(line))
      tcpProbe.expectMsgType[Write] must be equalTo Write(ByteString(line))
    }
    "even if line is in ByteString" in new TKSpec2 {
      val tcpProbe = TestProbe()
      val actorRef = TestActorRef(new Client(testserver, testport, listener) {
        override def tcp = tcpProbe.ref
      })
      tcpProbe.send(actorRef, Connected(remoteAddr, localAddr))
      tcpProbe.expectMsgType[Register] must be equalTo Register(actorRef)

      tcpProbe.send(actorRef, ByteString(line))
      tcpProbe.expectMsgType[Write] must be equalTo Write(ByteString(line))
    }
  }

  "Send a disconnect message" should {
    "produce a Close call to IO" in new TKSpec2 {
      val tcpProbe = TestProbe()
      val actorRef = TestActorRef(new Client(testserver, testport, listener) {
        override def tcp = tcpProbe.ref
      })

      tcpProbe.send(actorRef, Connected(remoteAddr, localAddr))
      tcpProbe.expectMsgType[Register] must be equalTo Register(actorRef)

      tcpProbe.send(actorRef, disconnectmsg)
      tcpProbe.expectMsgType[Write] must be equalTo Write(ByteString("QUIT :" + disconnectmsg.quitMessage + crlf))
    }
  }

  "When data is received it" should {
    "be passed to listener object" in new TKSpec2 {
      val probe = TestProbe()

      val actorRef = TestActorRef(new Client(testserver, testport, probe.ref))
      actorRef ! Connected(remoteAddr, localAddr)
      actorRef ! Received(ByteString(line))

      probe.expectMsg(ByteString(line))
    }
  }

  "Connection closing" should {
    "be hadled properly" in new TKSpec2 {
      val probe = TestProbe()

      val actorRef = TestActorRef(new Client(testserver, testport, listener) {
        override def connectionClosed = {
          probe.ref ! Closed
        }
      })
      actorRef ! Connected(remoteAddr, localAddr)
      actorRef ! Closed

      probe.expectMsg(Closed)
    }
  }

  "Logging in" should {
    "register an IRC connection" in new TKSpec2 {
      val tcpProbe = TestProbe()

      val actorRef = TestActorRef(new Client(testserver, testport, listener) {
        override def tcp = tcpProbe.ref
      })
      tcpProbe.send(actorRef, Connected(remoteAddr, localAddr))
      tcpProbe.expectMsgType[Register] must be equalTo Register(actorRef)
      tcpProbe.send(actorRef, LoginMessage(user))

      tcpProbe.expectMsg(Write(ByteString("PASS *" + crlf)))
      tcpProbe.expectMsg(Write(ByteString("NICK " + user + crlf)))
      tcpProbe.expectMsg(Write(ByteString("USER " + user + " 0 * :" + user + " " + user + crlf)))
    }
  }
}
