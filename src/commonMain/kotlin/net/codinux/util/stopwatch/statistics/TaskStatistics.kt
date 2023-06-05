package net.codinux.util.stopwatch.statistics

import net.codinux.util.stopwatch.Duration

data class TaskStatistics(
  val measuredDurations: List<Duration>,
  val min: Duration,
  val max: Duration,
  val average: Duration,
  val total: Duration
) {

  val countMeasurements = measuredDurations.size

}