package net.codinux.util.stopwatch.formatter

import kotlin.time.Duration

interface TimeFormatter {

    fun format(duration: Duration): String

}