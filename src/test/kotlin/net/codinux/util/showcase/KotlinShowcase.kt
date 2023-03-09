package net.codinux.util.showcase

import net.codinux.util.Stopwatch
import net.codinux.util.output.Slf4jLogger
import java.lang.Exception
import java.util.concurrent.TimeUnit


fun main() {
    KotlinShowcase().runShowcase()
}

class KotlinShowcase {

    companion object {

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

        showStaticMethodsToLogStatistics()

        showInstanceMethods()
    }

    private fun showStaticMethods() {
        // returns the elapsed time as Duration instance
        val measuredDuration = Stopwatch.measureDuration { myTask() }

        // returns elapsed time as formatted string
        val formattedDuration = Stopwatch.formatDuration { myTask() }

        // logs elapsed time to class' slf4j logger in format: "<task> took <formatted_duration>"
        Stopwatch.logDuration("My important task") { myTask() } // see console log output

        // of course you can also customize the (slf4j) logger to log to
        Stopwatch.DefaultLogger = Slf4jLogger("Task logger")
        Stopwatch.logDuration("Other task") { myTask() }

        // logs elapsed time and returns task's result
        val heavyCalculationResult = Stopwatch.logDuration("Task that returns a result") {
            myTask() // mimic heavy calculation
            "Task result" // return result to caller, heavyCalculationResult will then be `"Task result"`
        }
    }

    private fun showStaticMethodsToLogStatistics() {
        // adds elapsed time to task's statistics and logs it right away
        (1..3).forEach {
            Stopwatch.logDuration("My important task", addToStatistics = true, logStatisticsNow = true) { myTask() }
        }
        // logs something like this:
        // [main] INFO net.codinux.util.Stopwatch - My important task took 500.179 ms
        // [main] INFO net.codinux.util.Stopwatch - My important task [1]: min 500.179 ms, avg 500.179 ms, max 500.179 ms, total 500.179 ms
        // [main] INFO net.codinux.util.Stopwatch - My important task took 500.161 ms
        // [main] INFO net.codinux.util.Stopwatch - My important task [2]: min 500.161 ms, avg 500.170 ms, max 500.179 ms, total 01.000 s
        // [main] INFO net.codinux.util.Stopwatch - My important task took 500.116 ms
        // [main] INFO net.codinux.util.Stopwatch - My important task [3]: min 500.116 ms, avg 500.152 ms, max 500.179 ms, total 01.500 s

        // you can also log the statistics at a defined time:
        (1..10).forEach {
            // only adds the elapsed times to statistics, but doesn't log it
            Stopwatch.measureAndToStatistics("My heavy task") { myTask() }
        }
        Stopwatch.logStatistics("My heavy task") // now log statistics for this task at any time you like. Logs something like this:
        // [main] INFO net.codinux.util.Stopwatch - My heavy task [10]: min 500.126 ms, avg 500.182 ms, max 500.319 ms, total 05.001 s

        // statistics by default are logged when the JVM shuts down. This looks something like this:
        // [Shutdown Hook] INFO net.codinux.util.Stopwatch - My heavy task [10]: min 500.126 ms, avg 500.182 ms, max 500.319 ms, total 05.001 s
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