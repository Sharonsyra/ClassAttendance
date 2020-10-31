package com.sharonsyra.classattendance.student

import akka.actor.ActorSystem
import com.google.protobuf.any.Any
import com.sharonsyra.protobuf.classattendance.commands.{AddStudent, GetStudent, RemoveStudent}
import com.sharonsyra.protobuf.classattendance.common.Student
import com.sharonsyra.protobuf.classattendance.events.{StudentAdded, StudentRemoved}
import io.superflat.lagompb.TypedCommandHandler
import io.superflat.lagompb.protobuf.v1.core.{CommandHandlerResponse, FailureResponse, MetaData}
import scalapb.GeneratedMessage

import scala.util.{Failure, Success, Try}

class StudentCommandHandler(actorSystem: ActorSystem) extends TypedCommandHandler[Student](actorSystem) {

  override def handleTyped(
    command: GeneratedMessage,
    currentState: Student,
    currentMetaData: MetaData
  ): Try[CommandHandlerResponse] = {
    command match {
      case command: AddStudent => Try(handleAddStudent(command, currentState))
      case command: GetStudent => Try(handleGetStudent(command, currentState))
      case command: RemoveStudent => Try(handleRemoveStudent(command, currentState))
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

  private[student] def handleAddStudent(command: AddStudent, state: Student): CommandHandlerResponse = {
    Try(require(command.studentName.nonEmpty, "Missing student name")) match {
      case Success(value) =>
        CommandHandlerResponse()
          .withEvent(
            Any.pack(
              StudentAdded()
                .withStudentUuid(command.studentUuid)
                .withStudentName(command.studentName)
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

  private[student] def handleGetStudent(command: GetStudent, state: Student): CommandHandlerResponse = {
    Try(
      require(
        state.studentUuid.equals(command.studentUuid),
        s"Invalid class uuid ${command.studentUuid} sent"
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

  private[student] def handleRemoveStudent(command: RemoveStudent, state: Student): CommandHandlerResponse = {
    Try(
      require(
        state.studentUuid.equals(command.studentUuid),
        s"Invalid class uuid ${command.studentUuid} sent"
      )
    ) match {
      case Success(_) if(state.isDeleted) =>
        CommandHandlerResponse.defaultInstance
      case Success(_) =>
        CommandHandlerResponse()
          .withEvent(
            Any.pack(
              StudentRemoved()
                .withStudentUuid(command.studentUuid)
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
