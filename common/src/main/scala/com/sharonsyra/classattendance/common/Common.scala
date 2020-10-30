package com.sharonsyra.classattendance.common

import java.time.Instant

import com.sharonsyra.protobuf.classattendance.common.Timestamp

object Common {
  def instantToTimestamp(instant: Instant): Timestamp = {
    Timestamp()
      .withNanos(instant.getNano)
      .withSeconds(instant.getEpochSecond)
  }
}
