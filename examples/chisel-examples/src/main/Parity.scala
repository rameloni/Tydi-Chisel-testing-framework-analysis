// See LICENSE.txt for license details.

/*
 * Code from: https://github.com/ucb-bar/chisel-tutorial/tree/release/src/main/scala/examples
 *
 * This example implement an FSM.
 *
 * It aims to show how to use Enum to construct the states and switch & is to construct the FSM control logic.
 * It is used in order to inspect how the State (an Enum) can be inspected in the testing framework. Specifically,
 * how it differ from the code.
 *
 * This example differs from the FSM example since it uses an Enum instead of a ChiselEnum.
 */
package chiselexamples
package showenum

import chisel3._
import chisel3.util.Enum
import circt.stage.ChiselStage

class Parity extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })
  val s_even :: s_odd :: Nil = Enum(2)
  val state = RegInit(s_even)
  when(io.in) {
    when(state === s_even) { state := s_odd }
      .otherwise { state := s_even }
  }
  io.out := (state === s_odd)
}



object ParityVerilog extends App {
  private val outputDir = "output/parity/verilog"

  // emit Verilog
  emitVerilog(
    new Parity(),
    Array("--target-dir", outputDir, "--split-verilog")
  )
}

object ParityFIRRTL extends App {
  private val outputDir = "output/parity/firrtl"

  val firrtl = ChiselStage.emitCHIRRTL(
    new Parity(),
  )

  // val thisDir = new java.io.File(".").getCanonicalPath
  val dir = new java.io.File(outputDir)
  if (!dir.exists()) {
    dir.mkdir()
  }

  val pw =
    new java.io.PrintWriter(new java.io.File(outputDir + "/Parity.fir"))
  pw.write(firrtl)
  pw.close()

}
