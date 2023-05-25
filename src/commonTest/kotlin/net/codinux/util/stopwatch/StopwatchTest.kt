package net.codinux.util.stopwatch

import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeLessThan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class StopwatchTest {

    @Test
    fun nanoseconds() = runTest {
        val underTest = Stopwatch()

        wait(1, DurationUnit.NANOSECONDS)

        val elapsed = underTest.stop()
//        elapsed.toNanos().shouldBeLessThan(1000) // most systems have only microseconds resolution, not nanoseconds
        elapsed.inWholeNanoseconds.shouldBeGreaterThan(0)
    }

    @Test
    fun microseconds() = runTest {
        val underTest = Stopwatch()

        wait(1, DurationUnit.MICROSECONDS)

        val elapsed = underTest.stop()
//        elapsed.toNanos().shouldBeLessThan(1000000) // it's not that fast
        elapsed.inWholeNanoseconds.shouldBeGreaterThan(1000)
    }

    @Test
    fun milliseconds() = runTest {
        val underTest = Stopwatch()

        wait(1, DurationUnit.MILLISECONDS)

        val elapsed = underTest.stop()
        elapsed.inWholeMilliseconds.shouldBeLessThan(100)
        elapsed.inWholeMilliseconds.shouldBeGreaterThan(0)
    }

    @Test
    fun seconds() = runTest {
        val underTest = Stopwatch()

        wait(1, DurationUnit.SECONDS)

        val elapsed = underTest.stop()
        elapsed.inWholeSeconds.shouldBeLessThan(100)
        elapsed.inWholeSeconds.shouldBeGreaterThan(0)
    }


    private suspend fun wait(time: Long, unit: DurationUnit) {
        withContext(Dispatchers.Default) {
            delay(time.toDuration(unit))
        }
    }

}