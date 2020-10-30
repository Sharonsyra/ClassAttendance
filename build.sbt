import com.sharonsyra.classattendance.Dependencies.Versions

name := "ClassAttendance"

version  in ThisBuild := "0.1"

scalaVersion in ThisBuild := "2.13.3"

lazy val `class-attendance-service` =
  (project in file("."))
    .aggregate(`common`, `classroom-api`, `classroom`, `student-api`, `student`)
    .settings(publishArtifact := false, skip in publish := true)

lazy val `common` = (project in file("common"))
  .settings(
    name := "common",
    PB.protoSources in Compile := Seq(file("common/src/main/protobuf")),
    PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value),
    libraryDependencies ++= Seq(
      "io.superflat" %% "lagompb-core" % Versions.lagompbVersion,
      "io.superflat" %% "lagompb-core" % Versions.lagompbVersion % "protobuf"
    )
  )

lazy val `classroom-api` = (project in file("classroom-api"))
  .enablePlugins(LagomAkka)
  .enablePlugins(LagomApi)
  .settings(name := "classroom-api")
  .dependsOn(`common`)

lazy val `classroom` = (project in file("classroom"))
  .enablePlugins(LagomAkka)
  .enablePlugins(LagomImpl)
  .settings(name := "classroom")
  .dependsOn(`common`, `classroom-api`)

lazy val `student-api` = (project in file("student-api"))
  .enablePlugins(LagomAkka)
  .enablePlugins(LagomApi)
  .settings(name := "student-api")
  .dependsOn(`common`)

lazy val `student` = (project in file("student"))
  .enablePlugins(LagomAkka)
  .enablePlugins(LagomImpl)
  .settings(name := "student")
  .dependsOn(`common`, `student-api`)
