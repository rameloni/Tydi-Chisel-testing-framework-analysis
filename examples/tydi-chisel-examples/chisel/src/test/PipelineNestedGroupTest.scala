package tydiexamples.pipelinenestedgroup

import chisel3._
import chisel3.experimental.BundleLiterals.AddBundleLiteralConstructor
import chisel3.experimental.VecLiterals.AddVecLiteralConstructor
import chiseltest._
import nl.tudelft.tydi_chisel._
import nl.tudelft.tydi_chisel.Conversions._
import org.scalatest.flatspec.AnyFlatSpec


class PipelineNestedTestWrapper(module: => PipelineNested,
                                // val eIn: NestedNumberGroup,
                                // val eOut: Stats
                               ) extends TydiModule {
  private val mod: PipelineNested = Module(module)

  val io = IO(new Bundle {
    val inStream = Flipped(mod.inStream.cloneType)
    val outStream = mod.outStream.cloneType
  })

  // Connects the input and output of the module
  mod.in := io.inStream
  io.outStream := mod.out
}

// Tester for the Adder module
class PipelineNestedTester(dut: PipelineNestedTestWrapper) {

  private val date_t = new DateTimeGroup()
  private val numberGroup_t = new NumberGroup()
  private val nestedNumberGroup_t = new NestedNumberGroup()
  private val stats_t = new Stats()

  def apply(): Unit = {
    // Init the streams
    dut.io.inStream.initSource().setSourceClock(dut.clock)
    dut.io.outStream.initSink().setSinkClock(dut.clock)

    val inputs = vecLitFromSeq(
      Seq(100, 101, -102, 103, -104, 105, 0, 101),
      Seq(0, 1, 2, 3, 4, 5, 6, 7),
      vecDateFromSeq(
        days = Seq(1, 1, 2, 3, 4, 5, 6, 3),
        months = Seq(1, 1, 2, 3, 4, 5, 6, 2),
        years = Seq(2013, 2014, 2022, 2024, 2027, 5, 6, 1),
        utc = Seq(-1, -1, -2, 0, 1, 1, -1, 1))
    )
    var tot = 0
    var avg, max, min, sum = 0

    for (el <- inputs) {
      // Get the expected values
      val elInt = el.value.litValue.toInt
      val elUtc = el.date.utc.litValue.toInt
      if (elInt >= 0 && elUtc >= 1) {
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
//          dut.io.outStream.expectDequeueNow(expected)
        }
      )
    }
  }

  private def vecLitFromSeq(values: Seq[BigInt], times: Seq[BigInt], dates: Seq[DateTimeGroup]): Array[NestedNumberGroup] = {
    assert(values.length == times.length && values.length == dates.length)

    var result = Array.empty[NestedNumberGroup]
    for (i <- values.indices) {
      val numberGroup = numberGroup_t.Lit(
        _.value -> values(i).S, _.time -> times(i).U
      )

      val x = nestedNumberGroup_t.Lit(
        _.date -> dates(i),
        _.numberGroup -> numberGroup,
        _.value -> values(i).S,
        _.time -> times(i).U
      )
      result = result :+ x
    }
    result
  }

  private def vecDateFromSeq(days: Seq[BigInt], months: Seq[BigInt], utc: Seq[BigInt], years: Seq[BigInt]): Array[DateTimeGroup] = {

    assert(days.length == months.length && days.length == utc.length && years.length == days.length)

    var result = Array.empty[DateTimeGroup]
    for (i <- days.indices) {
      val x = date_t.Lit(
        _.day -> days(i).U,
        _.month -> months(i).U,
        _.utc -> utc(i).S,
        _.year -> years(i).U
      )
      result = result :+ x
    }
    result
  }
} // end class PipelineNestedTester

object PipelineNestedTester {
  def apply(dut: => PipelineNestedTestWrapper, printDebug: Boolean = false): Unit = {
    new PipelineNestedTester(dut).apply()
  }
} // end object PipelineNestedTester

class PipelineNestedWaveformTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "PipelineNestedWaveformTest"

  it should "dump Treadle VCD" in {
    test(new PipelineNestedTestWrapper(new PipelineNested))
      .withAnnotations(Seq(WriteVcdAnnotation, TreadleBackendAnnotation)) {
        PipelineNestedTester(_)
      }
  }

  it should "dump Verilator VCD" in {
    test(new PipelineNestedTestWrapper(new PipelineNested))
      .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation)) {
        PipelineNestedTester(_)
      }
  }

  it should "dump Icarus VCD" in {
    test(new PipelineNestedTestWrapper(new PipelineNested))
      .withAnnotations(Seq(WriteVcdAnnotation, IcarusBackendAnnotation)) {
        PipelineNestedTester(_)
      }
  }

  it should "dump Verilator FST" in {
    test(new PipelineNestedTestWrapper(new PipelineNested))
      .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation)) {
        PipelineNestedTester(_)
      }
  }

  it should "dump Icarus FST" in {
    test(new PipelineNestedTestWrapper(new PipelineNested))
      .withAnnotations(Seq(WriteFstAnnotation, IcarusBackendAnnotation)) {
        PipelineNestedTester(_)
      }
  }

  it should "dump Icarus LXT" in {
    test(new PipelineNestedTestWrapper(new PipelineNested))
      .withAnnotations(Seq(new WriteLxtAnnotation, IcarusBackendAnnotation)) {
        PipelineNestedTester(_)
      }
  }
} // end class AdderWaveformTest