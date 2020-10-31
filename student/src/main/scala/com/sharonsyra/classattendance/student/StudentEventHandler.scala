package com.sharonsyra.classattendance.student

import java.time.Instant

import akka.actor.ActorSystem
import com.sharonsyra.classattendance.common.Common
import com.sharonsyra.protobuf.classattendance.common.Student
import com.sharonsyra.protobuf.classattendance.events.{StudentAdded, StudentRemoved}
import io.superflat.lagompb.TypedEventHandler
import io.superflat.lagompb.protobuf.v1.core.MetaData
import scalapb.GeneratedMessage

class StudentEventHandler(actorSystem: ActorSystem) extends TypedEventHandler[Student](actorSystem) {

  override def handleTyped(event: GeneratedMessage, currentState: Student, metaData: MetaData): Student = {
    event match {
      case event: StudentAdded => handleStudentAdded(event, currentState)
      case event: StudentRemoved => handleStudentRemoved(event, currentState)
      case _ => throw new NotImplementedError()
    }
  }

  private[student] def handleStudentAdded(event: StudentAdded, state: Student): Student = {
    state.update(
      _.studentUuid := event.studentUuid,
      _.studentName := event.studentName,
      _.createdAt := Common.instantToTimestamp(Instant.now()),
      _.updatedAt := Common.instantToTimestamp(Instant.now())
    )
  }

  private[student] def handleStudentRemoved(event: StudentRemoved, state: Student): Student = {
    state.update(
      _.isDeleted := true
    )
  }

}
