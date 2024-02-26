package chiselexamples
package function

import chisel3._
import chiseltest._
import chiseltest.iotesters.PeekPokeTester
import org.scalatest.flatspec.AnyFlatSpec


// Test: x & y | ~x & y
class FunctionalityTester(dut:          Functionality,
                          var stepIncr: Int = 1,
                          print:        Boolean = false
                         ) extends PeekPokeTester(dut) {
  if (stepIncr < 1)
    stepIncr = 1

  val clb: (Int, Int, Int, Int) => Int =
    (a: Int, b: Int, c: Int, d: Int) => (a & b) | (~c & d)

  for (x <- 0 until (1 << 16) by stepIncr) {
    for (y <- 0 until (1 << 16) by stepIncr) {
      poke(dut.io.x, x)
      poke(dut.io.y, y)
      step(1)
      val ref = clb(x, y, x, y).U(16.W)
      expect(dut.io.z_boolean, ref)
      expect(dut.io.z_function, ref)
      expect(dut.io.z_val, ref)
      expect(dut.io.z_object, ref)
      expect(dut.io.z_class, ref)
    }
  }

  if (print) {
    System.out.println(s"Boolean  Output: \t${peek(dut.io.z_boolean)} \t${dut.io.z_boolean}")
    System.out.println(s"Function Output: \t${peek(dut.io.z_function)} \t${dut.io.z_function}")
    System.out.println(s"Val      Output: \t${peek(dut.io.z_val)} \t${dut.io.z_val}")
    System.out.println(s"Object   Output: \t${peek(dut.io.z_object)} \t${dut.io.z_object}")
    System.out.println(s"Class    Output: \t${peek(dut.io.z_class)} \t${dut.io.z_class}")
  }
}

class FunctionalityTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Functionality"

  it should "compare assignments" in {
    test(new Functionality())
      .withAnnotations(Seq(WriteVcdAnnotation))
      .runPeekPoke(new FunctionalityTester(_, stepIncr = 100, print = true))
  }
} // end FunctionalityTest


class FunctionalityWaveformTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "FunctionalityWaveformTest"

  val stepIncr = 100

  it should "dump Treadle VCD" in {
    test(new Functionality())
      .withAnnotations(Seq(WriteVcdAnnotation, TreadleBackendAnnotation))
      .runPeekPoke(new FunctionalityTester(_, stepIncr = stepIncr))
  }

  it should "dump Verilator VCD" in {
    test(new Functionality())
      .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation))
      .runPeekPoke(new FunctionalityTester(_, stepIncr = stepIncr))
  }

  it should "dump Icarus VCD" in {
    test(new Functionality())
      .withAnnotations(Seq(WriteVcdAnnotation, IcarusBackendAnnotation))
      .runPeekPoke(new FunctionalityTester(_, stepIncr = stepIncr))
  }

  it should "dump Verilator FST" in {
    test(new Functionality())
      .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation))
      .runPeekPoke(new FunctionalityTester(_, stepIncr = stepIncr))
  }

  it should "dump Icarus FST" in {
    test(new Functionality())
      .withAnnotations(Seq(WriteFstAnnotation, IcarusBackendAnnotation))
      .runPeekPoke(new FunctionalityTester(_, stepIncr = stepIncr))
  }

  it should "dump Icarus LXT" in {
    test(new Functionality())
      .withAnnotations(Seq(new WriteLxtAnnotation, IcarusBackendAnnotation))
      .runPeekPoke(new FunctionalityTester(_, stepIncr = stepIncr))
  }
} // end of class FunctionalityWaveformTest