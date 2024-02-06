package tydiexamples.pipelinenestedgroup

import nl.tudelft.tydi_chisel._
import chisel3._
import chisel3.util.Counter

import emit.Emit

object MyTypes {
  /** Bit(4) type, defined in pipelineNestedGroup_types */
  def generated_0_6_2l35FDJE_31 = UInt(4.W)

  assert(this.generated_0_6_2l35FDJE_31.getWidth == 4)

  /** Bit(5) type, defined in pipelineNestedGroup_types */
  def generated_0_6_RR9CHdQ3_29 = UInt(5.W)

  assert(this.generated_0_6_RR9CHdQ3_29.getWidth == 5)

  /** Bit(5) type, defined in pipelineNestedGroup_types */
  def generated_0_6_msMzXQvQ_33 = SInt(5.W)

  assert(this.generated_0_6_msMzXQvQ_33.getWidth == 5)

  /** Bit(12) type, defined in pipelineNestedGroup_types */
  def generated_0_7_NG8Nnrvp_35 = UInt(12.W)

  assert(this.generated_0_7_NG8Nnrvp_35.getWidth == 12)

  /** Bit(64) type, defined in PipelineNested_types */
  def generated_0_7_CB2i4kG4_37 = UInt(64.W)

  assert(this.generated_0_7_CB2i4kG4_37.getWidth == 64)

  /** Bit(64) type, defined in PipelineNested_types */
  def generated_0_7_x2qPUxY1_39 = SInt(64.W)

  assert(this.generated_0_7_x2qPUxY1_39.getWidth == 64)
}


/** Group element, defined in pipelineNestedGroup_types.
 * A DateTimeGroup represents a specific date time. */
class DateTimeGroup extends Group {
  val day = MyTypes.generated_0_6_RR9CHdQ3_29
  val month = MyTypes.generated_0_6_2l35FDJE_31
  val utc = MyTypes.generated_0_6_msMzXQvQ_33
  val year = MyTypes.generated_0_7_NG8Nnrvp_35
}

/** Group element, defined in pipelineNestedGroup_types.
 * A NestedNumberGroup represents a number group with a nested date time group. */
class NestedNumberGroup extends Group {
  val date = new DateTimeGroup
  val numberGroup = new NumberGroup
  val time = MyTypes.generated_0_7_CB2i4kG4_37
  val value = MyTypes.generated_0_7_x2qPUxY1_39
}

/** Stream, defined in pipelineNestedGroup_types. */
class Generated_0_42_ggV1HHhy_26 extends PhysicalStreamDetailed(e = new NestedNumberGroup, n = 1, d = 1, c = 1, r = false, u = Null())

object Generated_0_42_ggV1HHhy_26 {
  def apply(): Generated_0_42_ggV1HHhy_26 = Wire(new Generated_0_42_ggV1HHhy_26())
}

/** Bit(4), defined in pipelineNestedGroup_types. */
class Generated_0_6_2l35FDJE_31 extends BitsEl(4.W)

/** Bit(5), defined in pipelineNestedGroup_types. */
class Generated_0_6_RR9CHdQ3_29 extends BitsEl(5.W)

/** Bit(5), defined in pipelineNestedGroup_types. */
class Generated_0_6_msMzXQvQ_33 extends BitsEl(5.W)

/** Bit(12), defined in pipelineNestedGroup_types. */
class Generated_0_7_NG8Nnrvp_35 extends BitsEl(12.W)

/** Group element, defined in PipelineNested_types.
 * A composite type (like a struct) that contains a value associated with a timestamp. */
class NumberGroup extends Group {
  val time = MyTypes.generated_0_7_CB2i4kG4_37
  val value = MyTypes.generated_0_7_x2qPUxY1_39
}

/** Group element, defined in PipelineNested_types.
 * A composite type (like a struct) that represents the stats of the implemented algorithm. */
class Stats extends Group {
  val average = MyTypes.generated_0_7_CB2i4kG4_37
  val max = MyTypes.generated_0_7_CB2i4kG4_37
  val min = MyTypes.generated_0_7_CB2i4kG4_37
  val sum = MyTypes.generated_0_7_CB2i4kG4_37
}

/** Stream, defined in PipelineNested_types. */
class Generated_0_30_Poy9kLLB_40 extends PhysicalStreamDetailed(e = new Stats, n = 1, d = 1, c = 1, r = false, u = Null())

object Generated_0_30_Poy9kLLB_40 {
  def apply(): Generated_0_30_Poy9kLLB_40 = Wire(new Generated_0_30_Poy9kLLB_40())
}

/** Bit(64), defined in PipelineNested_types. */
class Generated_0_7_CB2i4kG4_37 extends BitsEl(64.W)

/** Bit(64), defined in PipelineNested_types. */
class Generated_0_7_x2qPUxY1_39 extends BitsEl(64.W)

/**
 * Streamlet, defined in pipelineNestedGroup.
 */
class NonNegativeFilter_interface extends TydiModule {
  /** Stream of [[in]] with input direction. */
  val inStream = Generated_0_42_ggV1HHhy_26().flip
  /** IO of [[inStream]] with input direction. */
  val in = inStream.toPhysical

  /** Stream of [[out]] with output direction. */
  val outStream = Generated_0_42_ggV1HHhy_26()
  /** IO of [[outStream]] with output direction. */
  val out = outStream.toPhysical
}

/**
 * Streamlet, defined in pipelineNestedGroup.
 */
class PipelineNested_interface extends TydiModule {
  /** Stream of [[in]] with input direction. */
  val inStream = Generated_0_42_ggV1HHhy_26().flip
  /** IO of [[inStream]] with input direction. */
  val in = inStream.toPhysical

  /** Stream of [[out]] with output direction. */
  val outStream = Generated_0_30_Poy9kLLB_40()
  /** IO of [[outStream]] with output direction. */
  val out = outStream.toPhysical
}

/**
 * Streamlet, defined in pipelineNestedGroup.
 */
class Reducer_interface extends TydiModule {
  /** Stream of [[in]] with input direction. */
  val inStream = Generated_0_42_ggV1HHhy_26().flip
  /** IO of [[inStream]] with input direction. */
  val in = inStream.toPhysical

  /** Stream of [[out]] with output direction. Stats_stream is inherited from PipelineNested. */
  val outStream = Generated_0_30_Poy9kLLB_40()
  /** IO of [[outStream]] with output direction. */
  val out = outStream.toPhysical
}

/**
 * Implementation, defined in pipelineNestedGroup.
 */
class NonNegativeFilter extends NonNegativeFilter_interface {
  // Filtered only if the value is non-negative
  // inStream.valid tells us if the input is valid
  private val canPass: Bool = inStream.el.value >= 0.S && inStream.el.date.utc >= 1.S && inStream.valid

  // Connect inStream to outStream
  // This is equivalent of connecting al the fields of the two streams
  // Every future assignment will overwrite the previous one
  outStream := inStream

  // Always ready to accept input
  inStream.ready := true.B

  // if (canPass) then { it can go out } else { it is not forwarded }
  outStream.valid := canPass && outStream.ready
  outStream.strb := inStream.strb(0) && canPass
}

/**
 * Implementation, defined in pipelineNestedGroup.
 */
class PipelineNested extends PipelineNested_interface {
  // Fixme: Remove the following line if this impl. contains logic. If it just interconnects, remove this comment.
  inStream := DontCare
  // Fixme: Remove the following line if this impl. contains logic. If it just interconnects, remove this comment.
  outStream := DontCare

  // Modules
  val filter = Module(new NonNegativeFilter)
  val reducer = Module(new Reducer)

  // Connections
  filter.in := in
  reducer.in := filter.out
  out := reducer.out
}

/**
 * Implementation, defined in pipelineNestedGroup.
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
  //inStream.ready := true.B

  // The output is valid only if we have received at least one sample
  outStream.valid := nSamples.value > 0.U

  // When a value is received
  when(inStream.valid) {
    // Get the value from th input stream
    val value = inStream.el.value.asUInt
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
  outStream.el.sum := cSum
  outStream.el.min := cMin
  outStream.el.max := cMax
  outStream.el.average := Mux(nValidSamples.value > 0.U, cSum / nValidSamples.value, 0.U)

  // Set the output stream control signals:
  // they are fixed since the sum, min, max and average are updated every cycle
  // and they hold the same value if no change is performed
  outStream.strb := 1.U
  outStream.stai := 0.U
  outStream.endi := 1.U
  outStream.last(0) := inStream.last(0)
}

object PipelineNestedVerilog extends App {
  Emit(
    "output/PipelineNested",
    () => new PipelineNested(),
    "PipelineNested"
  ).verilog()
}

object PipelineNestedFIRRTL extends App {
  Emit(
    "output/PipelineNested",
    () => new PipelineNested(),
    "PipelineNested"
  ).firrtl()
}

object PipelineNestedGenerateHGDB extends App {
  Emit(
    "output/PipelineNested",
    () => new PipelineNested(),
    "PipelineNested"
  ).hgdbOutputs()
}
