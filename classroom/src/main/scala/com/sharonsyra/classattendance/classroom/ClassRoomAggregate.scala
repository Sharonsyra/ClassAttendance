package com.sharonsyra.classattendance.classroom

import akka.actor.ActorSystem
import com.sharonsyra.protobuf.classattendance.common.ClassRoom
import io.superflat.lagompb.encryption.EncryptionAdapter
import io.superflat.lagompb.{AggregateRoot, TypedCommandHandler, TypedEventHandler}

final class ClassRoomAggregate(
  actorSystem: ActorSystem,
  commandHandler: TypedCommandHandler[ClassRoom],
  eventHandler: TypedEventHandler[ClassRoom],
  initialState: ClassRoom,
  encryptionAdapter: EncryptionAdapter
) extends AggregateRoot(actorSystem, commandHandler, eventHandler, initialState, encryptionAdapter) {

  override def aggregateName: String = "ClassRoom"

}
