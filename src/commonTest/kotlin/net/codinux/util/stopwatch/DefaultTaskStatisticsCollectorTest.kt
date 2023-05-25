package net.codinux.util.stopwatch

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

import kotlin.time.Duration.Companion.milliseconds

class DefaultTaskStatisticsCollectorTest {

  @Test
  fun addElapsedTime() = runTest {
    val task = "My task"
    Stopwatch.logDuration(task, true) { }
    Stopwatch.logDuration(task, true, true) { wait() }
    Stopwatch.logDuration(task, true) { wait() }
    Stopwatch.logDuration(task, true, true) { wait() }
  }

  private suspend fun wait() {
    delay(1.milliseconds)
  }
}