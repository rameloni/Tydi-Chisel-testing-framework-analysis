// See LICENSE for license details.

/*
 * Code from: https://github.com/ucb-bar/chisel-tutorial/tree/release/src/main/scala/examples
 *
 * This example implement a router.
 *
 * This example aims to show how constants, objects, classes etc. are represented in the testing
 * framework when used to parameterized the logic.
 *
 * Specifically, this example specifies classes and objects to represent router characteristics.
 * They have a meaning beyond the simple representation as bits
 *  - the router characteristics (addressWidth, dataWidth, headerWidth, routeTableSize, numberOfOutputs)
 *  - ReadCmd and WriteCmd: they represents COMMANDS
 *  - Packet: this represent a Packet
 *  - RouterIO: this represents the IO of the router
 *
 * For example, a packet is a Class and from the chisel code it is clear that it is not compatible
 * with other "equivalent" classes. The following two classes are equivalent, however, they are not
 * compatible, since they are different classes (namely different types).
 *
 *      class Packet extends Bundle {
 *       val header = UInt(Router.headerWidth.W)
 *       val body = UInt(Router.dataWidth.W)
 *      }
 *
 *      class EquivalentPacket extends Bundle {
 *       val header = UInt(Router.headerWidth.W)
 *       val body = UInt(Router.dataWidth.W)
 *      }
 *
 * Even though in the resulting final verilog code they will be simple wires, this difference of should
 * be visible during debugging and it would be great the testing framework shows it.
 *
 */

package chiselexamples
package parameterized

import chisel3._
import chisel3.util.{DeqIO, EnqIO, log2Ceil}
import circt.stage.ChiselStage

object Router {
  val addressWidth = 32
  val dataWidth = 64
  val headerWidth = 8
  val routeTableSize = 15
  val numberOfOutputs = 4
}

class ReadCmd extends Bundle {
  val addr = UInt(Router.addressWidth.W)
}

class WriteCmd extends ReadCmd {
  val data = UInt(Router.addressWidth.W)
}

class Packet extends Bundle {
  val header = UInt(Router.headerWidth.W)
  val body = UInt(Router.dataWidth.W)
}

/** The router circuit IO It routes a packet placed on its single input port to
 * one of n output ports
 *
 * @param n
 * is the number of fanned outputs for the routed packet
 */
class RouterIO(val n: Int) extends Bundle {
  val read_routing_table_request = DeqIO(new ReadCmd())
  val read_routing_table_response = EnqIO(UInt(Router.addressWidth.W))
  val load_routing_table_request = DeqIO(new WriteCmd())
  val in = DeqIO(new Packet())
  val outs = Vec(n, EnqIO(new Packet()))
}

/** routes packets by using their header as an index into an externally loaded
 * and readable table, The number of addresses recognized does not need to
 * match the number of outputs
 */
class Router extends Module {
  val depth: Int = Router.routeTableSize
  val n: Int = Router.numberOfOutputs
  val io = IO(new RouterIO(n))
  val tbl = Mem(depth, UInt(BigInt(n).bitLength.W))

  // These ensure all output signals are driven.
  io.read_routing_table_request.nodeq()
  io.load_routing_table_request.nodeq()
  io.read_routing_table_response.noenq()
  io.read_routing_table_response.bits := 0.U
  io.in.nodeq()
  io.outs.foreach { out =>
    out.bits := 0.U.asTypeOf(out.bits)
    out.noenq()
  }

  // We rely on Chisel's "last connect" semantics to override the default connections as appropriate.
  when(
    io.read_routing_table_request.valid && io.read_routing_table_response.ready
  ) {
    io.read_routing_table_response.enq(
      tbl(
        io.read_routing_table_request.deq().addr
      )
    )
  }
    .elsewhen(io.load_routing_table_request.valid) {
      val cmd = io.load_routing_table_request.deq()
      tbl(cmd.addr) := cmd.data
      printf("setting tbl(%d) to %d\n", cmd.addr, cmd.data)
    }
    .elsewhen(io.in.valid) {
      val pkt = io.in.bits
      val idx = tbl(pkt.header(log2Ceil(Router.routeTableSize), 0))
      when(io.outs(idx).ready) {
        io.in.deq()
        io.outs(idx).enq(pkt)
        printf(
          "got packet to route header %d, data %d, being routed to out(%d)\n",
          pkt.header,
          pkt.body,
          tbl(pkt.header)
        )
      }
    }
}


object RouterVerilog extends App {
  private val outputDir = "output/router/verilog"

  // emit Verilog
  emitVerilog(
    new Router(),
    Array("--target-dir", outputDir, "--split-verilog")
  )
}

object RouterFIRRTL extends App {
  private val outputDir = "output/router/firrtl"

  val firrtl = ChiselStage.emitCHIRRTL(
    new Router(),
  )

  // val thisDir = new java.io.File(".").getCanonicalPath
  val dir = new java.io.File(outputDir)
  if (!dir.exists()) {
    dir.mkdir()
  }

  val pw =
    new java.io.PrintWriter(new java.io.File(outputDir + "/Router.fir"))
  pw.write(firrtl)
  pw.close()

}
