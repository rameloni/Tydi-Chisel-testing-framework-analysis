package tydiexamples.pipelinenestedstream

import nl.tudelft.tydi_chisel._
import chisel3._
import chisel3.util.{Counter, is, switch}
import emit.Emit

object MyTypes {
  /** Bit(5) type, defined in pipelineNestedGroup_types */
  def UTC_t = SInt(5.W)

  assert(this.UTC_t.getWidth == 5)

  /** Bit(5) type, defined in pipelineNestedGroup_types */
  def Day_t = UInt(5.W)

  assert(this.Day_t.getWidth == 5)

  /** Bit(4) type, defined in pipelineNestedGroup_types */
  def Month_t = UInt(4.W)

  assert(this.Month_t.getWidth == 4)

  /** Bit(12) type, defined in pipelineNestedGroup_types */
  def Year_t = UInt(12.W)

  assert(this.Year_t.getWidth == 12)

  /** Bit(8) type, defined in pipelineNestedStream_types */
  def Char_t = UInt(8.W)

  assert(this.Char_t.getWidth == 8)

  /** Bit(64) type, defined in pipelineSimple_types */
  def UInt_64_t = UInt(64.W)

  assert(this.UInt_64_t.getWidth == 64)

  /** Bit(64) type, defined in pipelineSimple_types */
  def SInt_64_t = SInt(64.W)

  assert(this.SInt_64_t.getWidth == 64)
}


/** Group element, defined in pipelineNestedGroup_types.
 * A DateTimeGroup represents a specific date time. */
class DateTimeGroup extends Group {
  val day = MyTypes.Day_t
  val month = MyTypes.Month_t
  val utc = MyTypes.UTC_t
  val year = MyTypes.Year_t
}

/** Bit(5), defined in pipelineNestedGroup_types. */
class Generated_0_6_T6NcbOCR_35 extends BitsEl(5.W)

/** Bit(5), defined in pipelineNestedGroup_types. */
class Generated_0_6_lTtJH3Sm_31 extends BitsEl(5.W)

/** Bit(4), defined in pipelineNestedGroup_types. */
class Generated_0_6_s1qlLS0b_33 extends BitsEl(4.W)

/** Bit(12), defined in pipelineNestedGroup_types. */
class Generated_0_7_8dFmtPGe_37 extends BitsEl(12.W)

/** Group element, defined in pipelineNestedStream_types.
 * The Number Group from PipelineNestedGroup extended with a nested stream. */
class NumberGroupWithString extends Group {
  val date = new DateTimeGroup
  val my_custom_string = new Char_stream
  val numberNested = new NumberGroup
}

/** Group element, defined in pipelineNestedStream_types.
 * The Stats from PipelineSimple extended with a nested stream. */
class StatsWithString extends Group {
  val my_custom_string = new Char_stream
  val stats = new Stats
}

/** Stream, defined in pipelineNestedStream_types. */
class Char_stream extends PhysicalStreamDetailed(e = new Char_t, n = 3, d = 1, c = 1, r = false, u = Null())

object Char_stream {
  def apply(): Char_stream = Wire(new Char_stream())
}

/** Stream, defined in pipelineNestedStream_types. */
class Stats_stream extends PhysicalStreamDetailed(e = new StatsWithString, n = 1, d = 1, c = 1, r = false, u = Null())

object Stats_stream {
  def apply(): Stats_stream = Wire(new Stats_stream())
}

/** Stream, defined in pipelineNestedStream_types. */
class NumberGroup_stream extends PhysicalStreamDetailed(e = new NumberGroupWithString, n = 1, d = 1, c = 1, r = false, u = Null())

object NumberGroup_stream {
  def apply(): NumberGroup_stream = Wire(new NumberGroup_stream())
}

/** Bit(8), defined in pipelineNestedStream_types. */
class Char_t extends BitsEl(8.W)

/** Group element, defined in pipelineSimple_types.
 * A composite type (like a struct) that contains a value associated with a timestamp. */
class NumberGroup extends Group {
  val time = MyTypes.UInt_64_t
  val value = MyTypes.SInt_64_t
}

/** Group element, defined in pipelineSimple_types.
 * A composite type (like a struct) that represents the stats of the implemented algorithm. */
class Stats extends Group {
  val average = MyTypes.UInt_64_t
  val max = MyTypes.UInt_64_t
  val min = MyTypes.UInt_64_t
  val sum = MyTypes.UInt_64_t
}

/** Bit(64), defined in pipelineSimple_types. */
class Generated_0_7_ROwLvVN1_43 extends BitsEl(64.W)

/** Bit(64), defined in pipelineSimple_types. */
class Generated_0_7_xXsRK3Dv_45 extends BitsEl(64.W)

/**
 * Streamlet, defined in pipelineNestedStream.
 */
class NonNegativeFilter_interface extends TydiModule {
  /** Stream of [[in]] with input direction. */
  val inStream = NumberGroup_stream().flip
  /** IO of [[inStream]] with input direction. */
  val in = inStream.toPhysical
  /** IO of "my_custom_string" sub-stream of [[inStream]] with input direction. */
  val in_my_custom_string = inStream.el.my_custom_string.toPhysical

  /** Stream of [[out]] with output direction. */
  val outStream = NumberGroup_stream()
  /** IO of [[outStream]] with output direction. */
  val out = outStream.toPhysical
  /** IO of "my_custom_string" sub-stream of [[outStream]] with output direction. */
  val out_my_custom_string = outStream.el.my_custom_string.toPhysical
}

/**
 * Streamlet, defined in pipelineNestedStream.
 */
class PipelineNestedStream_interface extends TydiModule {
  /** Stream of [[in]] with input direction. */
  val inStream = NumberGroup_stream().flip
  /** IO of "my_custom_string" sub-stream of [[inStream]] with input direction. */
  val in_my_custom_string = inStream.el.my_custom_string.toPhysical
  /** IO of [[inStream]] with input direction. */
  val in = inStream.toPhysical

  /** Stream of [[out]] with output direction. */
  val outStream = Stats_stream()
  /** IO of [[outStream]] with output direction. */
  val out = outStream.toPhysical
  /** IO of "my_custom_string" sub-stream of [[outStream]] with output direction. */
  val out_my_custom_string = outStream.el.my_custom_string.toPhysical
}

/**
 * Streamlet, defined in pipelineNestedStream.
 */
class Reducer_interface extends TydiModule {
  /** Stream of [[in]] with input direction. */
  val inStream = NumberGroup_stream().flip
  /** IO of [[inStream]] with input direction. */
  val in = inStream.toPhysical
  /** IO of "my_custom_string" sub-stream of [[inStream]] with input direction. */
  val in_my_custom_string = inStream.el.my_custom_string.toPhysical

  /** Stream of [[out]] with output direction. */
  val outStream = Stats_stream()
  /** IO of [[outStream]] with output direction. */
  val out = outStream.toPhysical
  /** IO of "my_custom_string" sub-stream of [[outStream]] with output direction. */
  val out_my_custom_string = outStream.el.my_custom_string.toPhysical
}

/**
 * Implementation, defined in pipelineNestedStream.
 * This is a comment.
 */
class NonNegativeFilter extends NonNegativeFilter_interface {

  object StringReceiverState extends ChiselEnum {
    val IDLE, RECEIVING = Value
  }

  private val state = RegInit(StringReceiverState.IDLE)
  dontTouch(state)


  // Filtered only if the value is non-negative
  // inStream.valid tells us if the input is valid
  private val canPass: Bool = inStream.el.numberNested.value >= 0.S && inStream.el.date.utc >= 1.S &&
    inStream.valid && state === StringReceiverState.IDLE

  switch(state) {
    is(StringReceiverState.IDLE) {
      when(canPass && inStream.el.my_custom_string.valid
        //        && inStream.el.my_custom_string.last.last === 0.U(1.W)) {
        && inStream.el.my_custom_string.last.exists(x => x === 0.U)) {
        state := StringReceiverState.RECEIVING
      }
    }
    is(StringReceiverState.RECEIVING) {
      when(inStream.el.my_custom_string.valid
        //        && inStream.el.my_custom_string.last.last === 1.U(1.W)) {
        && inStream.el.my_custom_string.last.exists(x => x === 1.U)) {
        state := StringReceiverState.IDLE
      }
    }
  }


  // Connect inStream to outStream
  // This is equivalent of connecting al the fields of the two streams
  // Every future assignment will overwrite the previous one
  outStream := inStream

  // Accept new inputs when the a string is fully finished
  inStream.ready := inStream.el.my_custom_string.ready
  // Always ready to accept an input string
  inStream.el.my_custom_string.ready := true.B
  //  inStream.el.my_custom_string.ready := inStream.ready

  // if (canPass) then { it can go out } else { it is not forwarded }
  outStream.valid := canPass && outStream.ready
  outStream.strb := inStream.strb(0) && canPass

  outStream.el.my_custom_string.valid := inStream.el.my_custom_string.valid && (outStream.valid || state === StringReceiverState.RECEIVING)

}

/**
 * Implementation, defined in pipelineNestedStream.
 */
class PipelineNestedStream extends PipelineNestedStream_interface {
  // Fixme: Remove the following line if this impl. contains logic. If it just interconnects, remove this comment.
  //  inStream := DontCare
  // Fixme: Remove the following line if this impl. contains logic. If it just interconnects, remove this comment.
  outStream := DontCare


  // Modules
  val filter = Module(new NonNegativeFilter)
  val reducer = Module(new Reducer)

  // Connections
  filter.in := in
  filter.in_my_custom_string := in_my_custom_string
  inStream.ready := filter.in.ready
  inStream.el.my_custom_string.ready := filter.in_my_custom_string.ready

  reducer.in := filter.out
  reducer.in_my_custom_string := filter.out_my_custom_string
  out := reducer.out
  out_my_custom_string := reducer.out_my_custom_string
}

/**
 * Implementation, defined in pipelineNestedStream.
 */
class Reducer extends Reducer_interface {

  // Set the data width to 64 bits, such as the [[MyTypes]] types
  private val dataWidth = 64.W

  // Computing min and max
  private val maxVal: BigInt = BigInt(Long.MaxValue) // Must work with BigInt or we get an overflow
  private val cMin: UInt = RegInit(maxVal.U(dataWidth)) // REG for the min value
  private val cMax: UInt = RegInit(0.U(dataWidth)) // REG for the max value
  // Computing the sum
  private val cSum: UInt = RegInit(0.U(dataWidth)) // REG for the sum
  // Computing the avg
  private val nValidSamples: Counter = Counter(Int.MaxValue) // The number of samples received (valid && strb(0))
  private val nSamples: Counter = Counter(Int.MaxValue) // The number of samples received (valid)

  // Set the streams IN ready and OUT valid signals
  //inReady = if (maxVal > 0) { true.B } else { false.B}
  inStream.ready := (if (maxVal > 0) true.B else false.B)
  inStream.el.my_custom_string.ready := inStream.ready
  //inStream.ready := true.B

  // The output is valid only if we have received at least one sample
  outStream.valid := nSamples.value > 0.U

  // When a value is received
  when(inStream.valid) {
    // Get the value from th input stream
    val value = inStream.el.numberNested.value.asUInt
    nSamples.inc()

    // Check the strb line and perform the updates
    when(inStream.strb(0)) {
      cMin := cMin min value
      cMax := cMax max value
      cSum := cSum + value
      nValidSamples.inc()
    }
  }
  // Set the output stream
  outStream.el.stats.sum := cSum
  outStream.el.stats.min := cMin
  outStream.el.stats.max := cMax
  outStream.el.stats.average := Mux(nValidSamples.value > 0.U, cSum / nValidSamples.value, 0.U)

  // Set the output stream control signals:
  // they are fixed since the sum, min, max and average are updated every cycle
  // and they hold the same value if no change is performed
  outStream.strb := 1.U
  outStream.stai := 0.U
  outStream.endi := 1.U
  outStream.last := inStream.last

  // Nested stream
  outStream.el.my_custom_string := inStream.el.my_custom_string
  when(inStream.el.my_custom_string.valid) {
    inStream.el.my_custom_string.data.foreach(x => printf(cf"${x.value}%c"))
    //    when(inStream.el.my_custom_string.last.last === 1.U) {
    when(inStream.el.my_custom_string.last.exists(x => x === 1.U)) {
      printf("\n")

    }
  }
}


object PipelineNestedStreamVerilog extends App {
  Emit(
    "output/PipelineNestedStream",
    () => new PipelineNestedStream(),
    "PipelineNestedStream"
  ).verilog()
}

object PipelineNestedStreamFIRRTL extends App {
  Emit(
    "output/PipelineNestedStream",
    () => new PipelineNestedStream(),
    "PipelineNestedStream"
  ).firrtl()
}

object PipelineNestedStreamGenerateHGDB extends App {
  Emit(
    "output/PipelineNestedStream",
    () => new PipelineNestedStream(),
    "PipelineNestedStream"
  ).hgdbOutputs()
}