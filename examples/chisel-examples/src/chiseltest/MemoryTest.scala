package chiselexamples
package memory

import chisel3._
import chiseltest._
import chiseltest.iotesters.PeekPokeTester
import org.scalatest.flatspec.AnyFlatSpec


class MemoryTester(c: Memory, packetToSend: Int = 1) extends PeekPokeTester(c) {

  // Write some data to the memory
  var i = 0
  var readAddr = 0
  var writeAddr = 0

  // Simply read and write some data
  for (data <- 0 until c.n) {
    readAddr = i % c.n
    writeAddr = (i + 1) % c.n
    // Write the data
    poke(c.io.writeEn, true.B)
    poke(c.io.addrIn, writeAddr.U)
    poke(c.io.dataIn, data.U)
    step(1)

    // Read the data
    poke(c.io.writeEn, false.B)
    poke(c.io.addrIn, readAddr.U)
    step(1)

    i += 1
  }
}


class MemoryWaveformTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "MemoryWaveformTest"

  val packetToSend = 10
  val n = 16
  val width = 32

  it should "dump Treadle VCD" in {
    test(new Memory(n, width))
      .withAnnotations(Seq(WriteVcdAnnotation, TreadleBackendAnnotation))
      .runPeekPoke(new MemoryTester(_))
  }

  it should "dump Verilator VCD" in {
    test(new Memory(n, width))
      .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation))
      .runPeekPoke(new MemoryTester(_))
  }

  it should "dump Icarus VCD" in {
    test(new Memory(n, width))
      .withAnnotations(Seq(WriteVcdAnnotation, IcarusBackendAnnotation))
      .runPeekPoke(new MemoryTester(_))

  }

  it should "dump Verilator FST" in {
    test(new Memory(n, width))
      .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation))
      .runPeekPoke(new MemoryTester(_))

  }

  it should "dump Icarus FST" in {
    test(new Memory(n, width))
      .withAnnotations(Seq(WriteFstAnnotation, IcarusBackendAnnotation))
      .runPeekPoke(new MemoryTester(_))

  }

  it should "dump Icarus LXT" in {
    test(new Memory(n, width))
      .withAnnotations(Seq(new WriteLxtAnnotation, IcarusBackendAnnotation))
      .runPeekPoke(new MemoryTester(_))
  }
}


