// See LICENSE.txt for license details.
/*
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
import circt.stage.ChiselStage
// import chisel3.experimental.{ChiselAnnotation, annotate}
// import firrtl2.annotations.Annotation
// import firrtl2.passes.wiring.SinkAnnotation
// import firrtl2.annotations.{CircuitName, ComponentName, ModuleName, Named}
// import firrtl2.transforms.SortModules

//A n-bit adder with carry in and carry out
class Adder(val n: Int, print: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(n.W))
    val B = Input(UInt(n.W))
    val Cin = Input(UInt(1.W))
    val Sum = Output(UInt(n.W))
    val Cout = Output(UInt(1.W))
  })
  // val named =
  //   ComponentName(io.A.name, ModuleName(this.name, CircuitName("TopCircuitCustomName")))
  // val anno = Iterable(SinkAnnotation(named.asInstanceOf[Named], "CustomAnnotation"))
  // create an Array of FullAdders
  //  NOTE: Since we do all the wiring during elaboration and not at run-time,
  //  i.e., we don't need to dynamically index into the data structure at run-time,
  //  we use an Array instead of a Vec.
  val FAs = Array.fill(n)(Module(new FullAdder()).io)
  val carry = Wire(Vec(n + 1, UInt(1.W)))
  val sum = Wire(Vec(n, Bool()))

  // first carry is the top level carry in
  carry(0) := io.Cin

  // wire up the ports of the full adders
  for (i <- 0 until n) {
    FAs(i).a := io.A(i)
    FAs(i).b := io.B(i)
    FAs(i).cin := carry(i)
    carry(i + 1) := FAs(i).cout
    sum(i) := FAs(i).sum.asBool
  }
  io.Sum := sum.asUInt
  io.Cout := carry(n)

  // For debugging purposes
  if (print) {
    println(
      s"Adder: ${n} ${io.A.name} ${io.B.name} ${io.Cin.name} ${io.Sum.name} ${io.Cout.name}"
    )
  }
}

object AdderVerilog extends App {
  private val outputDir = "output/adder/verilog"
  val n = 4
  private val print = true

  // emit Verilog
  emitVerilog(
    new Adder(n, print),
    Array("--split-verilog", "--target-dir", outputDir)
  )
}

object AdderFIRRTL extends App {
  private val outputDir = "output/adder/firrtl"
  val n = 4
  private val print = true

  val firrtl = ChiselStage.emitCHIRRTL(
    new Adder(n, print),
//    Array("-td", outputDir)
  )

  // val thisDir = new java.io.File(".").getCanonicalPath
  val dir = new java.io.File(outputDir)
  if (!dir.exists()) {
    dir.mkdir()
  }

  val pw = new java.io.PrintWriter(new java.io.File(outputDir + "/Adder.fir"))
  pw.write(firrtl)
  pw.close()

}