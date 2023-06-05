package net.codinux.util.stopwatch

import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import net.codinux.util.stopwatch.formatter.DefaultTimeFormatter
import kotlin.test.Test
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DefaultTimeFormatterTest {
    
    private val underTest = DefaultTimeFormatter()


    @Test
    fun formatMilliseconds() {
        val elapsed = Duration(123456, DurationUnit.Microseconds)

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("123.456 ms")
    }

    @Test
    fun formatLessThan10Milliseconds() {
        val elapsed = Duration(1234, DurationUnit.Microseconds)

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("01.234 ms")
    }

    @Test
    fun formatSeconds() {
        val elapsed = Duration(12_345, DurationUnit.Milliseconds)

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("12.345 s")
    }

    @Test
    fun formatLessThan10Seconds() {
        val elapsed = Duration(1234, DurationUnit.Milliseconds)

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("01.234 s")
    }

    @Test
    fun formatMinutes() {
        val elapsed = Duration(12, DurationUnit.Minutes) + Duration(34, DurationUnit.Seconds) + Duration(567, DurationUnit.Milliseconds)

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("12:34.567 min")
    }

    @Test
    fun formatLessThen10Minutes() {
        val elapsed = Duration(60 + 23, DurationUnit.Seconds) + Duration(456, DurationUnit.Milliseconds)

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("01:23.456 min")
    }
    
}