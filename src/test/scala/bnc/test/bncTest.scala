package bnc.test

import org.scalatest.FlatSpec
import com.rayslava.scbnc._

class bncTest extends FlatSpec {
  "test (4)" should "be 144" in {
    assert (test(4) == 144)
  }
}
