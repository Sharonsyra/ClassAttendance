name := "ClassAttendance"

version  in ThisBuild := "0.1"

scalaVersion in ThisBuild := "2.13.3"

libraryDependencies ++= Seq(
  "io.superflat" %% "lagompb-core" % "1.0.0",
  "io.superflat" %% "lagompb-core" % "1.0.0" % "protobuf" // this will add the necessary runtimes
)

lazy val `class-attendance-service` =
  (project in file("."))
    .aggregate(`common`, `class-api`, `class`, `student-api`, `student`)
    .settings(publishArtifact := false, skip in publish := true)

lazy val `common` = (project in file("common"))
  .settings(
    name := "common",
    PB.protoSources in Compile := Seq(file("common/src/main/protobuf")),
    PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value)
  )

lazy val `class-api` = (project in file("class-api"))
  .settings(name := "class-api")
  .dependsOn(`common`)

lazy val `class` = (project in file("class"))
  .settings(name := "class")
  .dependsOn(`common`, `class-api`)

lazy val `student-api` = (project in file("student-api"))
  .settings(name := "student-api")
  .dependsOn(`common`)

lazy val `student` = (project in file("student"))
  .settings(name := "student")
  .dependsOn(`common`, `student-api`)
