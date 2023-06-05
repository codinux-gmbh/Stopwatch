package net.codinux.util.stopwatch.formatter

import net.codinux.util.stopwatch.Duration

interface TimeFormatter {

    fun format(duration: Duration): String

}