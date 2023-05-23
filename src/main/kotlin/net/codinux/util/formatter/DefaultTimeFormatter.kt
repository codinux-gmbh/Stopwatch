package net.codinux.util.formatter

import kotlin.time.Duration


open class DefaultTimeFormatter : TimeFormatter {

    override fun format(duration: Duration): String {
        return when {
            duration.inWholeMinutes > 0 -> {
                String.format("%02d:%02d.%03d min", duration.inWholeMinutes, toSecondsPart(duration), toMillisPart(duration))
            }
            duration.inWholeSeconds > 0 -> {
                String.format("%02d.%03d s", duration.inWholeSeconds, toMillisPart(duration))
            }
            duration.inWholeMilliseconds > 0 -> {
                String.format("%02d.%03d ms", duration.inWholeMilliseconds, duration.inWholeMicroseconds % 1000)
            }
            else -> {
                val durationMicroseconds = duration.inWholeMicroseconds
                String.format("%02d.%03d μs", durationMicroseconds, toNanosPart(duration) % 1000)
            }
        }
    }

    open fun toSecondsPart(duration: Duration): Int {
        return (duration.inWholeSeconds % 60L).toInt()
    }

    open fun toMillisPart(duration: Duration): Int {
        return (duration.inWholeMilliseconds % 1000).toInt()
    }

    open fun toNanosPart(duration: Duration): Int {
        return (duration.inWholeNanoseconds % 1000).toInt()
    }

}