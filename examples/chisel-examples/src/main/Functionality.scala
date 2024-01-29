// See LICENSE.txt for license details.
/*
 * Code from: https://github.com/ucb-bar/chisel-tutorial/tree/release/src/main/scala/examples
 *
 * Modified by: rameloni
 * The changes are:
 *  - Compare the function with other ways to implement the same logic:
 *    using a val, a function, an object and a class
 *
 * This example implement a small logic that exploits scala functions.
 *
 * It aims to inspect how the function is represented in the testing frameworks.
 * Specifically, how it differ from the code. If the testing frameworks show that the logic is
 * "encoded" in the function is shown or its inner implementation is directly shown.
 *
 * So, the question is: how the function is represented in the testing framework?
 * What framework shows that the function is executed?
 * Will the following two lines be represented in the same way?
 *
 *      io.z_function := clb(io.x, io.y, io.x, io.y)
 *      io.z_boolean := (io.x & io.y) | (~io.x & io.y)
 *      io.z_val := clb_val
 *      io.z_object := CLB(io.x, io.y, io.x, io.y)
 *      io.z_class := clb_class(io.x, io.y, io.x, io.y)
 *
 * This example might be particularly interesting from the waveforms point of view.
 */

package chiselexamples
package function

import chisel3._
import circt.stage.ChiselStage

class Functionality extends Module {
  val io = IO(new Bundle {
    val x = Input(UInt(16.W))
    val y = Input(UInt(16.W))

    val z_boolean = Output(UInt(16.W))
    val z_function = Output(UInt(16.W))
    val z_val = Output(UInt(16.W))
    val z_object = Output(UInt(16.W))
    val z_class = Output(UInt(16.W))
  })

  // This is the equivalent boolean logic of the clb function
  io.z_boolean := (io.x & io.y) | (~io.x & io.y)

  // This is a port that uses the clb function
  def clb(a: UInt, b: UInt, c: UInt, d: UInt) =
    (a & b) | (~c & d)

  io.z_function := clb(io.x, io.y, io.x, io.y)


  // This is an equivalent logic of the clb function but it uses a val and not a function
  val clb_val = (io.x & io.y) | (~io.x & io.y)
  io.z_val := clb_val

  // This is an equivalent logic of the clb function but it uses an object and not a function
  object CLB {
    def apply(a: UInt, b: UInt, c: UInt, d: UInt) =
      (a & b) | (~c & d)
  }

  io.z_object := CLB(io.x, io.y, io.x, io.y)

  // This is an equivalent logic of the clb function but it uses a class and not a function
  class CLBClass {
    def apply(a: UInt, b: UInt, c: UInt, d: UInt) =
      (a & b) | (~c & d)
  }

  val clb_class = new CLBClass
  io.z_class := clb_class(io.x, io.y, io.x, io.y)
}

object FunctionalityVerilog extends App {
  Emit(
    "output/functionality",
    () => new Functionality(),
    "Functionality"
  ).verilog()
}

object FunctionalityFIRRTL extends App {
  Emit(
    "output/functionality",
    () => new Functionality(),
    "Functionality"
  ).firrtl()

}

object FunctionalityGenerateHGDB extends App {
  Emit(
    "output/functionality",
    () => new Functionality(),
    "Functionality"
  ).hgdbOutputs()
}