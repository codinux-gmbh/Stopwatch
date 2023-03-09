package net.codinux.util.statistics

import net.codinux.util.formatter.TimeFormatter
import net.codinux.util.output.MessagePrinter
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread


open class DefaultTaskStatisticsCollector(
  private val printer: MessagePrinter,
  private val timeFormatter: TimeFormatter
) : TaskStatisticsCollector {

  protected open val stats: MutableMap<String, MutableList<Duration>> = ConcurrentHashMap()

  init {
    Runtime.getRuntime().addShutdownHook(thread(start = false, name = "Shutdown Hook") {
      printAllStatistics()
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

  override fun printAllStatistics() {
    stats.keys.sorted().forEach { task -> printStatistics(task) }
  }

  override fun printStatistics(task: String) {
    getStatisticsFor(task)?.let { taskStats ->
      printer.info(
        "$task [${taskStats.countMeasurements}]: " +
          "min ${timeFormatter.format(taskStats.min)}, " +
          "avg ${timeFormatter.format(taskStats.average)}, " +
          "max ${timeFormatter.format(taskStats.max)}, " +
          "total ${timeFormatter.format(taskStats.total)}"
      )
    }
  }

}