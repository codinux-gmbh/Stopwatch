package net.codinux.util.statistics

import java.time.Duration


interface TaskStatisticsCollector {

  fun addElapsedTime(task: String, elapsed: Duration)

  fun getMeasuredDurationsFor(task: String): List<Duration>?

  fun getStatisticsFor(task: String): TaskStatistics?

  fun printStatistics(task: String)

  fun printAllStatistics()

}