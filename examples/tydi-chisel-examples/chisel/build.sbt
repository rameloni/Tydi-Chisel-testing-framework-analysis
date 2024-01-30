val chiselVersion = "5.0.0"
//val chiselTestVersion = "5.1-SNAPSHOT"
val chiselTestVersion = "5.0.0"
Compile / scalaSource := baseDirectory.value / "src/main"

Test / scalaSource := baseDirectory.value / "src/test"

Compile / doc / scalacOptions ++= Seq("-siteroot", "docs")

unmanagedBase := baseDirectory.value / "lib"

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Tydi-Chisel-testing-framework-analysis: Tydi-Chisel examples",
    addCompilerPlugin(
      "org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full
    ),
    libraryDependencies += "org.chipsalliance" %% "chisel" % chiselVersion,
    libraryDependencies += "edu.berkeley.cs" %% "chiseltest" % chiselTestVersion,
    libraryDependencies += "nl.tudelft" %% "root" % "0.1.0",
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked",
      // "-Xfatal-warnings",
      "-language:reflectiveCalls",
      "-Ymacro-annotations"
    )
  )
