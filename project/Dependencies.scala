package com.sharonsyra.classattendance

import sbt._
import sbt.librarymanagement.ModuleID

object Dependencies {
  object Versions {
    val scala213 = "2.13.1"
    val lagompbVersion = "1.0.2"
    val akkaVersion = "2.6.10"
  }

  object Compile {
    val lagompb: ModuleID = "io.superflat" %% "lagompb-core" % Versions.lagompbVersion
    val lagompbReadSide = "io.superflat" %% "lagompb-readside" % Versions.lagompbVersion
  }

  object Runtime {
    val lagompbRuntime: ModuleID = "io.superflat" %% "lagompb-core" % Versions.lagompbVersion % "protobuf"
  }
}
