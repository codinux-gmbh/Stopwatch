package net.codinux.util.stopwatch.statistics

import net.codinux.util.stopwatch.formatter.TimeFormatter
import net.codinux.util.stopwatch.output.MessageLogger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds


open class DefaultTaskStatisticsCollector(
  private val logger: MessageLogger,
  private val timeFormatter: TimeFormatter
) : TaskStatisticsCollector {

//  protected open val stats: MutableMap<String, MutableList<Duration>> = ConcurrentHashMap()
  protected open val stats: MutableMap<String, MutableList<Duration>> = mutableMapOf()

  init {
//    Runtime.getRuntime().addShutdownHook(thread(start = false, name = "Shutdown Hook") {
//      logAllStatistics()
//    })
  }


  override fun addElapsedTime(task: String, elapsed: Duration) {
//    stats.getOrPut(task, { CopyOnWriteArrayList() } ).add(elapsed)
    stats.getOrPut(task, { mutableListOf() } ).add(elapsed)
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
      val average = measurements.map { it.inWholeNanoseconds }.average().let { it.nanoseconds }
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