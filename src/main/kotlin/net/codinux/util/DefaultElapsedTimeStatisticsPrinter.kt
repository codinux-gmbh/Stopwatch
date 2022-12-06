package net.codinux.util

import org.slf4j.Logger
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread


open class DefaultElapsedTimeStatisticsPrinter(
  override val defaultLogger: Logger,
  override val defaultTimeFormatter: TimeFormatter
) : ElapsedTimeStatisticsPrinter {

  protected open val stats: MutableMap<String, MutableList<Duration>> = ConcurrentHashMap()

  init {
    Runtime.getRuntime().addShutdownHook(thread(start = false, name = "Shutdown Hook") {
      printAllStatistics()
    })
  }


  override fun addElapsedTime(action: String, elapsed: Duration) {
    stats.getOrPut(action, { CopyOnWriteArrayList() } ).add(elapsed)
  }

  override fun printAllStatistics(logger: Logger, timeFormatter: TimeFormatter) {
    stats.keys.sorted().forEach { action -> printStatistics(action, logger, timeFormatter) }
  }

  override fun printStatistics(action: String, logger: Logger, timeFormatter: TimeFormatter) {
    val actionStats = stats[action]
    if (actionStats.isNullOrEmpty()) {
      logger.warn("No statistics found for action '$action'")
    } else {
      val min = actionStats.minOrNull()!!
      val max = actionStats.maxOrNull()!!
      val average = actionStats.map { it.toNanos() }.average().let { Duration.ofNanos(it.toLong()) }
      val total = actionStats.sumOf { it.toNanos() }.let { Duration.ofNanos(it) }

      logger.info("$action [${actionStats.size}]: min ${timeFormatter.format(min)}, avg ${timeFormatter.format(average)}, max ${timeFormatter.format(max)}, total ${timeFormatter.format(total)}")
    }
  }

}