package net.codinux.util

import org.slf4j.Logger
import java.time.Duration


interface ElapsedTimeStatisticsPrinter {

  val defaultLogger: Logger

  val defaultTimeFormatter: TimeFormatter

  fun addElapsedTime(action: String, elapsed: Duration)

  fun printStatistics(action: String) = printStatistics(action, defaultLogger)

  fun printStatistics(action: String, logger: Logger = defaultLogger) = printStatistics(action, logger, defaultTimeFormatter)

  fun printStatistics(action: String, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter)

  fun printAllStatistics() = printAllStatistics(defaultLogger)

  fun printAllStatistics(logger: Logger = defaultLogger) = printAllStatistics(logger, defaultTimeFormatter)

  fun printAllStatistics(logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter)

}