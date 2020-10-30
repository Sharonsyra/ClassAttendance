package com.sharonsyra.classattendance

import com.lightbend.lagom.sbt.LagomImport.{lagomScaladslApi, lagomScaladslServer}
import com.sharonsyra.classattendance.Dependencies.{Compile, Runtime}
import sbt.Keys.libraryDependencies
import sbt._

object LagomApi extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings = Seq(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer % Optional,
      Compile.lagompb,
      Runtime.lagompbRuntime
    )
  )
}