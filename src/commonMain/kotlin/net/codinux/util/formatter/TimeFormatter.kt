package net.codinux.util.formatter

import kotlin.time.Duration

interface TimeFormatter {

    fun format(duration: Duration): String

}