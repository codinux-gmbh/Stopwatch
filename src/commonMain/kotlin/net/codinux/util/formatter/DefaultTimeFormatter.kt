package net.codinux.util.formatter

import kotlin.time.Duration


open class DefaultTimeFormatter : TimeFormatter {

    override fun format(duration: Duration): String {
        return when {
            duration.inWholeMinutes > 0 -> {
                "${minPlaces(duration.inWholeMinutes, 2)}:${minPlaces(toSecondsPart(duration), 2)}.${minPlaces(toMillisPart(duration), 3)} min"
            }
            duration.inWholeSeconds > 0 -> {
                "${minPlaces(duration.inWholeSeconds, 2)}.${minPlaces(toMillisPart(duration), 3)} s"
            }
            duration.inWholeMilliseconds > 0 -> {
                "${minPlaces(duration.inWholeMilliseconds, 2)}.${minPlaces(duration.inWholeMicroseconds % 1000, 3)} ms"
            }
            else -> {
                val durationMicroseconds = duration.inWholeMicroseconds
                "${minPlaces(durationMicroseconds, 2)}.${minPlaces(toNanosPart(duration) % 1000, 3)} μs"
            }
        }
    }

    open fun toSecondsPart(duration: Duration): Long {
        return duration.inWholeSeconds % 60
    }

    open fun toMillisPart(duration: Duration): Long {
        return duration.inWholeMilliseconds % 1000
    }

    open fun toNanosPart(duration: Duration): Long {
        return duration.inWholeNanoseconds % 1000
    }

    protected open fun minPlaces(number: Long, length: Int) = number.toString().padStart(length, '0')

}