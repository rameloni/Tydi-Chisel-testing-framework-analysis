package tydiexamples.PixelConverter

import chisel3._
import chisel3.experimental.BundleLiterals.AddBundleLiteralConstructor
import chiseltest._
import nl.tudelft.tydi_chisel._
import nl.tudelft.tydi_chisel.Conversions._
import org.scalatest.flatspec.AnyFlatSpec


class Pixel_converterTestWrapper(module: => Pixel_converter,
                                 val eIn: Pixel,
                                 val eOut: Pixel) extends TydiModule {
  private val mod: Pixel_converter = Module(module)

  val io = IO(new Bundle {
    val inputStream = Flipped(mod.inputStream.cloneType)
    val outputStream = mod.outputStream.cloneType
  })

  // Connects the input and output of the module
  mod.io.input := io.inputStream
  io.outputStream := mod.io.output

}

// Tester for the Adder module
class Pixel_converterTester(dut: Pixel_converterTestWrapper, stepsFor: Int = 1, printDebug: Boolean = false) {
  val pixel_t = new Pixel()

  case class Rgb(r: Int, g: Int, b: Int)

  case class Color(rgb: Rgb, gray: Int, tag: Int) {
    def apply: Instance_Color_Identifiercolor_depth_1 = {
      pixel_t.color.Lit(_.tag -> tag.U, _.gray -> gray.U,
        _.rgb -> pixel_t.color.rgb.Lit(_.r -> rgb.r.U, _.g -> rgb.g.U, _.b -> rgb.b.U))
    }
  }

  def extractFloatComponents(input: Float): (Int, Int, Int) = {
    val floatBits = java.lang.Float.floatToIntBits(input)

    val sign = if ((floatBits & 0x80000000) == 0) 0 else 1
    val exponent = ((floatBits >> 23) & 0xFF) - 127
    val mantissa = (floatBits & 0x7FFFFF) / Math.pow(2, 23).toFloat

    (exponent, mantissa.toInt, sign)
  }

  def createPos(x: Float, y: Float): Pos = {
    val (x_exponent, x_mantissa, x_sign) = extractFloatComponents(x)
    val (y_exponent, y_mantissa, y_sign) = extractFloatComponents(y)
    val x_pos = pixel_t.pos.x.Lit(
      _.exponent -> pixel_t.pos.x.exponent.Lit(_.value -> x_exponent.U),
      _.mantissa -> pixel_t.pos.x.mantissa.Lit(_.value -> x_mantissa.U),
      _.sign -> x_sign.U)

    val y_pos = pixel_t.pos.y.Lit(
      _.exponent -> pixel_t.pos.y.exponent.Lit(_.value -> y_exponent.U),
      _.mantissa -> pixel_t.pos.y.mantissa.Lit(_.value -> y_mantissa.U),
      _.sign -> y_sign.U)

    pixel_t.pos.Lit(_.x -> x_pos, _.y -> y_pos)
  }

  def createPixel(tag: Int, rgb: Rgb, gray: Int, x: Float, y: Float): Pixel = {
    val color = Color(rgb, gray, tag).apply
    val pos = createPos(x, y)
    pixel_t.Lit(_.color -> color, _.pos -> pos)
  }

  def apply: Unit = {
    // Init the streams
    dut.io.inputStream.initSource().setSourceClock(dut.clock)
    dut.io.outputStream.initSink().setSinkClock(dut.clock)

    var tag = 0
    for (r <- 13 until 256 by stepsFor)
      for (g <- 14 until 256 by stepsFor)
        for (b <- 15 until 256 by stepsFor) {

          // Create a pixel
          val pixel = createPixel(tag, Rgb(r, g, b), (r + g + b) / 3, r / 3.0f, g / 3.0f)

          // Enqueue the input stream
          tag = (tag + 1) % 2
          parallel(
            dut.io.inputStream.enqueue(pixel),
            fork
              .withRegion(Monitor) {
                dut.io.outputStream.el.color.tag.expect(tag.U)
              }
              .joinAndStep(dut.clock)
          )
        }

    println(pixel_t.color.createEnum)
  }
} // end class Pixel_converterTester

object Pixel_converterTester {
  def apply(dut: => Pixel_converterTestWrapper, stepsFor: Int = 1, printDebug: Boolean = false): Unit = {
    new Pixel_converterTester(dut, stepsFor, printDebug).apply
  }
} // end object Pixel_converterTester

class Pixel_converterWaveformTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "Pixel_converterWaveformTest"

  val stepsFor = 98

  it should "dump Treadle VCD" in {
    test(new Pixel_converterTestWrapper(new Pixel_converter, new Pixel, new Pixel))
      .withAnnotations(Seq(WriteVcdAnnotation, TreadleBackendAnnotation)) {
        Pixel_converterTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Verilator VCD" in {
    test(new Pixel_converterTestWrapper(new Pixel_converter, new Pixel, new Pixel))
      .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation)) {
        Pixel_converterTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Icarus VCD" in {
    test(new Pixel_converterTestWrapper(new Pixel_converter, new Pixel, new Pixel))
      .withAnnotations(Seq(WriteVcdAnnotation, IcarusBackendAnnotation)) {
        Pixel_converterTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Verilator FST" in {
    test(new Pixel_converterTestWrapper(new Pixel_converter, new Pixel, new Pixel))
      .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation)) {
        Pixel_converterTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Icarus FST" in {
    test(new Pixel_converterTestWrapper(new Pixel_converter, new Pixel, new Pixel))
      .withAnnotations(Seq(WriteFstAnnotation, IcarusBackendAnnotation)) {
        Pixel_converterTester(_, stepsFor = stepsFor)
      }
  }

  it should "dump Icarus LXT" in {
    test(new Pixel_converterTestWrapper(new Pixel_converter, new Pixel, new Pixel))
      .withAnnotations(Seq(new WriteLxtAnnotation, IcarusBackendAnnotation)) {
        Pixel_converterTester(_, stepsFor = stepsFor)
      }
  }
} // end class AdderWaveformTest