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
        val elapsed = 123456.microseconds

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("123.456 ms")
    }

    @Test
    fun formatLessThan10Milliseconds() {
        val elapsed = 1234.microseconds

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("01.234 ms")
    }

    @Test
    fun formatSeconds() {
        val elapsed = 12.seconds.plus(345.milliseconds)

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("12.345 s")
    }

    @Test
    fun formatLessThan10Seconds() {
        val elapsed = 1234.milliseconds

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("01.234 s")
    }

    @Test
    fun formatMinutes() {
        val elapsed = 12.minutes.plus(34.seconds).plus(567.milliseconds)

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("12:34.567 min")
    }

    @Test
    fun formatLessThen10Minutes() {
        val elapsed = (60 + 23).seconds.plus(456.milliseconds)

        val result = underTest.format(elapsed)

        result.shouldBeEqualComparingTo("01:23.456 min")
    }
    
}