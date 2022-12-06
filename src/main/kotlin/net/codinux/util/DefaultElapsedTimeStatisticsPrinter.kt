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


  override fun addElapsedTime(task: String, elapsed: Duration) {
    stats.getOrPut(task, { CopyOnWriteArrayList() } ).add(elapsed)
  }

  override fun printAllStatistics(logger: Logger, timeFormatter: TimeFormatter) {
    stats.keys.sorted().forEach { task -> printStatistics(task, logger, timeFormatter) }
  }

  override fun printStatistics(task: String, logger: Logger, timeFormatter: TimeFormatter) {
    val taskStats = stats[task]
    if (taskStats.isNullOrEmpty()) {
      logger.warn("No statistics found for task '$task'")
    } else {
      val min = taskStats.minOrNull()!!
      val max = taskStats.maxOrNull()!!
      val average = taskStats.map { it.toNanos() }.average().let { Duration.ofNanos(it.toLong()) }
      val total = taskStats.sumOf { it.toNanos() }.let { Duration.ofNanos(it) }

      logger.info("$task [${taskStats.size}]: min ${timeFormatter.format(min)}, avg ${timeFormatter.format(average)}, max ${timeFormatter.format(max)}, total ${timeFormatter.format(total)}")
    }
  }

}