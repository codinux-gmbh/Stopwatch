package net.codinux.util

import org.slf4j.Logger
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread


open class ElapsedTimeStatisticsPrinter(
  open val defaultLogger: Logger,
  open val defaultTimeFormatter: TimeFormatter
) {

  protected open val stats: MutableMap<String, MutableList<Duration>> = ConcurrentHashMap()

  init {
    Runtime.getRuntime().addShutdownHook(thread(start = false, name = "Shutdown Hook") {
      printAllStatistics()
    })
  }


  open fun addElapsedTime(action: String, elapsed: Duration) {
    stats.getOrPut(action, { CopyOnWriteArrayList() } ).add(elapsed)
  }

  @JvmOverloads
  open fun printAllStatistics(logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter) {
    stats.keys.sorted().forEach { action -> printStatistics(action, logger, timeFormatter) }
  }

  @JvmOverloads
  open fun printStatistics(action: String, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter) {
    val actionStats = stats[action]
    if (actionStats.isNullOrEmpty()) {
      logger.warn("No statistics found for action '$action'")
    } else {
      val min = actionStats.minOrNull()!!
      val max = actionStats.maxOrNull()!!
      val average = actionStats.map { it.toNanos() }.average().let { Duration.ofNanos(it.toLong()) }

      logger.info("$action: min ${timeFormatter.format(min)}, avg ${timeFormatter.format(average)}, max ${timeFormatter.format(max)}")
    }
  }

}