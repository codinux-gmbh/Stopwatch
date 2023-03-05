package net.codinux.util.statistics

import java.time.Duration

data class TaskStatistics(
  val countMeasurements: Int,
  val min: Duration,
  val max: Duration,
  val average: Duration,
  val total: Duration
)