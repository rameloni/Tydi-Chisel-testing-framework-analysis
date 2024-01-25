
package chiselexamples
package showenum

import chisel3._
import chisel3.stage.PrintFullStackTraceAnnotation
import chiseltest._
import chiseltest.iotesters.PeekPokeTester
import org.scalatest.flatspec.AnyFlatSpec

class ParityTester(c: Parity) extends PeekPokeTester(c) {

  val inputs = Seq(0, 1, 1, 0, 1, 1, 0, 1, 1, 1)
  //		val expected = Seq(0, 0, 0, 0, 0, 1, 0, 0, 1, 0)
  for (i <- inputs) {
	poke(c.io.in, i.U)
	step(1)
	System.out.println(s"======> In: ${inputs(i)}, out: ${peek(c.io.out)}")
  }

}

class ParityWrapper extends Parity {

  val stateExposed = IO(Output(UInt(2.W)))

  stateExposed := state

}


class ParityTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Parity: with Enum (list of UInts)"

  it should "" in {
	test(new ParityWrapper())
	  .withAnnotations(Seq(WriteVcdAnnotation,
		treadle2.VerboseAnnotation)) {
		dut =>
		  val inputs = Seq(0, 1, 1, 0, 1, 1, 0, 1, 1, 1)
		  //		val expected = Seq(0, 0, 0, 0, 0, 1, 0, 0, 1, 0)
		  for (i <- 0 until 1) {
			dut.io.in.poke(inputs(i).U)
			dut.clock.step(1)
			println(s"======> In: ${inputs(i)}, out: ${dut.io.out.peek()}, \tstate: ${dut.stateExposed.peek()}")
		  }
	  }

  }
} // end of class FSMTest


class ParityWaveformTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "ParityWaveformTest"

  it should "dump Treadle VCD" in {
	test(new Parity())
	  .withAnnotations(Seq(WriteVcdAnnotation, TreadleBackendAnnotation))
	  .runPeekPoke(new ParityTester(_))
  }

  it should "dump Verilator VCD" in {
	test(new Parity())
	  .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation))
	  .runPeekPoke(new ParityTester(_))
  }

  it should "dump Icarus VCD" in {
	test(new Parity())
	  .withAnnotations(Seq(WriteVcdAnnotation, IcarusBackendAnnotation))
	  .runPeekPoke(new ParityTester(_))
  }

  it should "dump Verilator FST" in {
	test(new Parity())
	  .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation))
	  .runPeekPoke(new ParityTester(_))
  }

  it should "dump Icarus FST" in {
	test(new Parity())
	  .withAnnotations(Seq(WriteFstAnnotation, IcarusBackendAnnotation))
	  .runPeekPoke(new ParityTester(_))
  }

  it should "dump Icarus LXT" in {
	test(new Parity())
	  .withAnnotations(Seq(new WriteLxtAnnotation, IcarusBackendAnnotation))
	  .runPeekPoke(new ParityTester(_))
  }
} // end of class ParityWaveformTest