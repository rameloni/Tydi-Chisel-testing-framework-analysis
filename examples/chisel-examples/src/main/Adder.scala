// See LICENSE.txt for license details.
/**
 * Code from: https://github.com/ucb-bar/chisel-tutorial/tree/release/src/main/scala/examples
 *
 * Modified by: rameloni
 * The changes are:
 *  - Add a print parameter to the Adder class
 *
 * This example implement an n-bit adder.
 * It aims to show how an array of components that leads to a very simple and concise implementation
 * in chisel, also leads to a more complex implementation in verilog and in waveforms viewers.
 * So it shoes how the abstraction level of is lost when debugging with current waveform viewers.
 *
 * Moreover, it shows how chiseltest supports printf debugging.
 */
package chiselexamples
package adder

import chisel3._
import chisel3.util.random.FibonacciLFSR
import circt.stage.ChiselStage

// import chisel3.experimental.{ChiselAnnotation, annotate}
// import firrtl2.annotations.Annotation
// import firrtl2.passes.wiring.SinkAnnotation
// import firrtl2.annotations.{CircuitName, ComponentName, ModuleName, Named}
// import firrtl2.transforms.SortModules

// A n-bit adder with carry in and carry out
class Adder(val n: Int, val print: Boolean = false) extends Module {
  // IO interface
  val io = IO(new Bundle {
    val A = Input(UInt(n.W))
    val B = Input(UInt(n.W))
    val Cin = Input(UInt(1.W))

    val Sum = Output(UInt(n.W))
    val Cout = Output(Bool())
  })

  val io_A = Wire(UInt(n.W))
  io_A := FibonacciLFSR.maxPeriod(n).asUInt
  dontTouch(io_A)

  // val named =
  //   ComponentName(io.A.name, ModuleName(this.name, CircuitName("TopCircuitCustomName")))
  // val anno = Iterable(SinkAnnotation(named.asInstanceOf[Named], "CustomAnnotation"))
  // create an Array of FullAdders
  //  NOTE: Since we do all the wiring during elaboration and not at run-time,
  //  i.e., we don't need to dynamically index into the data structure at run-time,
  //  we use an Array instead of a Vec.
  // Internal logic
  val FAs = Array.fill(n)(Module(new FullAdder()))
  val FullAdder = Wire(UInt(1.W))
  dontTouch(FullAdder)

  val FullAdder_5 = Module(new FullAdder())
  val carry = Wire(Vec(n + 1, UInt(1.W)))
  val sum = Wire(Vec(n, Bool()))
  val sum_2 = Wire(Vec(n, UInt(1.W)))
  dontTouch(sum_2)

  FullAdder := 1.U
  FullAdder_5.io.a := 1.U
  FullAdder_5.io.b := 3.U
  FullAdder_5.io.cin := io.Cout && sum(1).asBool

  // first carry is the top level carry in
  carry(0) := io.Cin

  // wire up the ports of the full adders
  for (i <- 0 until n) {
    FAs(i).io.a := io.A(i)
    FAs(i).io.b := io.B(i)
    FAs(i).io.cin := carry(i)
    carry(i + 1) := FAs(i).io.cout
    sum(i) := FAs(i).io.sum.asBool
    sum_2(i) := sum(i)
  }

  io.Sum := sum.asUInt
  io.Cout := carry(n)

  // For debugging purposes
  if (print) {
    System.out.println(
      s"Adder: ${n} ${io.A.name} ${io.B.name} ${io.Cin.name} ${io.Sum.name} ${io.Cout.name}"
    )
  }
}


object AdderVerilog extends App {
  Emit(
    "output/adder",
    () => new Adder(5, true),
    "Adder"
  ).verilog()
}

object AdderFIRRTL extends App {
  Emit(
    "output/adder",
    () => new Adder(5, true),
    "Adder"
  ).firrtl()
}

object AdderGenerateHGDB extends App {
  Emit(
    "output/adder",
    () => new Adder(5, true),
    "Adder"
  ).hgdbOutputs()
}
