package net.codinux.util.showcase

import net.codinux.util.Stopwatch
import net.codinux.util.output.Slf4jMessagePrinter
import java.lang.Exception
import java.util.concurrent.TimeUnit

class KotlinShowcase {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            KotlinShowcase().runShowcase()
        }

        private fun myTask() {
            wait(123, TimeUnit.MILLISECONDS)
        }

        private fun wait(time: Long, unit: TimeUnit) {
            try {
                unit.sleep(time)
            } catch (ignored: Exception) {
            }
        }
    }


    fun runShowcase() {
        showStaticMethods()

        showInstanceMethods()
    }

    private fun showStaticMethods() {
        // returns the elapsed time
        val measuredDuration = Stopwatch.measureDuration { myTask() }

        // returns elapsed time formatted
        val formattedDuration = Stopwatch.formatDuration { myTask() }

        // logs elapsed time to class' slf4j logger in format: "<task> <formatted_duration>"
        Stopwatch.logDuration("My important task") { myTask() } // see console log output

        // of course you can also specify the (slf4j) logger to log to
        Stopwatch.logDuration("Other task", printer = Slf4jMessagePrinter("Task logger")) { myTask() }

        // logs elapsed time and returns task's result
        val heavyCalculationResult = Stopwatch.logDuration("Task that returns a result") {
            myTask() // mimic heavy calculation
            123L // return result to caller
        }
    }

    private fun showInstanceMethods() {
        // returns the elapsed time
        val measureDuration = Stopwatch() // will automatically be created in started state
        myTask()
        val measuredDuration = measureDuration.stop()

        // returns elapsed time formatted
        val formatDuration = Stopwatch()
        myTask()
        val formattedDuration = formatDuration.stopAndFormat()

        // logs elapsed time to class' slf4j logger in format: "<task> <formatted_duration>"
        val logDuration = Stopwatch()
        myTask()
        logDuration.stopAndLog("Heavy calculation task") // see console log output

        // you can also do all above tasks manually
        val notStartedAutomatically = Stopwatch(false) // creates the stopwatch in stopped state -> has to be started manually
        val notStartedDuration = notStartedAutomatically.elapsed // returns a duration of 0 as stopwatch has not been started yet

        notStartedAutomatically.start() // now start the stopwatch manually
        myTask() // mimic heavy calculation

        notStartedAutomatically.stop() // stops the stopwatch manually
        val durationAfterStopping = notStartedAutomatically.elapsed // gets the elapsed time in java.time.Duration
        val durationInNanoseconds = notStartedAutomatically.elapsedNanos // gets the elapsed time in nanoseconds
        val durationMillis = notStartedAutomatically.getElapsed(TimeUnit.MILLISECONDS) // gets the elapsed time in a desired time unit, milliseconds in this case
    }

}