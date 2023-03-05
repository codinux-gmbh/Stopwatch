package net.codinux.util

import java.time.Duration


interface ElapsedTimeStatisticsPrinter {

  fun addElapsedTime(task: String, elapsed: Duration)

  fun printStatistics(task: String)

  fun printAllStatistics()

}