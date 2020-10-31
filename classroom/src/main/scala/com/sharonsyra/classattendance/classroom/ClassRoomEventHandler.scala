package com.sharonsyra.classattendance.classroom

import java.time.Instant

import akka.actor.ActorSystem
import com.sharonsyra.classattendance.common.Common
import com.sharonsyra.protobuf.classattendance.common.ClassRoom
import com.sharonsyra.protobuf.classattendance.events._
import io.superflat.lagompb.TypedEventHandler
import io.superflat.lagompb.protobuf.v1.core.MetaData

class ClassRoomEventHandler(actorSystem: ActorSystem) extends TypedEventHandler[ClassRoom](actorSystem) {

  override def handleTyped(event: scalapb.GeneratedMessage, state: ClassRoom, eventMeta: MetaData): ClassRoom = {
    event match {
      case event: ClassAdded => handleClassAdded(event, state)
      case event: StudentCheckedIn => handleStudentCheckedIn(event, state)
      case event: StudentCheckedOut => handleStudentCheckedOut(event, state)
      case event: TimeLogStarted => handleTimeLogStarted(event, state)
      case event: TimeLogEnded => handleTimeLogEnded(event, state)
      case event: ClassRemoved => handleClassRemoved(event, state)
      case _ => throw new NotImplementedError()
    }
  }

  private[classroom] def handleClassAdded(event: ClassAdded, state: ClassRoom): ClassRoom =
    state.update(
      _.classUuid := event.classUuid,
      _.className := event.className,
      _.createdAt := Common.instantToTimestamp(Instant.now()),
      _.updatedAt := Common.instantToTimestamp(Instant.now())
    )

  private[classroom] def handleStudentCheckedIn(event: StudentCheckedIn, state: ClassRoom): ClassRoom =
    state.update(
      _.students.:++=(Seq(event.studentUuid).diff(state.students))
    )

  private[classroom] def handleStudentCheckedOut(event: StudentCheckedOut, state: ClassRoom): ClassRoom =
    state
      .clearStudents
      .update(
        _.students.:++=(state.students.filterNot(_.equals(event.studentUuid)))
      )

  private[classroom] def handleTimeLogStarted(event: TimeLogStarted, state: ClassRoom): ClassRoom =
    state.update(
      _.isActiveSession := true,
      _.sessionStart := Common.instantToTimestamp(Instant.now())
    )

  private[classroom] def handleTimeLogEnded(event: TimeLogEnded, state: ClassRoom): ClassRoom =
    state.update(
      _.isActiveSession := false,
      _.sessionEnd := Common.instantToTimestamp(Instant.now())
    )

  private[classroom] def handleClassRemoved(event: ClassRemoved, state: ClassRoom): ClassRoom =
    state.update(
      _.isDeleted := true
    )
}
