package net.codinux.util

import java.time.Duration


interface ElapsedTimeStatisticsPrinter {

  fun addElapsedTime(task: String, elapsed: Duration)

  fun getMeasuredDurationsFor(task: String): List<Duration>?

  fun getStatisticsFor(task: String): TaskStatistics?

  fun printStatistics(task: String)

  fun printAllStatistics()

}