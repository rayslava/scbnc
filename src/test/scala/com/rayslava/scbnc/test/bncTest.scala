package com.rayslava.scbnc.test

import org.specs2.specification._
import org.specs2.mutable._
import com.rayslava.scbnc

class bncTest extends Specification {
  "func (4)" should {
    "be 144" in {
      scbnc.func(4) must be_== (144)
    }
  }
}
