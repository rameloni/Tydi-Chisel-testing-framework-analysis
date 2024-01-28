package chiselexamples
package adder
package modulewrapper
import expose.ExposeBundle

import chisel3._
import chiseltest.experimental.expose

// Class that exposes the ports of the Adder module
class AdderWrapperByParameter(module: => Adder) extends Module {
  private val dut = Module(module)
  val n = dut.n

  val exposed = new Bundle {
	val io = IO(dut.io.cloneType)

	// Expose the inner ports of the FullAdders
	val FAs = IO(Vec(n, Output(dut.FAs(0).io.cloneType)))
	var FAsMethod = ExposeBundle.fromArray(dut.FAs.map(_.io), 1)

	val carry = expose(dut.carry)
	val sum = expose(dut.sum)
  }

  // Connect the input and output ports directly
  exposed.io <> dut.io

  exposed.FAs.zip(dut.FAs).foreach { case (exposed_io, fa) =>
	// Connect each element in the bundle
	exposed_io.getElements.zip(fa.io.getElements).foreach {
	  case (exposed_io, fa_io) =>
		exposed_io := expose(fa_io)
	}
  }
}

class AdderWrapperByInheritance(n: Int, print: Boolean = false) extends Adder(n, print) {

  val exposed_FAs = IO(Vec(n, Output(FAs(0).io.cloneType)))
  val exposed_FAsMethod = ExposeBundle.fromArray(FAs.map(_.io))
  val exposed_carry = expose(carry)
  val exposed_sum = expose(sum)

  exposed_FAs.zip(FAs).foreach { case (exposed_io, fa) =>
	exposed_io := fa.io
  }

}
