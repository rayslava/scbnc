package com.rayslava

package object scbnc {

  /** Squares it
    * @param Arg lolint
    * @return Int
    */
  def test (Arg: Integer) = (Arg * 4) * (Arg * 3)

  def main(args: Array[String]) = {
    println("Hey there!")

    println( test(4) )
  }
}
