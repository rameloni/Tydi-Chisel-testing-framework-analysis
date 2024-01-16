
package chiselexamples
package showenum

import chisel3._
import chiseltest._
import chiseltest.iotesters.PeekPokeTester
import org.scalatest.flatspec.AnyFlatSpec

class DetectTwoOnesTester(c: DetectTwoOnesWrapper) extends PeekPokeTester(c) {

  // Inputs and expected results
  val inputs = Seq(0, 0, 1, 0, 1, 1, 0, 1, 1, 1)
  val expected = Seq(0, 0, 0, 0, 0, 1, 0, 0, 1, 1)

  // Reset
  poke(c.io.in, 0)
  step(1)

  for (i <- inputs.indices) {
	poke(c.io.in, inputs(i))
	step(1)
//	c.clock.getStepCount
	expect(c.io.out, expected(i))
//	System.out.println(s"In: ${inputs(i)}, out: ${expected(i)}")
  }

}

class DetectTwoOnesWrapper extends DetectTwoOnes {

  val stateExposed = IO(Output(State()))

  stateExposed := state

}

class FSMTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "FSM: DetectTwoOnes"

  it should "Check States VCD" in {
	test(new DetectTwoOnesWrapper())
	  .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation))
	  .runPeekPoke(new DetectTwoOnesTester(_))
  }

  it should "Check States FST" in {
	test(new DetectTwoOnesWrapper())
	  .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation))
	  .runPeekPoke(new DetectTwoOnesTester(_))
  }


  it should "Check States LXT" in {
	test(new DetectTwoOnesWrapper())
	  .withAnnotations(Seq(WriteLxtAnnotation.apply(), IcarusBackendAnnotation))
	  .runPeekPoke(new DetectTwoOnesTester(_))
  }

}