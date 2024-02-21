package chiselexamples

import chisel3.RawModule
import circt.stage.ChiselStage

protected class Emit[T <: RawModule](
                                      val outputPath: String,
                                      // Function
                                      val module: () => T,
                                      val moduleName: String
                                    ) {

  private val outputDirVerilog = outputPath + "/verilog"
  private val outputDirFirrtl = outputPath + "/firrtl"
  private val outputDirHGDB = outputPath + "/hgdb"

  private val nameFirrtl = moduleName + ".fir"
  private val nameToml = moduleName + ".toml"
  private val nameDB = moduleName + ".db"

  def verilog(): Unit = {
    // emit Verilog
    ChiselStage.emitSystemVerilogFile(
      module(),
      Array("--split-verilog", "--target-dir", outputDirVerilog),
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  }

  def firrtl(): Unit = {
    val firrtl = ChiselStage.emitCHIRRTL(
      module()
    )
    val dir = new java.io.File(outputDirFirrtl)
    if (!dir.exists())
      dir.mkdirs()

    val pw = new java.io.PrintWriter(new java.io.File(outputDirFirrtl + "/" + nameFirrtl))
    pw.write(firrtl)
    pw.close()
  }

  import sys.process._

  /**
   * ! This requires the hgdb-firrtl and toml2hgdb to be installed
   */
  def hgdbOutputs(): Unit = {
    // Emit first the firrtl file
    // firrtl()

    if (!new java.io.File(outputDirHGDB).exists())
      new java.io.File(outputDirHGDB).mkdirs()
    // Generate the hgdb outputs
    val hgdbFirrtlCmd =
      "hgdb-firrtl -i " + outputDirFirrtl + "/" + nameFirrtl + " --hgdb-toml " + outputDirHGDB + "/" + nameToml
    hgdbFirrtlCmd.!

    val toml2hgdb =
      "toml2hgdb " + outputDirHGDB + "/" + nameToml + " " + outputDirHGDB + "/" + nameDB
    toml2hgdb.!
  }
}

object Emit {
  def apply(
             outputPath: String,
             module: () => RawModule,
             moduleName: String
           ): Emit[RawModule] = {
    new Emit[RawModule](
      outputPath,
      module,
      moduleName
    )
  }
}