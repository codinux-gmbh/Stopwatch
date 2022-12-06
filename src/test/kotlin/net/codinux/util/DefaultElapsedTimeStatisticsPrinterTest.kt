package net.codinux.util

import org.junit.jupiter.api.Test

import java.util.concurrent.TimeUnit

class DefaultElapsedTimeStatisticsPrinterTest {

  @Test
  fun addElapsedTime() {
    val task = "My task"
    Stopwatch.logDuration(task, true) { }
    Stopwatch.logDuration(task, true, true) { TimeUnit.MICROSECONDS.sleep(1) }
    Stopwatch.logDuration(task, true) { TimeUnit.MICROSECONDS.sleep(1) }
    Stopwatch.logDuration(task, true, true) { TimeUnit.MICROSECONDS.sleep(1) }
  }
}