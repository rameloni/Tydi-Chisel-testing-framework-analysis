package tydiexamples.pipelinenestedstream

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.experimental.VecLiterals.AddVecLiteralConstructor
import chisel3.reflect.DataMirror
import chiseltest._
import nl.tudelft.tydi_chisel._
import nl.tudelft.tydi_chisel.Conversions._
import org.scalatest.flatspec.AnyFlatSpec

object FlippedWithNested {
  def apply[Tel <: TydiEl, Tus <: Data](stream: PhysicalStreamDetailed[Tel, Tus])
  : PhysicalStreamDetailed[Tel, Tus] = {
    // Check for nested streams
    var flippedStream = Flipped(stream)

    val streamElements = stream.getStreamElements

    val seq = flippedStream.data.map(el => {
      println(s"Flipping : $el")
      Flipped(el)
    })


    // flippedStream.data =
    //      Vec(flippedStream.data.length, stream.el).Lit(seq.zipWithIndex.map(v => (v._2, v._1)): _*)

    flippedStream
  }

}

class PipelineNestedStreamTestWrapper(module: => PipelineNestedStream,
                                      // val eIn: NumberGroupWithString,
                                      // val eOut: Stats
                                     ) extends TydiModule {
  private val mod: PipelineNestedStream = Module(module)

  val io = IO(new Bundle {
    val inStream = Flipped(mod.inStream.cloneType)
    val outStream = mod.outStream.cloneType
  })

  // Connects the input and output of the module
  mod.in := io.inStream
  io.outStream := mod.out

  mod.in_my_custom_string.stai := io.inStream.el.my_custom_string.stai
  mod.in_my_custom_string.user := io.inStream.el.my_custom_string.getUserConcat
  mod.in_my_custom_string.last := io.inStream.el.my_custom_string.last.asUInt
  mod.in_my_custom_string.strb := io.inStream.el.my_custom_string.strb
  mod.in_my_custom_string.endi := io.inStream.el.my_custom_string.endi
  mod.in_my_custom_string.data := io.inStream.el.my_custom_string.getDataConcat
  mod.in_my_custom_string.valid := io.inStream.el.my_custom_string.valid
  //  io.inStream.el.my_custom_string.ready := mod.in_my_custom_string.ready ???

  println(DataMirror.directionOf(io.inStream.el.my_custom_string.ready))

  io.outStream.el.my_custom_string := DontCare
  io.outStream.el.my_custom_string := mod.out_my_custom_string

}

// Tester for the Adder module
class PipelineNestedStreamTester(dut: PipelineNestedStreamTestWrapper) {

  private val char_t = new Char_t()
  private val date_t = new DateTimeGroup()
  private val numberGroup_t = new NumberGroup()
  private val numberGroupWithString_t = new NumberGroupWithString()
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
      val elInt = el.numberNested.value.litValue.toInt
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

  private def createStreamLit[Tel <: TydiEl, Tus <: Data, StreamEl <: PhysicalStreamDetailed[Tel, Tus]]
  (stream_t: StreamEl, elements: Seq[Tel], user: Tus, last: Boolean = false): StreamEl = {
    assert(elements.nonEmpty, "The elements list must not be empty")
    assert(elements.length == stream_t.data.length, "The elements list must have the same length as the stream data field: " +
      s"Input length: ${elements.length} != Stream length: ${stream_t.data.length}")

    val lasts = Vec(1, UInt(1.W)).Lit(Seq((0, last.asBool.asUInt)): _*)
    stream_t.Lit(
      _.data -> {
        val x = elements.zipWithIndex.map(v => (v._2, v._1))
        Vec(elements.length, elements.head.cloneType).Lit(x: _*)
      },
      _.last -> lasts,
      _.ready -> false.B,
      _.valid -> true.B,
      _.strb -> 1.U,
      //      _.user ->
    )
  }

  private def vecLitFromSeq(values: Seq[BigInt], times: Seq[BigInt], dates: Seq[DateTimeGroup]): Array[NumberGroupWithString] = {
    assert(values.length == times.length && values.length == dates.length)

    var result = Array.empty[NumberGroupWithString]
    for (i <- values.indices) {

      val numberGroup = numberGroup_t.Lit(
        _.value -> values(i).S, _.time -> times(i).U
      )
      // Check if numberGroupWithString_t.my_custom_string is a bundle


      val nestedString = numberGroupWithString_t.my_custom_string.cloneType

      val chars = char_t.Lit(_.value -> 'f'.U)

      val last = Vec(1, UInt(1.W)).Lit(Seq((0, true.B)): _*)
      //      println(s"Null.isInstanceOf[chisel3.Data]: ${Null().isInstanceOf[chisel3.Data]}")
      //      println(s"chars.isInstanceOf[nl.tudelft.tydi_chisel.TydiEl]: ${chars.isInstanceOf[nl.tudelft.tydi_chisel.TydiEl]}")
      //      println(s"nestedString.isInstanceOf[nl.tudelft.tydi_chisel.PhysicalStreamDetailed]: " +
      //        s"${nestedString.isInstanceOf[nl.tudelft.tydi_chisel.PhysicalStreamDetailed[TydiEl, Data]]}")
      val x = numberGroupWithString_t.Lit(
        _.date -> dates(i),
        _.numberNested -> numberGroup,

        //                _.my_custom_string -> nestedString.Lit(_.el -> chars, _.last -> last, _.strb -> 1.U, _.ready -> false.B, _.valid -> true.B),
        _.my_custom_string -> createStreamLit(new Char_stream(), Seq(chars), last = true, user = Null()),

      )
      //      x <> nestedString
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
} // end class PipelineNestedStreamTester

object PipelineNestedStreamTester {
  def apply(dut: => PipelineNestedStreamTestWrapper, printDebug: Boolean = false): Unit = {
    new PipelineNestedStreamTester(dut).apply()
  }
} // end object PipelineNestedStreamTester

class PipelineNestedStreamWaveformTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "PipelineNestedStreamWaveformTest"

  it should "dump Treadle VCD" in {
    test(new PipelineNestedStreamTestWrapper(new PipelineNestedStream))
      .withAnnotations(Seq(WriteVcdAnnotation, TreadleBackendAnnotation)) {
        PipelineNestedStreamTester(_)
      }
  }

  it should "dump Verilator VCD" in {
    test(new PipelineNestedStreamTestWrapper(new PipelineNestedStream))
      .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation)) {
        PipelineNestedStreamTester(_)
      }
  }

  it should "dump Icarus VCD" in {
    test(new PipelineNestedStreamTestWrapper(new PipelineNestedStream))
      .withAnnotations(Seq(WriteVcdAnnotation, IcarusBackendAnnotation)) {
        PipelineNestedStreamTester(_)
      }
  }

  it should "dump Verilator FST" in {
    test(new PipelineNestedStreamTestWrapper(new PipelineNestedStream))
      .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation)) {
        PipelineNestedStreamTester(_)
      }
  }

  it should "dump Icarus FST" in {
    test(new PipelineNestedStreamTestWrapper(new PipelineNestedStream))
      .withAnnotations(Seq(WriteFstAnnotation, IcarusBackendAnnotation)) {
        PipelineNestedStreamTester(_)
      }
  }

  it should "dump Icarus LXT" in {
    test(new PipelineNestedStreamTestWrapper(new PipelineNestedStream))
      .withAnnotations(Seq(new WriteLxtAnnotation, IcarusBackendAnnotation)) {
        PipelineNestedStreamTester(_)
      }
  }
} // end class AdderWaveformTest