package net.codinux.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class StopwatchTest {

    @Test
    fun nanoseconds() {
        val underTest = Stopwatch()

        wait(1, DurationUnit.NANOSECONDS)

        val elapsed = underTest.stop()
//        assertThat(elapsed.toNanos()).isLessThan(1000) // most systems have only microseconds resolution, not nanoseconds
        assertThat(elapsed.inWholeNanoseconds).isGreaterThan(0)
    }

    @Test
    fun microseconds() {
        val underTest = Stopwatch()

        wait(1, DurationUnit.MICROSECONDS)

        val elapsed = underTest.stop()
//        assertThat(elapsed.toNanos()).isLessThan(1000000) // it's not that fast
        assertThat(elapsed.inWholeNanoseconds).isGreaterThan(1000)
    }

    @Test
    fun milliseconds() {
        val underTest = Stopwatch()

        wait(1, DurationUnit.MILLISECONDS)

        val elapsed = underTest.stop()
        assertThat(elapsed.inWholeMilliseconds).isLessThan(100)
        assertThat(elapsed.inWholeMilliseconds).isGreaterThan(0)
    }

    @Test
    fun seconds() {
        val underTest = Stopwatch()

        wait(1, DurationUnit.SECONDS)

        val elapsed = underTest.stop()
        assertThat(elapsed.inWholeSeconds).isLessThan(100)
        assertThat(elapsed.inWholeSeconds).isGreaterThan(0)
    }


    private fun wait(time: Long, unit: DurationUnit) = runBlocking {
        delay(time.toDuration(unit))
    }

}