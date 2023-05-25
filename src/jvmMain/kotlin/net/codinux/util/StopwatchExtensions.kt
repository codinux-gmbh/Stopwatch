@file:JvmName("StopwatchKt")

package net.codinux.util

import net.codinux.util.Stopwatch.Companion.logStatistics
import net.codinux.util.statistics.TaskStatisticsCollector
import java.time.Duration
import java.util.function.Supplier
import kotlin.jvm.JvmName

val Stopwatch.elapsedDuration: Duration
  get() = Duration.ofNanos(this.elapsedNanos)

// methods to make a nicer API for Java

fun measureDuration(task: Runnable): Duration = Duration.ofNanos(Stopwatch.measureDuration { task.run() }.inWholeNanoseconds)

fun formatDuration(task: Runnable): String {
  return Stopwatch.formatDuration { task.run() }
}

// overload for programming languages that don't support default parameters
fun logDuration(taskName: String, task: Runnable) =
  logDuration(taskName, Stopwatch.DefaultAddToStatistics, Stopwatch.DefaultLogStatisticsNow, task)

fun logDuration(taskName: String, addToStatistics: Boolean = Stopwatch.DefaultAddToStatistics, logStatisticsNow: Boolean = Stopwatch.DefaultLogStatisticsNow, task: Runnable) =
  Stopwatch.logDuration(taskName, addToStatistics, logStatisticsNow) { task.run() }

// overload for programming languages that don't support default parameters
fun <T> logDuration(taskName: String, task: Supplier<T>) =
  logDuration(taskName, Stopwatch.DefaultAddToStatistics, Stopwatch.DefaultLogStatisticsNow, task)

fun <T> logDuration(taskName: String, addToStatistics: Boolean = Stopwatch.DefaultAddToStatistics, logStatisticsNow: Boolean = Stopwatch.DefaultLogStatisticsNow, task: Supplier<T>) =
  Stopwatch.logDuration(taskName, addToStatistics, logStatisticsNow) { task.get() }

/**
 * Adds the elapsed time only to [TaskStatisticsCollector] but doesn't log it.
 * Set [logStatisticsNow] to true to log task statistics now. Otherwise statistics will be logged when JVM shuts down or by a call to [logStatistics].
 */
// overload for programming languages that don't support default parameters
fun measureAndToStatistics(taskName: String, task: Runnable) =
  measureAndToStatistics(taskName, Stopwatch.DefaultLogStatisticsNow, task)

/**
 * Adds the elapsed time only to [TaskStatisticsCollector] but doesn't log it.
 * Set [logStatisticsNow] to true to log task statistics now. Otherwise statistics will be logged when JVM shuts down or by a call to [logStatistics].
 */
fun measureAndToStatistics(taskName: String, logStatisticsNow: Boolean = Stopwatch.DefaultLogStatisticsNow, task: Runnable) =
  Stopwatch.measureAndToStatistics(taskName, logStatisticsNow) { task.run() }

fun stopDuration(stopwatch: Stopwatch): Duration = Duration.ofNanos(stopwatch.stopNanos())
