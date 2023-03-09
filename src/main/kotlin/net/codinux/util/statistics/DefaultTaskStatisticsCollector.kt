package net.codinux.util.statistics

import net.codinux.util.formatter.TimeFormatter
import net.codinux.util.output.MessageLogger
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread


open class DefaultTaskStatisticsCollector(
  private val logger: MessageLogger,
  private val timeFormatter: TimeFormatter
) : TaskStatisticsCollector {

  protected open val stats: MutableMap<String, MutableList<Duration>> = ConcurrentHashMap()

  init {
    Runtime.getRuntime().addShutdownHook(thread(start = false, name = "Shutdown Hook") {
      logAllStatistics()
    })
  }


  override fun addElapsedTime(task: String, elapsed: Duration) {
    stats.getOrPut(task, { CopyOnWriteArrayList() } ).add(elapsed)
  }

  override fun getMeasuredDurationsFor(task: String): List<Duration>? {
    val taskStats = stats[task]

    return if (taskStats.isNullOrEmpty()) {
      null
    } else {
      ArrayList(taskStats) // don't pass mutable state to the outside
    }
  }

  override fun getStatisticsFor(task: String): TaskStatistics? {
    return getMeasuredDurationsFor(task)?.let { measurements ->
      val min = measurements.minOrNull()!!
      val max = measurements.maxOrNull()!!
      val average = measurements.map { it.toNanos() }.average().let { Duration.ofNanos(it.toLong()) }
      val total = measurements.fold(Duration.ZERO) { acc, duration -> acc + duration }

      TaskStatistics(measurements, min, max, average, total)
    }
  }

  override fun logAllStatistics() {
    stats.keys.sorted().forEach { task -> logStatistics(task) }
  }

  override fun logStatistics(task: String) {
    getStatisticsFor(task)?.let { taskStats ->
      logger.info(
        "$task [${taskStats.countMeasurements}]: " +
          "min ${timeFormatter.format(taskStats.min)}, " +
          "avg ${timeFormatter.format(taskStats.average)}, " +
          "max ${timeFormatter.format(taskStats.max)}, " +
          "total ${timeFormatter.format(taskStats.total)}"
      )
    }
  }

}