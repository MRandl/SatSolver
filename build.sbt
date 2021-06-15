val scaVersion = "3.0.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-simple",
    version := "0.1.0",
    scalaVersion := scaVersion
)
