package net.codinux.util.stopwatch.statistics

import net.codinux.util.stopwatch.Duration

interface TaskStatisticsCollector {

  fun addElapsedTime(task: String, elapsed: Duration)

  fun getMeasuredDurationsFor(task: String): List<Duration>?

  fun getStatisticsFor(task: String): TaskStatistics?

  fun getAllStatistics(): List<TaskStatistics> = getAllStatistics(StatisticsSortProperty.TaskName)

  fun getAllStatistics(sort: StatisticsSortProperty = StatisticsSortProperty.TaskName): List<TaskStatistics>

  fun logStatistics(task: String)

  fun logAllStatistics() = logAllStatistics(StatisticsSortProperty.TaskName)

  fun logAllStatistics(sort: StatisticsSortProperty = StatisticsSortProperty.TaskName)

}