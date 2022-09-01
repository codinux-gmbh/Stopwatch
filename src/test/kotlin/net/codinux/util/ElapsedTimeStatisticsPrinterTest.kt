package net.codinux.util

import org.junit.jupiter.api.Test

import java.util.concurrent.TimeUnit

class ElapsedTimeStatisticsPrinterTest {

  @Test
  fun addElapsedTime() {
    val action = "test"
    Stopwatch.logDuration(action, true) { }
    Stopwatch.logDuration(action, false, true) { TimeUnit.MICROSECONDS.sleep(1) }
  }
}