package chiselexamples
package adder


import chiseltest._
import chiseltest.iotesters.PeekPokeTester
import org.scalatest.flatspec.AnyFlatSpec
import chisel3.reflect.DataMirror
import chiselexamples.adder.modulewrapper.{AdderWrapperByInheritance, AdderWrapperByParameter}

// Class to add two n-bit numbers
class SwAdder(n: Int) {
  def apply(a: Int, b: Int, cin: Boolean): (Int, Boolean) = {

	val sum = a + b + (if (cin) 1 else 0)
	val mask = (1 << n) - 1
	// Return the sum and the carry out
	(sum & mask, ((sum >> n) & 1) == 1)
  }
} // end class SwAdder

// Tester for the Adder module
class AdderTester(dut: Adder, stepsFor: Int = 1, printDebug: Boolean = false) extends PeekPokeTester(dut) {

  if (printDebug) {
	System.out.println("=================================================================")
	System.out.println(f"Module: ${dut.name}")
	System.out.println(f"${dut.clock.name}%10s ${dut.io.A.name}%10s ${dut.io.B.name}%10s ${dut.io.Cin.name}%10s ${dut.io.Sum.name}%10s " +
	  f"${dut.io.Cout.name}%10s : Ports")
	System.out.println(f"${DataMirror.directionOf(dut.clock)}%10s ${DataMirror.directionOf(dut.io.A)}%10s ${DataMirror.directionOf(dut.io.B)}%10s " +
	  f"${DataMirror.directionOf(dut.io.Cin)}%10s ${DataMirror.directionOf(dut.io.Sum)}%10s " +
	  f"${DataMirror.directionOf(dut.io.Cout)}%10s : Directions")
	System.out.println(f"${dut.clock.typeName}%10s ${dut.io.A.typeName}%10s ${dut.io.B.typeName}%10s ${dut.io.Cin.typeName}%10s " +
	  f"${dut.io.Sum.typeName}%10s ${dut.io.Cout.typeName}%10s : Types")
	System.out.println("=================================================================")
  }

  if (stepsFor < 1) {
	throw new Exception("stepsFor must be >= 1")
  }
  // Sum every possible combination: make exhaustive test
  for (a <- 1 until 1 << dut.n by stepsFor)
	for (b <- 1 until 1 << dut.n by stepsFor)
	  for (cin <- 0 until 2) {
		// Set inputs
		poke(dut.io.A, a)
		poke(dut.io.B, b)
		poke(dut.io.Cin, cin)

		// Advance the clock
		step(1)

		val copyOfDUT = dut
		val peekedSum = peek(dut.io.Sum)
		val peekedCout = peek(dut.io.Cout)
		val peekedA = peek(dut.io.A)
		val peekedB = peek(dut.io.B)
		val peekedCin = peek(dut.io.Cin)

		// Compute the reference sum and carry
		val (sum, cout) = (new SwAdder(dut.n))(a, b, cin == 1)
		// Check that the computed sum and carry are correct
		expect(dut.io.Sum, sum)
		expect(dut.io.Cout, cout)

		if (printDebug)
		  System.out.println(f"${t}%10s ${peek(dut.io.A)}%10s ${peek(dut.io.B)}%10s ${peek(dut.io.Cin)}%10s " +
			f"${peek(dut.io.Sum)}%10s ${peek(dut.io.Cout)}%10s")
	  }
} // end class AdderTester


class AdderClassicTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "AdderClassicTest"

  it should "4 bit adder" in {
	test(new Adder(4, print = true))
	  .withAnnotations(Seq(WriteVcdAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = 8))
  }

  it should "8 bit adder" in {
	test(new Adder(8, print = false))
	  .withAnnotations(Seq(WriteVcdAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = 16))
  }

  it should "21 bit adder" in {
	test(new Adder(21, print = false))
	  // Write vcd annotations to inspect the waveforms
	  .withAnnotations(Seq(WriteVcdAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = 10_000))
  }

} // end class AdderClassicTest

class AdderPrintfVerboseTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "AdderPrintfVerboseTest"

  it should "4 bit adder with printf" in {
	test(new Adder(4, print = true))
	  .runPeekPoke(new AdderTester(_, stepsFor = 2, printDebug = true))
  }

  it should "4 bit adder with verbose annotation" in {
	test(new Adder(4, print = true))
	  .withAnnotations(Seq(treadle2.VerboseAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = 8, printDebug = false))
  }

  it should "4 bit adder with printf and verbose annotation" in {
	test(new Adder(4, print = true))
	  .withAnnotations(Seq(treadle2.VerboseAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = 8, printDebug = true))
  }

} // end class AdderPrintfVerboseTest

class AdderExposeTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "AdderExposeTest"

  it should "adder with exposed ports by parameter" in {
	test(new AdderWrapperByParameter(new Adder(4, print = true)))
	  .withChiselAnnotations(Seq()) { c =>
		c.exposed.io.A.poke(4)
		c.exposed.io.B.poke(1)
		c.exposed.io.Cin.poke(0)

		c.clock.step(1)
		System.out.println(s"Sum: ${c.exposed.io.Sum.peek().asBools.map(x => x.asUInt)}")
		System.out.println(s"Cout: ${c.exposed.io.Cout.peek()}")

		// FAs
		System.out.println(s"    a b i s c")
		for (fas <- c.exposed.FAs) {
		  System.out.println(s"FA: ${fas.a.peekInt} ${fas.b.peekInt} ${fas.cin.peekInt} ${fas.sum.peekInt} ${fas.cout.peekInt}")
		}
		System.out.println("-------------------------------")
		for (fas <- c.exposed.FAsMethod) {
		  System.out.println(s"FA: ${fas.a.peekInt} ${fas.b.peekInt} ${fas.cin.peekInt} ${fas.sum.peekInt} ${fas.cout.peekInt}")
		}
	  }
  }

  it should "adder with exposed ports by Inheritance" in {
	test(new AdderWrapperByInheritance(4, print = true))
	  .withChiselAnnotations(Seq()) { c =>
		c.io.A.poke(4)
		c.io.B.poke(1)
		c.io.Cin.poke(0)

		c.clock.step(1)
		System.out.println(s"Sum: ${c.io.Sum.peek().asBools.map(x => x.asUInt)}")
		System.out.println(s"Cout: ${c.io.Cout.peek()}")

		// FAs
		System.out.println(s"    a b i s c")
		for (fas <- c.exposed_FAs) {
		  System.out.println(s"FA: ${fas.a.peekInt} ${fas.b.peekInt} ${fas.cin.peekInt} ${fas.sum.peekInt} ${fas.cout.peekInt}")
		}
		System.out.println("-------------------------------")
		for (fas <- c.exposed_FAsMethod) {
		  System.out.println(s"FA: ${fas.a.peekInt} ${fas.b.peekInt} ${fas.cin.peekInt} ${fas.sum.peekInt} ${fas.cout.peekInt}")
		}
	  }
  }

} // end class AdderExposeTest

class AdderWaveformTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "AdderWaveformTest"

  val n = 5
  val print = false
  val stepsFor = 3

  it should "dump Treadle VCD" in {
	test(new Adder(n = n, print = print))
	  .withAnnotations(Seq(WriteVcdAnnotation, TreadleBackendAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = stepsFor))
  }

  it should "dump Verilator VCD" in {
	test(new Adder(n = n, print = print))
	  .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = stepsFor))
  }

  it should "dump Icarus VCD" in {
	test(new Adder(n = n, print = print))
	  .withAnnotations(Seq(WriteVcdAnnotation, IcarusBackendAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = stepsFor))
  }

  it should "dump Verilator FST" in {
	test(new Adder(n = n, print = print))
	  .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = stepsFor))
  }

  it should "dump Icarus FST" in {
	test(new Adder(n = n, print = print))
	  .withAnnotations(Seq(WriteFstAnnotation, IcarusBackendAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = stepsFor))
  }

  it should "dump Icarus LXT" in {
	test(new Adder(n = n, print = print))
	  .withAnnotations(Seq(new WriteLxtAnnotation, IcarusBackendAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = stepsFor))
  }
} // end class AdderWaveformTest