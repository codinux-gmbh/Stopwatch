package net.codinux.util.stopwatch.statistics

import kotlin.time.Duration

data class TaskStatistics(
  val measuredDurations: List<Duration>,
  val min: Duration,
  val max: Duration,
  val average: Duration,
  val total: Duration
) {

  val countMeasurements = measuredDurations.size

}