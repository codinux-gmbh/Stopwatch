package net.codinux.util

import org.junit.jupiter.api.Test

import java.util.concurrent.TimeUnit

class ElapsedTimeStatisticsPrinterTest {

  @Test
  fun addElapsedTime() {
    val action = "test"
    Stopwatch.logDuration(action, true) { TimeUnit.MILLISECONDS.sleep(1) }
    Stopwatch.logDuration(action, false, true) { TimeUnit.MILLISECONDS.sleep(5) }
  }
}