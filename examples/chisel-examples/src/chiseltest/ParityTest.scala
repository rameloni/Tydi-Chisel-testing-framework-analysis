
package chiselexamples
package showenum

import chisel3._
import chisel3.stage.PrintFullStackTraceAnnotation
import chiseltest._
import chiseltest.iotesters.PeekPokeTester
import org.scalatest.flatspec.AnyFlatSpec


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

}