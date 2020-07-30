val dottyVersion = "0.25.0-RC2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-simple",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      ("org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0").withDottyCompat(scalaVersion.value)
    )
)
