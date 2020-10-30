package com.sharonsyra.classattendance.classroom.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.Service.restCall
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceCall}
import com.sharonsyra.protobuf.classattendance.commands._
import com.sharonsyra.protobuf.classattendance.common._
import io.superflat.lagompb.BaseService

trait ClassRoomService extends BaseService {

  def addClass: ServiceCall[AddClass, ClassRoom]

  def getClassRoom(class_uuid: String): ServiceCall[GetClass, ClassRoom]

  def listClasses: ServiceCall[NotUsed, ListClassDetails]

  def checkInStudent(class_uuid: String, student_uuid: String): ServiceCall[CheckInStudent, ClassRoom]

  def checkOutStudent(class_uuid: String, student_uuid: String): ServiceCall[CheckOutStudent, ClassRoom]

  def startTimeLog(class_uuid: String): ServiceCall[StartTimeLog, ClassRoom]

  def endTimeLog(class_uuid: String): ServiceCall[EndTimeLog, ClassRoom]

  def removeClass(class_uuid: String): ServiceCall[RemoveClass, ClassRoom]

  override val routes: Seq[Descriptor.Call[_, _]] = Seq(
    restCall(Method.POST, "/api/classes", addClass),
    restCall(Method.GET, "/api/classes/:class_uuid", getClassRoom _),
    restCall(Method.GET, "/api/classes", listClasses),
    restCall(Method.PATCH, "/api/classes/:class_uuid/allocation/:student_uuid", checkInStudent _),
    restCall(Method.DELETE, "/api/classes/:class_uuid/allocation/:student_uuid", checkOutStudent _),
    restCall(Method.POST, "/api/classes/:class_uuid/session", startTimeLog _),
    restCall(Method.POST, "/api/classes/:class_uuid/session", endTimeLog _),
    restCall(Method.DELETE, "/api/classes/:class_uuid", removeClass _),
  )
}
