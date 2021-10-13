package net.codinux.util

import java.time.Duration

interface TimeFormatter {

    fun format(duration: Duration): String

}