package net.codinux.util.stopwatch.statistics

import net.codinux.util.stopwatch.Duration
import net.codinux.util.stopwatch.collections.ConcurrentList
import net.codinux.util.stopwatch.collections.ConcurrentMap
import net.codinux.util.stopwatch.collections.isNullOrEmpty
import net.codinux.util.stopwatch.formatter.TimeFormatter
import net.codinux.util.stopwatch.output.MessageLogger
import net.codinux.util.stopwatch.toDuration

open class DefaultTaskStatisticsCollector(
  private val logger: MessageLogger,
  private val timeFormatter: TimeFormatter
) : TaskStatisticsCollector {

  protected open val stats: ConcurrentMap<String, ConcurrentList<Duration>> = ConcurrentMap()

  init {
//    Runtime.getRuntime().addShutdownHook(thread(start = false, name = "Shutdown Hook") {
//      logAllStatistics()
//    })
  }


  override fun addElapsedTime(task: String, elapsed: Duration) {
    stats.getOrPut(task, { ConcurrentList() } ).add(elapsed)
  }

  override fun getMeasuredDurationsFor(task: String): List<Duration>? {
    val taskStats = stats.get(task)

    return if (taskStats.isNullOrEmpty()) {
      null
    } else {
      ArrayList(taskStats.asCollection()) // don't pass mutable state to the outside
    }
  }

  override fun getStatisticsFor(task: String): TaskStatistics? {
    return getMeasuredDurationsFor(task)?.let { measurements ->
      val measurementsNanos = measurements.map { it.inWholeNanoseconds }

      val min = measurementsNanos.minOrNull()!!
      val max = measurementsNanos.maxOrNull()!!
      val average = measurementsNanos.average().toLong()
      val total = measurementsNanos.sum()

      TaskStatistics(task, measurements, min.toDuration(), max.toDuration(), average.toDuration(), total.toDuration())
    }
  }

  override fun getAllStatistics(sort: StatisticsSortProperty): List<TaskStatistics> =
    stats.keys.sorted().mapNotNull { getStatisticsFor(it) }
      .sortedWith { a, b -> compare(a, b, sort) }

  override fun logAllStatistics(sort: StatisticsSortProperty) {
    getAllStatistics(sort).forEach { taskStats -> logStatistics(taskStats) }
  }

  override fun logStatistics(task: String) {
    getStatisticsFor(task)?.let { taskStats ->
      logStatistics(taskStats)
    }
  }

  protected open fun logStatistics(taskStats: TaskStatistics) {
    logger.info(
      "${taskStats.task} [${taskStats.countMeasurements}]: " +
          "min ${timeFormatter.format(taskStats.min)}, " +
          "avg ${timeFormatter.format(taskStats.average)}, " +
          "max ${timeFormatter.format(taskStats.max)}, " +
          "total ${timeFormatter.format(taskStats.total)}"
    )
  }


  protected open fun compare(stats1: TaskStatistics, stats2: TaskStatistics, sort: StatisticsSortProperty): Int = when (sort) {
    StatisticsSortProperty.TaskName -> stats1.task.compareTo(stats2.task)
    StatisticsSortProperty.CountMeasurements -> stats2.countMeasurements.compareTo(stats1.countMeasurements)
    StatisticsSortProperty.MinDuration -> stats1.min.compareTo(stats2.min)
    StatisticsSortProperty.AverageDuration -> stats2.average.compareTo(stats1.average)
    StatisticsSortProperty.MaxDuration -> stats2.max.compareTo(stats1.max)
    StatisticsSortProperty.TotalDuration -> stats2.total.compareTo(stats1.total)
  }

}