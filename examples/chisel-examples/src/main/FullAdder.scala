// See LICENSE.txt for license details.
/*
 * Code from: https://github.com/ucb-bar/chisel-tutorial/tree/release/src/main/scala/examples
 * This FullAdder is part of the Adder example.
 */
package chiselexamples
package adder

import chisel3._

class FullAdder extends Module {
  private val width = 1
  val io = IO(new Bundle {
    val a    = Input(UInt(width.W))
    val b    = Input(UInt(width.W))
    val cin  = Input(UInt(width.W))
    val sum  = Output(UInt(width.W))
    val cout = Output(UInt(width.W))
  })

  // Generate the sum
  val a_xor_b = io.a ^ io.b
  io.sum := a_xor_b ^ io.cin
  // Generate the carry
  val a_and_b   = io.a & io.b
  val b_and_cin = io.b & io.cin
  val a_and_cin = io.a & io.cin
  io.cout := a_and_b | b_and_cin | a_and_cin
}
