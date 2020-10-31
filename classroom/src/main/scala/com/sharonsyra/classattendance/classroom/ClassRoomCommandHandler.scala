package com.sharonsyra.classattendance.classroom

import java.time.Instant

import akka.actor.ActorSystem
import com.google.protobuf.any.Any
import com.sharonsyra.classattendance.common.Common
import com.sharonsyra.protobuf.classattendance.commands._
import com.sharonsyra.protobuf.classattendance.common.ClassRoom
import com.sharonsyra.protobuf.classattendance.events.{ClassAdded, ClassRemoved, TimeLogEnded, TimeLogStarted}
import io.superflat.lagompb.TypedCommandHandler
import io.superflat.lagompb.protobuf.v1.core.{CommandHandlerResponse, FailureResponse, MetaData}
import scalapb.GeneratedMessage

import scala.util.{Failure, Success, Try}

class ClassRoomCommandHandler(actorSystem: ActorSystem) extends TypedCommandHandler[ClassRoom](actorSystem) {

  override def handleTyped(
    command: GeneratedMessage,
    currentState: ClassRoom,
    currentMetaData: MetaData
  ): Try[CommandHandlerResponse] = {
    command match {
      case command: AddClass  => Try(handleAddClass(command, currentState))
      case command: GetClassRoom => Try(handleGetClassRoom(command, currentState))
      case command: CheckInStudent => Try(handleCheckInStudent(command, currentState))
      case command: CheckOutStudent => Try(handleCheckOutStudent(command, currentState))
      case command: StartTimeLog => Try(handleStartTimeLog(command, currentState))
      case command: EndTimeLog => Try(handleEndTimeLog(command, currentState))
      case command: RemoveClass => Try(handleRemoveClass(command, currentState))
      case _ =>
        Try(
          CommandHandlerResponse()
            .withFailure(
              FailureResponse()
                .withValidation(s"Unknown command ${command.getClass} supplied")
            )
        )
    }
  }

  private[classroom] def handleAddClass(addClass: AddClass, state: ClassRoom): CommandHandlerResponse = {
    Try(require(addClass.className.nonEmpty, "Missing class name")) match {
      case Success(value) =>
        CommandHandlerResponse()
          .withEvent(
            Any.pack(
              ClassAdded()
                .withClassUuid(addClass.classUuid)
                .withClassName(addClass.className)
            )
          )
      case Failure(exception) =>
        CommandHandlerResponse()
          .withFailure(
            FailureResponse()
              .withValidation(exception.getMessage)
          )
    }
  }

  private[classroom] def handleGetClassRoom(command: GetClassRoom, state: ClassRoom): CommandHandlerResponse = {
    Try(
      require(
        state.classUuid.equals(command.classUuid),
        s"Invalid class uuid ${command.classUuid} sent"
      )
    ) match {
      case Success(_) =>
        CommandHandlerResponse.defaultInstance
      case Failure(exception) =>
        CommandHandlerResponse()
          .withFailure(
            FailureResponse()
              .withValidation(exception.getMessage)
          )
    }
  }

  private[classroom] def handleCheckInStudent(command: CheckInStudent, state: ClassRoom): CommandHandlerResponse = {
    Try(
      require(
        state.classUuid.equals(command.classUuid),
        s"Invalid class uuid ${command.classUuid} sent"
      )
    )
      .map(_ => require(!state.isActiveSession, s"Class not in session!"))
      .map(_ => require(state.students.contains(command.studentUuid), s"Student already checked in!")) match {
        case Success(_) =>
          CommandHandlerResponse()
            .withEvent(
              Any.pack(
                CheckInStudent()
                  .withClassUuid(command.classUuid)
                  .withStudentUuid(command.studentUuid)
                  .withCheckInTime(Common.instantToTimestamp(Instant.now()))
              )
            )
        case Failure(exception) =>
          CommandHandlerResponse()
            .withFailure(
              FailureResponse()
                .withValidation(exception.getMessage)
            )
    }
  }

  private[classroom] def handleCheckOutStudent(command: CheckOutStudent, state: ClassRoom): CommandHandlerResponse = {
    Try(
      require(
        state.classUuid.equals(command.classUuid),
        s"Invalid class uuid ${command.classUuid} sent"
      )
    )
      .map(_ => require(!state.isActiveSession, s"Class not in session!"))
      .map(_ => require(!state.students.contains(command.studentUuid), s"Student not checked in!"))
      .map(_ => require(command.reason.nonEmpty, s"Reason is required!"))
    match {
      case Success(_) =>
        CommandHandlerResponse()
          .withEvent(
            Any.pack(
              CheckOutStudent()
                .withClassUuid(command.classUuid)
                .withStudentUuid(command.studentUuid)
                .withReason(command.reason)
                .withCheckOutTime(Common.instantToTimestamp(Instant.now()))
            )
          )
      case Failure(exception) =>
        CommandHandlerResponse()
          .withFailure(
            FailureResponse()
              .withValidation(exception.getMessage)
          )
    }
  }

  private[classroom] def handleStartTimeLog(command: StartTimeLog, state: ClassRoom): CommandHandlerResponse = {
    Try(
      require(
        state.classUuid.equals(command.classUuid),
        s"Invalid class uuid ${command.classUuid} sent"
      )
    )
      .map(_ => require(!state.isActiveSession, s"Class already in session")) match {
      case Success(_) =>
        CommandHandlerResponse()
        .withEvent(
          Any.pack(
            TimeLogStarted()
              .withClassUuid(command.classUuid)
              .withStartedAt(Common.instantToTimestamp(Instant.now()))
          )
        )
      case Failure(exception) =>
        CommandHandlerResponse()
          .withFailure(
            FailureResponse()
              .withValidation(exception.getMessage)
          )
    }
  }

  private[classroom] def handleEndTimeLog(command: EndTimeLog, state: ClassRoom): CommandHandlerResponse = {
    Try(
      require(
        state.classUuid.equals(command.classUuid),
        s"Invalid class uuid ${command.classUuid} sent"
      )
    )
      .map(_ => require(state.isActiveSession, s"Class not in session")) match {
      case Success(_) =>
        CommandHandlerResponse()
          .withEvent(
            Any.pack(
              TimeLogEnded()
                .withClassUuid(command.classUuid)
                .withEndedAt(Common.instantToTimestamp(Instant.now()))
            )
          )
      case Failure(exception) =>
        CommandHandlerResponse()
          .withFailure(
            FailureResponse()
              .withValidation(exception.getMessage)
          )
    }
  }

  private[classroom] def handleRemoveClass(command: RemoveClass, state: ClassRoom): CommandHandlerResponse = {
    Try(
      require(
        state.classUuid.equals(command.classUuid),
        s"Invalid class uuid ${command.classUuid} sent"
      )
    ) match {
      case Success(_) if(state.isDeleted) =>
        CommandHandlerResponse.defaultInstance
      case Success(_) =>
        CommandHandlerResponse()
          .withEvent(
            Any.pack(
              ClassRemoved()
                .withClassUuid(command.classUuid)
            )
          )
      case Failure(exception) =>
        CommandHandlerResponse()
          .withFailure(
            FailureResponse()
              .withValidation(exception.getMessage)
          )
    }
  }
}
