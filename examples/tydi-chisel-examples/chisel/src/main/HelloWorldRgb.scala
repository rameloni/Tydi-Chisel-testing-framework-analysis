import nl.tudelft.tydi_chisel._ // Import TyDI library.
import chisel3._

object MyTypes {
  /** Bit(8) type, defined in main */
  def generated_0_16_OqHuFqKi_21 = UInt(8.W)

  assert(this.generated_0_16_OqHuFqKi_21.getWidth == 8)
}


/** Group element, defined in main. */
class Rgb extends Group {
  val b = MyTypes.generated_0_16_OqHuFqKi_21
  val g = MyTypes.generated_0_16_OqHuFqKi_21
  val r = MyTypes.generated_0_16_OqHuFqKi_21
}

/** Stream, defined in main. */
class Generated_0_101_bHWhCFjR_22 extends PhysicalStreamDetailed(e = new Rgb, n = 2, d = 1, c = 1, r = true, u = Null())

object Generated_0_101_bHWhCFjR_22 {
  def apply(): Generated_0_101_bHWhCFjR_22 = Wire(new Generated_0_101_bHWhCFjR_22())
}

/** Bit(8), defined in main. */
class Generated_0_16_OqHuFqKi_21 extends BitsEl(8.W)

/** Stream, defined in main. */
class Generated_0_86_q1AG1GZ7_18 extends PhysicalStreamDetailed(e = new Rgb, n = 1, d = 2, c = 1, r = false, u = new Rgb)

object Generated_0_86_q1AG1GZ7_18 {
  def apply(): Generated_0_86_q1AG1GZ7_18 = Wire(new Generated_0_86_q1AG1GZ7_18())
}

/**
 * Streamlet, defined in main.
 * RGB bypass streamlet documentation.
 */
class Rgb_bypass extends TydiModule {
  /** Stream of [[input]] with input direction. */
  val inputStream = Generated_0_86_q1AG1GZ7_18().flip
  /** IO of [[inputStream]] with input direction. */
  val input = inputStream.toPhysical

  /** Stream of [[input2]] with input direction. */
  val input2Stream = Generated_0_101_bHWhCFjR_22().flip
  /** IO of [[input2Stream]] with input direction. */
  val input2 = input2Stream.toPhysical

  /** Stream of [[output]] with output direction. */
  val outputStream = Generated_0_86_q1AG1GZ7_18()
  /** IO of [[outputStream]] with output direction. */
  val output = outputStream.toPhysical

  /** Stream of [[output2]] with output direction. */
  val output2Stream = Generated_0_101_bHWhCFjR_22()
  /** IO of [[output2Stream]] with output direction. */
  val output2 = output2Stream.toPhysical
}

/**
 * Implementation, defined in main.
 * RGB bypass implement documentation.
 */
class Helloworld_rgb extends Rgb_bypass {
  // Fixme: Remove the following line if this impl. contains logic. If it just interconnects, remove this comment.
  inputStream := DontCare
  // Fixme: Remove the following line if this impl. contains logic. If it just interconnects, remove this comment.
  input2Stream := DontCare
  // Fixme: Remove the following line if this impl. contains logic. If it just interconnects, remove this comment.
  outputStream := DontCare
  // Fixme: Remove the following line if this impl. contains logic. If it just interconnects, remove this comment.
  output2Stream := DontCare

  // Connections
  // Stream 1.
  output := input
  // Stream 2.
  output2 := input2
}
