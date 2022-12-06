package net.codinux.util

import net.codinux.util.output.MessagePrinter
import java.time.Duration


interface ElapsedTimeStatisticsPrinter {

  val defaultPrinter: MessagePrinter

  val defaultTimeFormatter: TimeFormatter

  fun addElapsedTime(task: String, elapsed: Duration)

  fun printStatistics(task: String) = printStatistics(task, defaultPrinter)

  fun printStatistics(task: String, printer: MessagePrinter = defaultPrinter) = printStatistics(task, printer, defaultTimeFormatter)

  fun printStatistics(task: String, printer: MessagePrinter = defaultPrinter, timeFormatter: TimeFormatter = defaultTimeFormatter)

  fun printAllStatistics() = printAllStatistics(defaultPrinter)

  fun printAllStatistics(printer: MessagePrinter = defaultPrinter) = printAllStatistics(printer, defaultTimeFormatter)

  fun printAllStatistics(printer: MessagePrinter = defaultPrinter, timeFormatter: TimeFormatter = defaultTimeFormatter)

}