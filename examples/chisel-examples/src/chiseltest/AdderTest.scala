package chiselexamples
package adder

import chisel3.Data
import chisel3.experimental.BaseModule
import chiseltest._
import chiseltest.iotesters.PeekPokeTester
import org.scalatest.flatspec.AnyFlatSpec
import chisel3.reflect.DataMirror
import chisel3.stage.PrintFullStackTraceAnnotation
import chiselexamples.adder.modulewrapper.{AdderWrapperByInheritance, AdderWrapperByParameter}
import chiselexamples.expose.ExposeModule

// Class to add two n-bit numbers
class SwAdder(n: Int) {
  def apply(a: Int, b: Int, cin: Boolean): (Int, Boolean) = {

	val sum = a + b + (if (cin) 1 else 0)
	val mask = (1 << n) - 1
	// Return the sum and the carry out
	(sum & mask, ((sum >> n) & 1) == 1)
  }
}

// Tester for the Adder module
class AdderTester(c: Adder, stepsFor: Int = 1, printDebug: Boolean = false) extends PeekPokeTester(c) {

  if (printDebug) {
	System.out.println("=================================================================")
	System.out.println(f"Module: ${c.name}")
	System.out.println(f"${c.clock.name}%10s ${c.io.A.name}%10s ${c.io.B.name}%10s ${c.io.Cin.name}%10s ${c.io.Sum.name}%10s " +
	  f"${c.io.Cout.name}%10s : Ports")
	System.out.println(f"${DataMirror.directionOf(c.clock)}%10s ${DataMirror.directionOf(c.io.A)}%10s ${DataMirror.directionOf(c.io.B)}%10s " +
	  f"${DataMirror.directionOf(c.io.Cin)}%10s ${DataMirror.directionOf(c.io.Sum)}%10s " +
	  f"${DataMirror.directionOf(c.io.Cout)}%10s : Directions")
	System.out.println(f"${c.clock.typeName}%10s ${c.io.A.typeName}%10s ${c.io.B.typeName}%10s ${c.io.Cin.typeName}%10s " +
	  f"${c.io.Sum.typeName}%10s ${c.io.Cout.typeName}%10s : Types")
	System.out.println("=================================================================")
  }

  if (stepsFor < 1) {
	throw new Exception("stepsFor must be >= 1")
  }
  // Sum every possible combination: make exhaustive test
  for (a <- 1 until 1 << c.n by stepsFor)
	for (b <- 1 until 1 << c.n by stepsFor)
	  for (cin <- 0 until 2) {
		// Set inputs
		poke(c.io.A, a)
		poke(c.io.B, b)
		poke(c.io.Cin, cin)
		// Advance the clock
		step(1)

		// Compute the reference sum and carry
		val (sum, cout) = (new SwAdder(c.n))(a, b, cin == 1)

		// Check that the computed sum and carry are correct
		expect(c.io.Sum, sum)
		expect(c.io.Cout, cout)

		if (printDebug)
		  System.out.println(f"${t}%10s ${peek(c.io.A)}%10s ${peek(c.io.B)}%10s ${peek(c.io.Cin)}%10s " +
			f"${peek(c.io.Sum)}%10s ${peek(c.io.Cout)}%10s")
	  }
}


class AdderClassicTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "AdderClassicTest"

  it should "4 bit adder" in {
	test(new Adder(4, print = true))
	  .runPeekPoke(new AdderTester(_))
  }

  it should "8 bit adder" in {
	test(new Adder(8, print = false))
	  .runPeekPoke(new AdderTester(_))
  }

  it should "21 bit adder" in {
	test(new Adder(21, print = false))
	  // Write vcd annotations to inspect the waveforms
	  .withAnnotations(Seq(WriteVcdAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = 10_000))
  }

}

class AdderPrintfVerboseTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "AdderPrintfVerboseTest"

  it should "4 bit adder with printf" in {
	test(new Adder(4, print = true))
	  .runPeekPoke(new AdderTester(_, stepsFor = 2, printDebug = true))
  }

  it should "4 bit adder with verbose annotation" in {
	test(new Adder(4, print = true))
	  .withAnnotations(Seq(treadle2.VerboseAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = 2, printDebug = false))
  }

  it should "4 bit adder with printf and verbose annotation" in {
	test(new Adder(4, print = true))
	  .withAnnotations(Seq(treadle2.VerboseAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = 2, printDebug = true))
  }

}

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


}