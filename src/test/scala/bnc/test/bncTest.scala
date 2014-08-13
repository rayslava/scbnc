package bnc.test

import org.specs2.specification._
import org.specs2.mutable._
import com.rayslava.scbnc

class bncTest extends Specification {
  "test (4)" should {
    "be 144" in {
      scbnc.test(4) must be_== (144)
    }
  }
}
