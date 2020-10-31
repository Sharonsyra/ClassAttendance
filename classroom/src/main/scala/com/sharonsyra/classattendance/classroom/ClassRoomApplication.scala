package com.sharonsyra.classattendance.classroom

import com.lightbend.lagom.scaladsl.akka.discovery.AkkaDiscoveryComponents
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.sharonsyra.classattendance.classroom.api.ClassRoomService
import com.sharonsyra.protobuf.classattendance.common.ClassRoom
import com.softwaremill.macwire.wire
import io.superflat.lagompb.encryption.{NoEncryption, ProtoEncryption}
import io.superflat.lagompb.{AggregateRoot, BaseApplication, TypedCommandHandler, TypedEventHandler}

abstract class ClassRoomApplication(context: LagomApplicationContext) extends BaseApplication(context) {
  // kamon.init() // this assists with akka monitoring. LagomPb comes with inbuilt kamon integration
  // I will comment this out for now to write on it later

  // wire up the event and command handler
  lazy val commandHandler: TypedCommandHandler[ClassRoom] = wire[ClassRoomCommandHandler]
  lazy val eventHandler: TypedEventHandler[ClassRoom] = wire[ClassRoomEventHandler]
  lazy val encryptor: ProtoEncryption = NoEncryption

  override lazy val aggregateRoot: AggregateRoot =
    new ClassRoomAggregate(actorSystem, commandHandler, eventHandler, ClassRoom.defaultInstance, encryptionAdapter)

  override lazy val server: LagomServer =
    serverFor[ClassRoomService](wire[ClassRoomServiceImpl])
//   .additionalRouter(wire[ClassRoomGrpcServiceImpl]) // To cover later

}

class ClassRoomApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new ClassRoomApplication(context) with AkkaDiscoveryComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ClassRoomApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] = Some(readDescriptor[ClassRoomService])
}
