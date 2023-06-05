package net.codinux.util.stopwatch

import net.codinux.util.stopwatch.formatter.DefaultTimeFormatter

class Duration(nanoseconds: Long) {

    constructor(valueInUnit: Long, unit: DurationUnit) : this(valueInUnit * unit.nanosecondsFactor)

    companion object {
        val Zero = Duration(0)
    }


    val inWholeMinutes by lazy { inWholeSeconds / 60 }

    val inWholeSeconds by lazy { inWholeMilliseconds / 1_000 }
    val secondsPart by lazy { inWholeSeconds % 60 }

    val inWholeMilliseconds by lazy { inWholeMicroseconds / 1_000 }
    val millisecondsPart by lazy { inWholeMilliseconds % 1_000 }

    val inWholeMicroseconds by lazy { inWholeNanoseconds / 1_000 }
    val microsecondsPart by lazy { inWholeMicroseconds % 1_000 }

    val inWholeNanoseconds = nanoseconds
    val nanosecondsPart by lazy { inWholeNanoseconds % 1_000 }

    fun toLong(unit: DurationUnit) = inWholeNanoseconds / unit.nanosecondsFactor

    operator fun plus(other: Duration): Duration {
        return Duration(this.inWholeNanoseconds + other.inWholeNanoseconds)
    }


    override fun toString(): String {
        return DefaultTimeFormatter().format(this)
    }

}


internal fun Long.toDuration() = Duration(this)