package com.sharonsyra.classattendance.classroom

import java.time.Instant
import java.util.UUID

import akka.NotUsed
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import com.google.protobuf.any.Any
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.sharonsyra.classattendance.classroom.api.ClassRoomService
import com.sharonsyra.classattendance.common.Common
import com.sharonsyra.protobuf.classattendance.commands._
import com.sharonsyra.protobuf.classattendance.common._
import io.superflat.lagompb.{AggregateRoot, BaseServiceImpl}

import scala.concurrent.ExecutionContext

class ClassRoomServiceImpl(
  clusterSharding: ClusterSharding,
  persistentEntityRegistry: PersistentEntityRegistry,
  aggregateRoot: AggregateRoot
)(
  implicit ec: ExecutionContext
) extends BaseServiceImpl(clusterSharding, persistentEntityRegistry, aggregateRoot)
  with ClassRoomService {

  override def addClass: ServiceCall[AddClass, ClassRoom] = req => {
    val entityId: String = UUID.randomUUID().toString
    sendCommand(
      entityId,
      AddClass()
        .withClassUuid(entityId)
        .withClassName(req.className),
      Map.empty[String, Any]
    ).map(result => result.getState.unpack[ClassRoom])
  }

  override def getClassRoom(class_uuid: String): ServiceCall[GetClassRoom, ClassRoom] = { _ =>
    sendCommand(
      class_uuid,
      GetClassRoom()
        .withClassUuid(class_uuid),
      Map.empty[String, Any]
    ).map(result => result.getState.unpack[ClassRoom])
  }

  override def listClasses: ServiceCall[NotUsed, ListClassDetails] = ???

  override def checkInStudent(class_uuid: String, student_uuid: String): ServiceCall[CheckInStudent, ClassRoom] = { _ =>
    sendCommand(
      class_uuid,
      CheckInStudent()
        .withClassUuid(class_uuid)
        .withStudentUuid(student_uuid)
        .withCheckInTime(Common.instantToTimestamp(Instant.now())),
      Map.empty[String, Any]
    ).map(result => result.getState.unpack[ClassRoom])

  }

  override def checkOutStudent(class_uuid: String, student_uuid: String): ServiceCall[CheckOutStudent, ClassRoom] = { req =>
    sendCommand(
      class_uuid,
      CheckOutStudent()
        .withClassUuid(class_uuid)
        .withStudentUuid(student_uuid)
        .withCheckOutTime(Common.instantToTimestamp(Instant.now()))
        .withReason(req.reason),
      Map.empty[String, Any]
    ).map(result => result.getState.unpack[ClassRoom])
  }

  override def startTimeLog(class_uuid: String): ServiceCall[StartTimeLog, ClassRoom] = { _ =>
    sendCommand(
      class_uuid,
      StartTimeLog()
        .withClassUuid(class_uuid)
        .withStartedAt(Common.instantToTimestamp(Instant.now())),
      Map.empty[String, Any]
    ).map(result => result.getState.unpack[ClassRoom])
  }

  override def endTimeLog(class_uuid: String): ServiceCall[EndTimeLog, ClassRoom] = { _ =>
    sendCommand(
      class_uuid,
      EndTimeLog()
        .withClassUuid(class_uuid)
        .withEndedAt(Common.instantToTimestamp(Instant.now())),
      Map.empty[String, Any]
    ).map(result => result.getState.unpack[ClassRoom])
  }

  override def removeClass(class_uuid: String): ServiceCall[RemoveClass, ClassRoom] = { _ =>
    sendCommand(
      class_uuid,
      RemoveClass()
        .withClassUuid(class_uuid),
      Map.empty[String, Any]
    ).map(result => result.getState.unpack[ClassRoom])
  }
}
