package net.codinux.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration

class DefaultTimeFormatterTest {
    
    private val underTest = DefaultTimeFormatter()


    @Test
    fun formatMilliseconds() {
        val elapsed = Duration.ofNanos(123456 * 1000)

        val result = underTest.format(elapsed)

        assertThat(result).isEqualTo("123.456 ms")
    }

    @Test
    fun formatLessThan10Milliseconds() {
        val elapsed = Duration.ofNanos(1234 * 1000)

        val result = underTest.format(elapsed)

        assertThat(result).isEqualTo("01.234 ms")
    }

    @Test
    fun formatSeconds() {
        val elapsed = Duration.ofSeconds(12, 345 * 1000 * 1000L)

        val result = underTest.format(elapsed)

        assertThat(result).isEqualTo("12.345 s")
    }

    @Test
    fun formatLessThan10Seconds() {
        val elapsed = Duration.ofSeconds(1, 234 * 1000 * 1000L)

        val result = underTest.format(elapsed)

        assertThat(result).isEqualTo("01.234 s")
    }

    @Test
    fun formatMinutes() {
        val elapsed = Duration.ofSeconds(12 * 60 + 34, 567 * 1000 * 1000L)

        val result = underTest.format(elapsed)

        assertThat(result).isEqualTo("12:34.567 min")
    }

    @Test
    fun formatLessThen10Minutes() {
        val elapsed = Duration.ofSeconds(60 + 23, 456 * 1000 * 1000L)

        val result = underTest.format(elapsed)

        assertThat(result).isEqualTo("01:23.456 min")
    }
    
}