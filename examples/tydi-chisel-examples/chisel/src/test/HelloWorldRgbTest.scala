package tydiexamples.helloworld


import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import chisel3.experimental.BundleLiterals.AddBundleLiteralConstructor

import chisel3._

import nl.tudelft.tydi_chisel._
import nl.tudelft.tydi_chisel.Conversions._


class HelloWorldTestWrapper(module: => Helloworld_rgb,
                            val eIn: Rgb,
                            val eOut: Rgb) extends TydiModule {
  private val mod: Helloworld_rgb = Module(module)

  val io = IO(new Bundle {
    val inputStream = Flipped(mod.inputStream.cloneType)
    val outputStream = mod.outputStream.cloneType

    val input2Stream = Flipped(mod.input2Stream.cloneType)
    val output2Stream = mod.output2Stream.cloneType
  })

  // Connects the input and output of the module
  mod.io.input := io.inputStream
  io.outputStream := mod.io.output

  mod.io.input2 := io.input2Stream
  io.output2Stream := mod.io.output2

}

// Tester for the Adder module
class HelloWorldTester(dut: HelloWorldTestWrapper, stepsFor: Int = 1, printDebug: Boolean = false) {

  val rgb_t = new Rgb()

  def apply(): Unit = {
    // Init the streams
    dut.io.inputStream.initSource().setSourceClock(dut.clock)
    dut.io.outputStream.initSink().setSinkClock(dut.clock)
    dut.io.input2Stream.initSource().setSourceClock(dut.clock)
    dut.io.output2Stream.initSink().setSinkClock(dut.clock)

    for (r <- 1 until 256 by stepsFor)
      for (g <- 0 until 256 by stepsFor)
        for (b <- 0 until 256 by stepsFor) {
          val rgb = rgb_t.Lit(_.r -> 1.U, _.g -> g.U, _.b -> b.U)

          // Enqueue the input stream
          dut.io.inputStream.enqueue(rgb)

          if (dut.io.input2Stream.ready.peek().litToBoolean)
            // Send the same input in the second stream
            dut.io.input2Stream.enqueueElNow(rgb)

        }
  }
} // end class HelloWorldTester

object HelloWorldTester {
  def apply(dut: => HelloWorldTestWrapper, stepsFor: Int = 1, printDebug: Boolean = false): Unit = {
    new HelloWorldTester(dut, stepsFor, printDebug).apply()
  }
} // end object HelloWorldTester

class HelloWorldWaveformTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "HelloWorldWaveformTest"

  val stepsFor = 98

  it should "dump Treadle VCD" in {
    test(new HelloWorldTestWrapper(new Helloworld_rgb, new Rgb, new Rgb))
      .withAnnotations(Seq(WriteVcdAnnotation, TreadleBackendAnnotation)) {
        HelloWorldTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Verilator VCD" in {
    test(new HelloWorldTestWrapper(new Helloworld_rgb, new Rgb, new Rgb))
      .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation)) {
        HelloWorldTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Icarus VCD" in {
    test(new HelloWorldTestWrapper(new Helloworld_rgb, new Rgb, new Rgb))
      .withAnnotations(Seq(WriteVcdAnnotation, IcarusBackendAnnotation)) {
        HelloWorldTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Verilator FST" in {
    test(new HelloWorldTestWrapper(new Helloworld_rgb, new Rgb, new Rgb))
      .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation)) {
        HelloWorldTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Icarus FST" in {
    test(new HelloWorldTestWrapper(new Helloworld_rgb, new Rgb, new Rgb))
      .withAnnotations(Seq(WriteFstAnnotation, IcarusBackendAnnotation)) {
        HelloWorldTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Icarus LXT" in {
    test(new HelloWorldTestWrapper(new Helloworld_rgb, new Rgb, new Rgb))
      .withAnnotations(Seq(new WriteLxtAnnotation, IcarusBackendAnnotation)) {
        HelloWorldTester(_, stepsFor = stepsFor)
      }
  }
} // end class AdderWaveformTest