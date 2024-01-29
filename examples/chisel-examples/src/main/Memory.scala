package chiselexamples
package memory

import chisel3._
import chisel3.util.log2Ceil

/*
 * This example uses chisel `Mem`.
 * It is also used in the `Router` example.
 * Here, it is used in order to highlight the representation of Mem.
 * The Router example is too complex to well show the weaknesses of a chisel memory representation in testing tools.
 */
class Memory(val n: Int, val width: Int) extends Module {

  // Define the address size
  val addrSize = log2Ceil(n)

  // Define the IO
  val io = IO(new Bundle {
    val writeEn = Input(Bool())
    val addrIn = Input(UInt(addrSize.W))
    val dataIn = Input(UInt(width.W))
    val dataOut = Output(UInt(width.W))
  })
  // The internal memory
  val mem: Mem[UInt] = Mem(n, UInt(32.W))

  // Read/Write logic
  when(io.writeEn) {
    mem.write(io.addrIn, io.dataIn)
    io.dataOut := DontCare
  }.otherwise {
    io.dataOut := mem.read(io.addrIn)
  }
}


object MemoryVerilog extends App {
  Emit(
    "output/memory",
    () => new Memory(4, 32),
    "Memory"
  ).verilog()
}

object MemoryFirrtl extends App {
  Emit(
    "output/memory",
    () => new Memory(4, 32),
    "Memory"
  ).firrtl()
}

object MemoryHGDB extends App {
  Emit(
    "output/memory",
    () => new Memory(4, 32),
    "Memory"
  ).hgdbOutputs()
}

