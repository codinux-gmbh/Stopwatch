package net.codinux.util

import java.time.Duration


open class DefaultTimeFormatter : TimeFormatter {

    override fun format(duration: Duration): String {
        return when {
            duration.toMinutes() > 0 -> {
                String.format("%02d:%02d.%03d min", duration.toMinutes(), duration.toSecondsPart(), duration.toMillisPart())
            }
            duration.toSeconds() > 0 -> {
                String.format("%02d.%03d s", duration.toSeconds(), duration.toMillisPart())
            }
            else -> {
                val durationMicroseconds = duration.toNanos() / 1000
                String.format("%02d.%03d ms", duration.toMillis(), (durationMicroseconds % 1000))
            }
        }
    }

}