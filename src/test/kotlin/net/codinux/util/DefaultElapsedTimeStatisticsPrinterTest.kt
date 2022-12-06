package net.codinux.util

import org.junit.jupiter.api.Test

import java.util.concurrent.TimeUnit

class DefaultElapsedTimeStatisticsPrinterTest {

  @Test
  fun addElapsedTime() {
    val action = "test"
    Stopwatch.logDuration(action, true) { }
    Stopwatch.logDuration(action, true, true) { TimeUnit.MICROSECONDS.sleep(1) }
    Stopwatch.logDuration(action, true) { TimeUnit.MICROSECONDS.sleep(1) }
    Stopwatch.logDuration(action, true, true) { TimeUnit.MICROSECONDS.sleep(1) }
  }
}