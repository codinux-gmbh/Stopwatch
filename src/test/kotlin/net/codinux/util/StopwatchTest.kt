package net.codinux.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.TimeUnit

class StopwatchTest {

    @Test
    fun nanoseconds() {
        val underTest = Stopwatch()

        wait(1, TimeUnit.NANOSECONDS)

        val elapsed = underTest.stop()
//        assertThat(elapsed.toNanos()).isLessThan(1000) // it's not that fast
        assertThat(elapsed.toNanos()).isGreaterThan(0)
    }

    @Test
    fun microseconds() {
        val underTest = Stopwatch()

        wait(1, TimeUnit.MICROSECONDS)

        val elapsed = underTest.stop()
//        assertThat(elapsed.toNanos()).isLessThan(1000000) // it's not that fast
        assertThat(elapsed.toNanos()).isGreaterThan(1000)
    }

    @Test
    fun milliseconds() {
        val underTest = Stopwatch()

        wait(1, TimeUnit.MILLISECONDS)

        val elapsed = underTest.stop()
        assertThat(elapsed.toMillis()).isLessThan(100)
        assertThat(elapsed.toMillis()).isGreaterThan(0)
    }

    @Test
    fun seconds() {
        val underTest = Stopwatch()

        wait(1, TimeUnit.SECONDS)

        val elapsed = underTest.stop()
        assertThat(elapsed.toSeconds()).isLessThan(100)
        assertThat(elapsed.toSeconds()).isGreaterThan(0)
    }


    @Test
    fun formatMilliseconds() {
        val elapsed = Duration.ofNanos(123456 * 1000)
        val underTest = Stopwatch()

        val result = underTest.formatElapsedTime(elapsed)

        assertThat(result).isEqualTo("123.456 ms")
    }

    @Test
    fun formatLessThan10Milliseconds() {
        val elapsed = Duration.ofNanos(1234 * 1000)
        val underTest = Stopwatch()

        val result = underTest.formatElapsedTime(elapsed)

        assertThat(result).isEqualTo("01.234 ms")
    }

    @Test
    fun formatSeconds() {
        val elapsed = Duration.ofSeconds(12, 345 * 1000 * 1000L)
        val underTest = Stopwatch()

        val result = underTest.formatElapsedTime(elapsed)

        assertThat(result).isEqualTo("12.345 s")
    }

    @Test
    fun formatLessThan10Seconds() {
        val elapsed = Duration.ofSeconds(1, 234 * 1000 * 1000L)
        val underTest = Stopwatch()

        val result = underTest.formatElapsedTime(elapsed)

        assertThat(result).isEqualTo("01.234 s")
    }

    @Test
    fun formatMinutes() {
        val elapsed = Duration.ofSeconds(12 * 60 + 34, 567 * 1000 * 1000L)
        val underTest = Stopwatch()

        val result = underTest.formatElapsedTime(elapsed)

        assertThat(result).isEqualTo("12:34.567 min")
    }

    @Test
    fun formatLessThen10Minutes() {
        val elapsed = Duration.ofSeconds(60 + 23, 456 * 1000 * 1000L)
        val underTest = Stopwatch()

        val result = underTest.formatElapsedTime(elapsed)

        assertThat(result).isEqualTo("01:23.456 min")
    }


    private fun wait(time: Long, unit: TimeUnit) {
        unit.sleep(time)
    }

}