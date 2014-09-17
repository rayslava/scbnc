package com.rayslava.scbnc.test

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.Tcp._
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe, TestActorRef}
import akka.util.{ByteString, Timeout}
import com.rayslava.scbnc.irc.Client
import com.rayslava.scbnc.parser.Parser
import com.rayslava.scbnc.types.Message
import com.rayslava.scbnc.types._
import com.typesafe.config._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import scala.concurrent.duration._
import scala.util.{Success}
import org.specs2.mutable._
import org.specs2.time.NoTimeConversions

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
    "return '" + response + "'" in new TKSpec2 {
      val actorRef = TestActorRef(new Client(testserver, testport, listener))
      actorRef ! Connected(remoteAddr, localAddr)

      val future = actorRef ? new Message(line)
      val Success(result: String) = future.value.get
      result must be(response)
    }
    "send data to tcp" in new TKSpec2 {
      val tcpProbe = TestProbe()
      val actorRef = TestActorRef(new Client(testserver, testport, listener) {
        override def tcp = tcpProbe.ref
      })
      actorRef ! Connected(remoteAddr, localAddr)
      actorRef ! new Message(line)
      tcpProbe.expectMsgType[Write] must be equalTo Write(ByteString(line))
    }
    "even if line is in ByteString" in new TKSpec2 {
      val tcpProbe = TestProbe()
      val actorRef = TestActorRef(new Client(testserver, testport, listener) {
        override def tcp = tcpProbe.ref
      })
      actorRef ! Connected(remoteAddr, localAddr)
      val future = actorRef ? ByteString(line)
      val Success(result: String) = future.value.get
      (tcpProbe.expectMsgType[Write] must be equalTo Write(ByteString(line))) && (result must be(response))
    }
  }

  "Send a disconnect message" should {
    "produce a Close call to IO" in new TKSpec2 {
      val tcpProbe = TestProbe()
      val actorRef = TestActorRef(new Client(testserver, testport, listener) {
        override def tcp = tcpProbe.ref
      })

      actorRef ! Connected(remoteAddr, localAddr)

      actorRef ! disconnectmsg
      tcpProbe.expectMsgType[Write] must be equalTo Write(ByteString("QUIT :" + disconnectmsg.quitMessage))
      tcpProbe.expectMsgType[Close.type] must be equalTo Close
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
      val probe = TestProbe()

      val actorRef = TestActorRef(new Client(testserver, testport, listener) {
        override def tcp = probe.ref
      })
      actorRef ! Connected(remoteAddr, localAddr)
      actorRef ! LoginMessage(user)

      probe.expectMsg(Write(ByteString("PASS *")))
      probe.expectMsg(Write(ByteString("NICK " + user)))
      probe.expectMsg(Write(ByteString("USER " + user + " 0 * " + user)))
    }
  }
}
