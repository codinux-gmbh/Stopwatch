package net.codinux.util.statistics

import kotlin.time.Duration


interface TaskStatisticsCollector {

  fun addElapsedTime(task: String, elapsed: Duration)

  fun getMeasuredDurationsFor(task: String): List<Duration>?

  fun getStatisticsFor(task: String): TaskStatistics?

  fun logStatistics(task: String)

  fun logAllStatistics()

}