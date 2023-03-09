package net.codinux.util.statistics

import java.time.Duration

data class TaskStatistics(
  val measuredDurations: List<Duration>,
  val min: Duration,
  val max: Duration,
  val average: Duration,
  val total: Duration
) {

  val countMeasurements = measuredDurations.size

}