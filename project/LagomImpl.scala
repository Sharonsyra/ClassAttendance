package com.sharonsyra.classattendance

import com.lightbend.lagom.sbt.LagomImport._
import com.sharonsyra.classattendance.Dependencies.{Compile, Runtime}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerBaseImage
import sbt.Keys.{libraryDependencies, version}
import sbt.{AutoPlugin, Plugins, plugins}


object LagomImpl extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings = Seq(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslTestKit,
      lagomScaladslAkkaDiscovery,
      lagomScaladslPersistenceJdbc,
      lagomScaladslCluster,
      Compile.lagompb,
      Compile.lagompbReadSide,
      Runtime.lagompbRuntime
    )
  )
}
