package net.codinux.util.stopwatch.statistics

import net.codinux.util.stopwatch.Duration

interface TaskStatisticsCollector {

  fun addElapsedTime(task: String, elapsed: Duration)

  fun getMeasuredDurationsFor(task: String): List<Duration>?

  fun getStatisticsFor(task: String): TaskStatistics?

  fun logStatistics(task: String)

  fun logAllStatistics()

}