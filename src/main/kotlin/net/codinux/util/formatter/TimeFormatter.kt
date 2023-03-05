package net.codinux.util.formatter

import java.time.Duration

interface TimeFormatter {

    fun format(duration: Duration): String

}