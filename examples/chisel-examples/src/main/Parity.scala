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

// Check if the sequence of input 1's is even or odd
class Parity extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })

  val s_even :: s_odd :: Nil = Enum(2)
  val state = RegInit(s_even)

  // State logic
  when(io.in) {
    when(state === s_even) {
      state := s_odd
    }
      .otherwise {
        state := s_even
      }
  }

  // Output logic
  io.out := (state === s_odd)
}


object ParityVerilog extends App {
  Emit(
    "output/parity",
    () => new Parity(),
    "Parity"
  ).verilog()
}

object ParityFIRRTL extends App {
  Emit(
    "output/parity",
    () => new Parity(),
    "Parity"
  ).firrtl()

}

object ParityHGDB extends App {
  Emit(
    "output/parity",
    () => new Parity(),
    "Parity"
  ).hgdbOutputs()
}