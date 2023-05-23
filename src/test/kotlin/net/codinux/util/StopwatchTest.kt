package net.codinux.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class StopwatchTest {

    @Test
    fun nanoseconds() {
        val underTest = Stopwatch()

        wait(1, TimeUnit.NANOSECONDS)

        val elapsed = underTest.stop()
//        assertThat(elapsed.toNanos()).isLessThan(1000) // most systems have only microseconds resolution, not nanoseconds
        assertThat(elapsed.inWholeNanoseconds).isGreaterThan(0)
    }

    @Test
    fun microseconds() {
        val underTest = Stopwatch()

        wait(1, TimeUnit.MICROSECONDS)

        val elapsed = underTest.stop()
//        assertThat(elapsed.toNanos()).isLessThan(1000000) // it's not that fast
        assertThat(elapsed.inWholeNanoseconds).isGreaterThan(1000)
    }

    @Test
    fun milliseconds() {
        val underTest = Stopwatch()

        wait(1, TimeUnit.MILLISECONDS)

        val elapsed = underTest.stop()
        assertThat(elapsed.inWholeMilliseconds).isLessThan(100)
        assertThat(elapsed.inWholeMilliseconds).isGreaterThan(0)
    }

    @Test
    fun seconds() {
        val underTest = Stopwatch()

        wait(1, TimeUnit.SECONDS)

        val elapsed = underTest.stop()
        assertThat(elapsed.inWholeSeconds).isLessThan(100)
        assertThat(elapsed.inWholeSeconds).isGreaterThan(0)
    }


    private fun wait(time: Long, unit: TimeUnit) {
        unit.sleep(time)
    }

}