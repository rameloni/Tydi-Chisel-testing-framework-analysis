package tydiexamples.PixelConverter

import chisel3._
import chisel3.experimental.BundleLiterals.AddBundleLiteralConstructor

import nl.tudelft.tydi_chisel._

import emit.Emit

object MyTypes {
  def generated_0_16_a1Z2hVka_23 = UInt(8.W)

  assert(this.generated_0_16_a1Z2hVka_23.getWidth == 8)

  def value_8 = UInt(8.W)

  assert(this.value_8.getWidth == 8)

  def value_23 = UInt(23.W)

  assert(this.value_23.getWidth == 23)

  def sign = UInt(1.W)

  assert(this.sign.getWidth == 1)
}


/** */
class Generated_0_16_a1Z2hVka_23 extends BitsEl(8.W)

/** */
class Value(width: Int) extends BitsEl(width.W)

/** */
class Sign extends BitsEl(1.W)

/** Group element, defined in main.
 * A Pixel with a position (x, y) and a color (rg) documentation. */
class Pixel extends Group {
  val color = new Instance_Color_Identifiercolor_depth_1
  val pos = new Pos
}

/** Group element, defined in main.
 * A position with an x and y coordinated. */
class Pos extends Group {
  val x = new Instance_Float_Int23Int8_3
  val y = new Instance_Float_Int23Int8_6
}

/** Stream, defined in main. */
class Generated_0_81_eEjRSkb3_20 extends PhysicalStreamDetailed(e = new Pixel, n = 1, d = 2, c = 1, r = false, u = Null())

object Generated_0_81_eEjRSkb3_20 {
  def apply(): Generated_0_81_eEjRSkb3_20 = Wire(new Generated_0_81_eEjRSkb3_20())
}

/** Union element, defined in main. */
class Instance_Color_Identifiercolor_depth_1 extends Union(2) {
  val gray = MyTypes.generated_0_16_a1Z2hVka_23
  val rgb = new Instance_Rgb_Identifiercolor_depth_2
}

/** Group element, defined in main.
 * This is a custom float representation. */
class Instance_Float_Int23Int8_3 extends Group {
  val exponent = new Instance_UInt_Identifiern_exponent_4
  val mantissa = new Instance_UInt_Identifiern_mantissa_5
  val sign = MyTypes.sign
}

/** Group element, defined in main.
 * This is a custom float representation. */
class Instance_Float_Int23Int8_6 extends Group {
  val exponent = new Instance_UInt_Identifiern_exponent_7
  val mantissa = new Instance_UInt_Identifiern_mantissa_8
  val sign = MyTypes.sign
}

/** Group element, defined in main. */
class Instance_Rgb_Identifiercolor_depth_2 extends Group {
  val b = MyTypes.generated_0_16_a1Z2hVka_23
  val g = MyTypes.generated_0_16_a1Z2hVka_23
  val r = MyTypes.generated_0_16_a1Z2hVka_23
}

/** Group element, defined in main. */
class Instance_UInt_Identifiern_exponent_4 extends Group {
  val value = MyTypes.value_23
}

/** Group element, defined in main. */
class Instance_UInt_Identifiern_exponent_7 extends Group {
  val value = MyTypes.value_23
}

/** Group element, defined in main. */
class Instance_UInt_Identifiern_mantissa_5 extends Group {
  val value = MyTypes.value_8
}

/** Group element, defined in main. */
class Instance_UInt_Identifiern_mantissa_8 extends Group {
  val value = MyTypes.value_8
}

/**
 * Streamlet, defined in main.
 * RGB bypass streamlet documentation.
 */
class Pixel_converter_interface extends TydiModule {
  /** Stream of [[io.input]] with input direction. */
  val inputStream = Generated_0_81_eEjRSkb3_20().flip

  /** Stream of [[io.output]] with output direction. */
  val outputStream = Generated_0_81_eEjRSkb3_20()

  // Group of Physical IOs
  val io = new Bundle {
    /** IO of [[inputStream]] with input direction. */
    val input = inputStream.toPhysical
    /** IO of [[outputStream]] with output direction. */
    val output = outputStream.toPhysical
  }

}

/**
 * Implementation, defined in main.
 * RGB bypass implement documentation.
 */
class Pixel_converter extends Pixel_converter_interface {
  def rgb2gray(r: UInt, g: UInt, b: UInt): UInt = {
    // 0.299 * R + 0.587 * G + 0.114 * B
    // 0.25 * R + 0.5 * G + 0.25 * B
    ((r + (g << 1).asUInt + b) >> 2).asUInt

  }


  def gray2rgb(gray: UInt): (UInt, UInt, UInt) = {
    ((gray << 2).asUInt, (gray << 1).asUInt, (gray << 2).asUInt)
  }
  // Connections
  outputStream := inputStream

  inputStream.ready := true.B // This will overload the previous connection (inputStream.ready := outputStream.ready)

  when(inputStream.valid) {
    // Convert
    when(inputStream.el.color.tag === 0.U) {
      // Input is Gray
      val (r, g, b) = gray2rgb(inputStream.el.color.gray)
      val outRgb = outputStream.el.color.rgb
      outRgb.r := r
      outRgb.g := g
      outRgb.b := b

      outputStream.el.color.gray := 0.U
      outputStream.el.color.tag := 1.U

    }.otherwise {
      val inRgb = inputStream.el.color.rgb
      val gray = rgb2gray(inRgb.r, inRgb.g, inRgb.b)
      outputStream.el.color.gray := gray
      val outRgb = outputStream.el.color.rgb
      outRgb.r := 0.U
      outRgb.g := 0.U
      outRgb.b := 0.U
      outputStream.el.color.tag := 0.U
    }

  }
}

object Pixel_converterVerilog extends App {
  Emit(
    "output/Pixel_converter",
    () => new Pixel_converter(),
    "Pixel_converter"
  ).verilog()
}

object Pixel_converterFIRRTL extends App {
  Emit(
    "output/Pixel_converter",
    () => new Pixel_converter(),
    "Pixel_converter"
  ).firrtl()
}

object Pixel_converterGenerateHGDB extends App {
  Emit(
    "output/Pixel_converter",
    () => new Pixel_converter(),
    "Pixel_converter"
  ).hgdbOutputs()
}
