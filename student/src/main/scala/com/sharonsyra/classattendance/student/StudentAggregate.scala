package com.sharonsyra.classattendance.student

import akka.actor.ActorSystem
import com.sharonsyra.protobuf.classattendance.common.Student
import io.superflat.lagompb.encryption.EncryptionAdapter
import io.superflat.lagompb.{AggregateRoot, TypedCommandHandler, TypedEventHandler}

final class StudentAggregate(
  actorSystem: ActorSystem,
  commandHandler: TypedCommandHandler[Student],
  eventHandler: TypedEventHandler[Student],
  initialState: Student,
  encryptionAdapter: EncryptionAdapter
) extends AggregateRoot(actorSystem, commandHandler, eventHandler, initialState, encryptionAdapter) {

  override def aggregateName: String = "Student"

}
