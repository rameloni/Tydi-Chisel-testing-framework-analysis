// SPDX-License-Identifier: Apache-2.0
/*
 * Code from: https://github.com/ucb-bar/chisel-tutorial/tree/release/src/main/scala/examples
 *
 * This example implement an FSM.
 *
 * It aims to show how to use ChiselEnum to construct the states and switch & is to construct the FSM control logic.
 * It is used in order to inspect how the State (an ChiselEnum) can be inspected in the testing framework. Specifically,
 * how it differ from the code.
 *
 */
package chiselexamples
package showenum

import chisel3._
import circt.stage.ChiselStage
import chisel3.util._

/* ### How do I create a finite state machine?
 *
 * Use ChiselEnum to construct the states and switch & is to construct the FSM
 * control logic
 */

class DetectTwoOnes extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
    val inDebug = Output(Bool())
  })

  object State extends ChiselEnum {
    val sNone, sOne1, sTwo1s = Value
  }

  val state = RegInit(State.sNone)

  io.inDebug := io.in // This is needed to make hgdb able to stop at the breakpoint, breakpoints in the states won't work
  io.out := (state === State.sTwo1s)

  switch(state) {
    is(State.sNone) {
      when(io.in) {
        state := State.sOne1
      }
    }
    is(State.sOne1) {
      when(io.in) {
        state := State.sTwo1s
      }.otherwise {
        state := State.sNone
      }
    }
    is(State.sTwo1s) {
      when(!io.in) {
        state := State.sNone
      }
    }
  }
}

//class DetectTwoOnesTester extends CookbookTester(10) {
//
//  val dut = Module(new DetectTwoOnes)
//
//  // Inputs and expected results
//  val inputs: Vec[Bool] = VecInit(false.B, true.B, false.B, true.B, true.B, true.B, false.B, true.B, true.B, false.B)
//  val expected: Vec[Bool] =
//    VecInit(false.B, false.B, false.B, false.B, false.B, true.B, true.B, false.B, false.B, true.B)
//
//  dut.io.in := inputs(cycle)
//  assert(dut.io.out === expected(cycle))
//}
//
//class FSMSpec extends CookbookSpec {
//  "DetectTwoOnes" should "work" in {
//    assertTesterPasses { new DetectTwoOnesTester }
//  }
//}

object DetectTwoOnesVerilog extends App {
  Emit(
    "output/fsm",
    () => new DetectTwoOnes(),
    "DetectTwoOnes"
  ).verilog()
}

object DetectTwoOnesFIRRTL extends App {
  Emit(
    "output/fsm",
    () => new DetectTwoOnes(),
    "DetectTwoOnes"
  ).firrtl()
}

object DetectTwoOnesHGDB extends App {
  Emit(
    "output/fsm",
    () => new DetectTwoOnes(),
    "DetectTwoOnes"
  ).hgdbOutputs()
}