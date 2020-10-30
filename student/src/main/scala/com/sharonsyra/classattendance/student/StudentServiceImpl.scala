package com.sharonsyra.classattendance.student

import java.util.UUID

import akka.NotUsed
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import com.google.protobuf.any.Any
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.sharonsyra.classattendance.student.api.StudentService
import com.sharonsyra.protobuf.classattendance.commands.{AddStudent, GetStudent, RemoveStudent}
import com.sharonsyra.protobuf.classattendance.common.{ListStudentDetails, Student}
import io.superflat.lagompb.{AggregateRoot, BaseServiceImpl}

import scala.concurrent.ExecutionContext

class StudentServiceImpl(
  clusterSharding: ClusterSharding,
  persistentEntityRegistry: PersistentEntityRegistry,
  aggregateRoot: AggregateRoot
)(
  implicit ec: ExecutionContext
) extends BaseServiceImpl(clusterSharding, persistentEntityRegistry, aggregateRoot) with StudentService {
  override def addStudent: ServiceCall[AddStudent, Student] = req => {
    val entityId = UUID.randomUUID().toString
    sendCommand(
      entityId,
      AddStudent()
        .withStudentUuid(entityId)
        .withStudentName(req.studentName),
      Map.empty[String, Any]
    ).map(result => result.getState.unpack[Student])
  }

  override def getStudent(student_uuid: String): ServiceCall[GetStudent, Student] = { _ =>
    sendCommand(
      student_uuid,
      GetStudent()
        .withStudentUuid(student_uuid),
      Map.empty[String, Any]
    ).map(result => result.getState.unpack[Student])
  }

  override def listStudents: ServiceCall[NotUsed, ListStudentDetails] = ???

  override def removeStudent(student_uuid: String): ServiceCall[RemoveStudent, Student] = { _ =>
    sendCommand(
      student_uuid,
      RemoveStudent()
        .withStudentUuid(student_uuid),
      Map.empty[String, Any]
    ).map(result => result.getState.unpack[Student])
  }
}
