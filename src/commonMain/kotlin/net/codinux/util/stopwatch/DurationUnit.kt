package net.codinux.util.stopwatch

enum class DurationUnit(val nanosecondsFactor: Long) {

    Nanoseconds(1),
    Microseconds(1_000),
    Milliseconds(1_000 * Microseconds.nanosecondsFactor),
    Seconds(1_000 * Milliseconds.nanosecondsFactor),
    Minutes(60 * Seconds.nanosecondsFactor)

}