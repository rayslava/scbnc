package bnc.test

import org.scalatest._
import com.rayslava.scbnc

class bncTest extends FlatSpec with Matchers {
  "test (4)" should "be 144" in {
    scbnc.test(4) should be (144)
  }
}
