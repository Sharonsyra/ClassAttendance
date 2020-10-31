package com.sharonsyra.classattendance.student

import com.lightbend.lagom.scaladsl.akka.discovery.AkkaDiscoveryComponents
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.sharonsyra.classattendance.student.api.StudentService
import com.sharonsyra.protobuf.classattendance.common.Student
import com.softwaremill.macwire.wire
import io.superflat.lagompb.encryption.{NoEncryption, ProtoEncryption}
import io.superflat.lagompb.{AggregateRoot, BaseApplication, TypedCommandHandler, TypedEventHandler}

abstract class StudentApplication(context: LagomApplicationContext) extends BaseApplication(context) {
  // kamon.init() // this assists with akka monitoring. LagomPb comes with inbuilt kamon integration
  // I will comment this out for now to write on it later

  // wire up the event and command handler
  lazy val commandHandler: TypedCommandHandler[Student] = wire[StudentCommandHandler]
  lazy val eventHandler: TypedEventHandler[Student] = wire[StudentEventHandler]
  lazy val encryptor: ProtoEncryption = NoEncryption

  override lazy val aggregateRoot: AggregateRoot =
    new StudentAggregate(actorSystem, commandHandler, eventHandler, Student.defaultInstance, encryptionAdapter)

  override lazy val server: LagomServer =
    serverFor[StudentService](wire[StudentServiceImpl])
}

class StudentApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new StudentApplication(context) with AkkaDiscoveryComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new StudentApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] = Some(readDescriptor[StudentService])
}
