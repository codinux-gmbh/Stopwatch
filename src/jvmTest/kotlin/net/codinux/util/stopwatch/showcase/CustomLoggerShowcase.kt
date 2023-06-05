package net.codinux.util.stopwatch.showcase

import net.codinux.util.stopwatch.Stopwatch
import net.codinux.util.stopwatch.output.MessageLogger
import java.util.concurrent.TimeUnit

fun main() {
    CustomLoggerShowcase().runShowcase()
}

class CustomMessageLogger : MessageLogger {

    override fun info(message: String) {
        // implement your custom logging here
        System.err.println(message)
    }

}

class CustomLoggerShowcase {

    fun runShowcase() {
        // set default message logger for all instances:
        Stopwatch.DefaultLogger = CustomMessageLogger()

        // now CustomMessageLogger gets used:
        Stopwatch.logDuration("Heavy calculation") { heavyCalculation() }

        // or set the custom message logger only for a specific Stopwatch instance
        val stopwatch = Stopwatch(logger = CustomMessageLogger())
        heavyCalculation()
        stopwatch.logElapsedTime("Heavy calculation")
    }

    private fun heavyCalculation() {
        // mimic heavy calculation
        TimeUnit.MILLISECONDS.sleep(300)
    }
}