package net.codinux.util

import org.slf4j.Logger
import java.time.Duration


interface ElapsedTimeStatisticsPrinter {

  val defaultLogger: Logger

  val defaultTimeFormatter: TimeFormatter

  fun addElapsedTime(task: String, elapsed: Duration)

  fun printStatistics(task: String) = printStatistics(task, defaultLogger)

  fun printStatistics(task: String, logger: Logger = defaultLogger) = printStatistics(task, logger, defaultTimeFormatter)

  fun printStatistics(task: String, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter)

  fun printAllStatistics() = printAllStatistics(defaultLogger)

  fun printAllStatistics(logger: Logger = defaultLogger) = printAllStatistics(logger, defaultTimeFormatter)

  fun printAllStatistics(logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter)

}