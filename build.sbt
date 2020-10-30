name := "ClassAttendance"

version  in ThisBuild := "0.1"

scalaVersion in ThisBuild := "2.13.3"

libraryDependencies ++= Seq(
  "io.superflat" %% "lagompb-core" % "1.0.0",
  "io.superflat" %% "lagompb-core" % "1.0.0" % "protobuf" // this will add the necessary runtimes
)
