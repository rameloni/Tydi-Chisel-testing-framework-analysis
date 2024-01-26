package chiselexamples
package parameterized

import chisel3.experimental.BundleLiterals.AddBundleLiteralConstructor
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class RouterTester(c: Router, packetToSend: Int = 1) {

  // Init the router sources and sinks
  def initRouter(): Unit = {
    c.io.read_routing_table_request.initSource().setSourceClock(c.clock)
    c.io.read_routing_table_response.initSink().setSinkClock(c.clock)
    c.io.load_routing_table_request.initSource().setSourceClock(c.clock)
    c.io.in.initSource().setSourceClock(c.clock)
    c.io.outs.foreach(_.initSink().setSinkClock(c.clock))
  }

  // Read the routing table and expect
  def readRoutingTable(addr: Int, data: Option[Int]): Unit = {
    c.io.read_routing_table_response.ready.poke(true.B)

    fork {
      if (data.isDefined)
        c.io.read_routing_table_response.expectDequeue(data.get.U)
    }.fork {
      c.io.read_routing_table_request.enqueue(new ReadCmd().Lit(_.addr -> addr.U))
    }.join()
  }

  // Write the routing table
  def writeRoutingTable(addr: Int, data: Int): Unit = {
    c.io.load_routing_table_request.enqueue(new WriteCmd().Lit(_.addr -> addr.U, _.data -> data.U))
  }

  // Write the routing table and check that the data is written
  def writeRoutingTableWithConfirm(addr: Int, data: Int): Unit = {
    writeRoutingTable(addr, data)
    readRoutingTable(addr, Some(data))
  }

  def routePacket(header: Int, body: Int, routed_to: Int): Unit = {
    // Set the outs ready
    c.io.outs.foreach(_.ready.poke(true.B))

    fork {
      c.io.in.enqueueNow(
        new Packet().Lit(
          _.header -> header.U,
          _.body -> body.U)
      )
    }.fork {
      //      c.io.outs(routed_to).expectDequeue(
      //        new Packet().Lit(
      //          _.header -> header.U,
      //          _.body -> body.U)
      //      )
    }.join()

    println(s"rout_packet $header $body should go to out($routed_to)")
  }

  def apply() = {

    initRouter()

    // load routing table, confirm each write as built
    for (i <- 0 until Router.numberOfOutputs) {
      writeRoutingTableWithConfirm(i, (i + 1) % Router.numberOfOutputs)
    }

    // check them in reverse order just for fun
    for (i <- Router.numberOfOutputs - 1 to 0 by -1) {
      readRoutingTable(i, Some((i + 1) % Router.numberOfOutputs))
    }

    // send some regular packets
    for (i <- 0 until Router.numberOfOutputs) {
      routePacket(i, i * 3, (i + 1) % 4)
    }


    // generate a new routing table
    val new_routing_table = Array.tabulate(Router.routeTableSize) { _ =>
      scala.util.Random.nextInt(Router.numberOfOutputs)
    }

    // load a new routing table
    for ((destination, index) <- new_routing_table.zipWithIndex) {
      writeRoutingTable(index, destination)
    }

    // send a bunch of packets, with random values
    val rnd = new scala.util.Random(0)
    for (i <- 0 until packetToSend) {
      // scala rnd
      val data = rnd.nextInt(Int.MaxValue - 1)
      routePacket(i % Router.routeTableSize, data, new_routing_table(i % Router.routeTableSize))
    }
    for (i <- 0 until packetToSend) {
      c.clock.step(1)
      writeRoutingTable(i, i)
      readRoutingTable(i, None)

      routePacket(i, i, i)
    }

  }
}

object RouterTester {
  def apply(c: Router, packetToSend: Int = 1) = {
    new RouterTester(c, packetToSend = packetToSend).apply()
  }
}

class RouterWaveformTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "RouterWaveformTest"

  val packetToSend = 10

  it should "run" in {
    test(new Router)
      .withAnnotations(Seq(WriteVcdAnnotation)) {
        c =>
          //          c.io.read_routing_table_request.setSourceClock(c.clock)
          c.io.read_routing_table_request.initSource().setSourceClock(c.clock)
          c.io.read_routing_table_response.initSink().setSinkClock(c.clock)
          c.io.load_routing_table_request.initSource().setSourceClock(c.clock)
          c.io.in.initSource().setSourceClock(c.clock)
          c.io.outs.foreach(_.initSink().setSinkClock(c.clock))

          val r = new ReadCmd().Lit(_.addr -> 3.U)
          c.io.read_routing_table_response.ready.poke(true.B)
          c.io.read_routing_table_request.enqueue(r)

      }
  }

  it should "dump Treadle VCD" in {
    test(new Router())
      .withAnnotations(Seq(WriteVcdAnnotation, TreadleBackendAnnotation)) {
        c => RouterTester(c, packetToSend = packetToSend)
      }
  }

  it should "dump Verilator VCD" in {
    test(new Router())
      .withAnnotations(Seq(WriteVcdAnnotation, VerilatorBackendAnnotation)) {
        c => RouterTester(c, packetToSend = packetToSend)
      }
  }

  it should "dump Icarus VCD" in {
    test(new Router())
      .withAnnotations(Seq(WriteVcdAnnotation, IcarusBackendAnnotation)) {
        c => RouterTester(c, packetToSend = packetToSend)
      }
  }

  it should "dump Verilator FST" in {
    test(new Router())
      .withAnnotations(Seq(WriteFstAnnotation, VerilatorBackendAnnotation)) {
        c => RouterTester(c, packetToSend = packetToSend)
      }
  }

  it should "dump Icarus FST" in {
    test(new Router())
      .withAnnotations(Seq(WriteFstAnnotation, IcarusBackendAnnotation)) {
        c => RouterTester(c, packetToSend = packetToSend)
      }
  }

  it should "dump Icarus LXT" in {
    test(new Router())
      .withAnnotations(Seq(new WriteLxtAnnotation, IcarusBackendAnnotation)) {
        c => RouterTester(c, packetToSend = packetToSend)
      }
  }
}


