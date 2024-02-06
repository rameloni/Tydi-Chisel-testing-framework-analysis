package tydiexamples.pipelinesimple

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import chisel3.experimental.BundleLiterals.AddBundleLiteralConstructor
import chisel3._
import chisel3.experimental.VecLiterals.AddVecLiteralConstructor
import nl.tudelft.tydi_chisel._
import nl.tudelft.tydi_chisel.Conversions._


class PipelineSimpleTestWrapper(module: => PipelineSimple,
                                // val eIn: NumberGroup,
                                // val eOut: Stats
                               ) extends TydiModule {
  private val mod: PipelineSimple = Module(module)

  val io = IO(new Bundle {
    val inStream = Flipped(mod.inStream.cloneType)
    val outStream = mod.outStream.cloneType

  })

  // Connects the input and output of the module
  mod.in := io.inStream
  io.outStream := mod.out
}

// Tester for the Adder module
class PipelineSimpleTester(dut: PipelineSimpleTestWrapper, stepsFor: Int = 1, printDebug: Boolean = false) {

  val numberGroup_t = new NumberGroup()
  val stats_t = new Stats()

  def apply(): Unit = {
    // Init the streams
    dut.io.inStream.initSource().setSourceClock(dut.clock)
    dut.io.outStream.initSink().setSinkClock(dut.clock)

    val inputs = vecLitFromSeq(
      Seq(100, 101, -102, 103, -104, 105, 0),
      Seq(0, 1, 2, 3, 4, 5, 6))
    var tot = 0
    var avg, max, min, sum = 0

    for (el <- inputs) {
      // Get the expected values
      val elInt = el.value.litValue.toInt
      if (elInt >= 0) {
        tot += 1
        sum += elInt
        if (elInt > max) max = elInt
        if (tot <= 1 || elInt < min) min = elInt
        avg = sum / tot
      }
      val expected = stats_t.Lit(_.average -> avg.U, _.max -> max.U, _.min -> min.U, _.sum -> sum.U)
      println(s"tot: $tot, avg: $avg, max: $max, min: $min, sum: $sum")

      // Perform the test
      parallel(
        dut.io.inStream.enqueueElNow(el),
        {
          dut.clock.step() // Elements are available one clock cycle after the enqueue, because they are internally implemented as REGs
          dut.io.outStream.expectDequeueNow(expected)
        }
      )
    }
  }

  def vecLitFromSeq(s: Seq[BigInt], time: Seq[BigInt]): Vec[NumberGroup] = {
    val n = s.length
    val mapping = s.zip(time).map(c => numberGroup_t.Lit(_.value -> c._1.S, _.time -> c._2.U)).zipWithIndex.map(v => (v._2, v._1))
    Vec(n, numberGroup_t).Lit(mapping: _*)
  }

} // end class PipelineSimpleTester

object PipelineSimpleTester {
  def apply(dut: => PipelineSimpleTestWrapper, stepsFor: Int = 1, printDebug: Boolean = false): Unit = {
    new PipelineSimpleTester(dut, stepsFor, printDebug).apply()
  }
} // end object PipelineSimpleTester

class PipelineSimpleWaveformTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "PipelineSimpleWaveformTest"

  val stepsFor = 98

  it should "dump Treadle VCD" in {
    test(new PipelineSimpleTestWrapper(new PipelineSimple /*,  new NumberGroup, new Stats */))
      .withAnnotations(Seq(WriteVcdAnnotation, TreadleBackendAnnotation)) {
        PipelineSimpleTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Verilator VCD" in {
    test(new PipelineSimpleTestWrapper(new PipelineSimple /*,  new NumberGroup, new Stats */))
      .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation)) {
        PipelineSimpleTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Icarus VCD" in {
    test(new PipelineSimpleTestWrapper(new PipelineSimple /*,  new NumberGroup, new Stats */))
      .withAnnotations(Seq(WriteVcdAnnotation, IcarusBackendAnnotation)) {
        PipelineSimpleTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Verilator FST" in {
    test(new PipelineSimpleTestWrapper(new PipelineSimple /*,  new NumberGroup, new Stats */))
      .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation)) {
        PipelineSimpleTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Icarus FST" in {
    test(new PipelineSimpleTestWrapper(new PipelineSimple /*,  new NumberGroup, new Stats */))
      .withAnnotations(Seq(WriteFstAnnotation, IcarusBackendAnnotation)) {
        PipelineSimpleTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Icarus LXT" in {
    test(new PipelineSimpleTestWrapper(new PipelineSimple /*,  new NumberGroup, new Stats */))
      .withAnnotations(Seq(new WriteLxtAnnotation, IcarusBackendAnnotation)) {
        PipelineSimpleTester(_, stepsFor = stepsFor)
      }
  }
} // end class AdderWaveformTest