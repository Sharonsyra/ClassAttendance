package com.sharonsyra.classattendance.student.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceCall}
import com.lightbend.lagom.scaladsl.api.Service.restCall
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.sharonsyra.protobuf.classattendance.commands.{AddStudent, GetStudent, RemoveStudent}
import com.sharonsyra.protobuf.classattendance.common.{ListStudentDetails, Student}
import io.superflat.lagompb.BaseService

trait StudentService extends BaseService {

  def addStudent: ServiceCall[AddStudent, Student]

  def getStudent(student_uuid: String): ServiceCall[GetStudent, Student]

  def listStudents: ServiceCall[NotUsed, ListStudentDetails]

  def removeStudent(student_uuid: String): ServiceCall[RemoveStudent, Student]

  override val routes: Seq[Descriptor.Call[_, _]] = Seq(
    restCall(Method.POST, "/api/students", addStudent),
    restCall(Method.GET, "/api/students/:student_uuid", getStudent _),
    restCall(Method.GET, "/api/students", listStudents),
    restCall(Method.DELETE, "/api/students/:student_uuid", removeStudent _),
  )
}
