package net.codinux.util

import net.codinux.util.formatter.DefaultTimeFormatter
import net.codinux.util.formatter.TimeFormatter
import net.codinux.util.output.MessagePrinter
import net.codinux.util.output.Slf4jOrSystemOutMessagePrinter
import net.codinux.util.statistics.DefaultElapsedTimeStatisticsPrinter
import net.codinux.util.statistics.ElapsedTimeStatisticsPrinter
import java.time.Duration
import java.util.concurrent.TimeUnit


open class Stopwatch constructor(
    createStarted: Boolean = true,
    protected open val printer: MessagePrinter = DefaultPrinter,
    protected open val timeFormatter: TimeFormatter = DefaultTimeFormatter,
    protected open val statisticsPrinter: ElapsedTimeStatisticsPrinter = DefaultStatisticsPrinter
) {

    // overload for programming languages that don't support default parameters
    constructor(createStarted: Boolean) : this(createStarted, DefaultPrinter)

    companion object {

        @JvmStatic
        var DefaultTimeFormatter: TimeFormatter = DefaultTimeFormatter()

        @JvmStatic
        var DefaultPrinter: MessagePrinter = Slf4jOrSystemOutMessagePrinter()

        @JvmStatic
        var DefaultStatisticsPrinter: ElapsedTimeStatisticsPrinter = DefaultElapsedTimeStatisticsPrinter(DefaultPrinter, DefaultTimeFormatter)

        const val DefaultAddToStatistics = false

        const val DefaultPrintStatisticsNow = false


        @JvmStatic
        inline fun measureDuration(task: Runnable): Duration {
            return measureDuration { task.run() }
        }

        @JvmStatic
        inline fun measureDuration(task: () -> Unit): Duration {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stop()
        }


        @JvmStatic
        inline fun formatDuration(task: Runnable): String {
            return formatDuration { task.run() }
        }

        @JvmStatic
        inline fun formatDuration(task: () -> Unit): String {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stopAndFormat()
        }


        @JvmStatic
        // overload for programming languages that don't support default parameters
        inline fun logDuration(taskName: String, task: Runnable) =
            logDuration(taskName, DefaultAddToStatistics, DefaultPrintStatisticsNow, task)

        @JvmStatic
        inline fun logDuration(taskName: String, addToStatistics: Boolean = DefaultAddToStatistics, printStatisticsNow: Boolean = DefaultPrintStatisticsNow, task: Runnable) =
            logDuration(taskName, addToStatistics, printStatisticsNow) { task.run() }

        @JvmStatic
        // overload for programming languages that don't support default parameters
        inline fun <T> logDuration(taskName: String, task: () -> T): T =
            logDuration(taskName, DefaultAddToStatistics, DefaultPrintStatisticsNow, task)

        @JvmStatic
        inline fun <T> logDuration(taskName: String, addToStatistics: Boolean = DefaultAddToStatistics, printStatisticsNow: Boolean = DefaultPrintStatisticsNow, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(taskName, addToStatistics, printStatisticsNow)

            return result
        }


        suspend inline fun measureDurationAsync(task: suspend () -> Unit): Duration {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stop()
        }

        suspend inline fun <T> logDurationAsync(taskName: String, addToStatistics: Boolean = DefaultAddToStatistics, printStatisticsNow: Boolean = DefaultPrintStatisticsNow, task: suspend () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(taskName, addToStatistics, printStatisticsNow)

            return result
        }


        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        @JvmStatic
        // overload for programming languages that don't support default parameters
        inline fun measureAndToStatistics(taskName: String, task: Runnable) =
            measureAndToStatistics(taskName, DefaultPrintStatisticsNow, task)

        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        @JvmStatic
        inline fun measureAndToStatistics(taskName: String, printStatisticsNow: Boolean = DefaultPrintStatisticsNow, task: Runnable) =
            measureAndToStatistics(taskName, printStatisticsNow) { task.run() }

        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        @JvmStatic
        // overload for programming languages that don't support default parameters
        inline fun <T> measureAndToStatistics(taskName: String, task: () -> T) =
            measureAndToStatistics(taskName, DefaultPrintStatisticsNow, task)

        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        @JvmStatic
        inline fun <T> measureAndToStatistics(taskName: String, printStatisticsNow: Boolean = DefaultPrintStatisticsNow, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndAddToStatistics(taskName, printStatisticsNow)

            return result
        }

        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        suspend inline fun <T> measureAndToStatisticsAsync(taskName: String, printStatisticsNow: Boolean = DefaultPrintStatisticsNow, task: suspend () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndAddToStatistics(taskName, printStatisticsNow)

            return result
        }

        fun printStatistics(task: String) = DefaultStatisticsPrinter.printStatistics(task)

        fun printAllStatistics() = DefaultStatisticsPrinter.printAllStatistics()
    }


    /**
     * Returns if the stopwatch currently is running (or if it's in stopped state).
     */
    open var isRunning = false
        protected set

    protected var startedAt = 0L

    protected var elapsedDurationWhenStopped: Duration? = null

    /**
     * Returns the elapsed time as [Duration].
     * If another time unit is desired see [elapsedNanos] and [getElapsed].
     */
    open val elapsed: Duration
        get() {
            elapsedDurationWhenStopped?.let { return it }

            if (isRunning) {
                return calculateDuration()
            }

            return Duration.ofNanos(0) // not running and never started
        }

    /**
     * Returns the elapsed time in nanoseconds.
     */
    open val elapsedNanos: Long
        get() = elapsed.toNanos()


    init {
        if (createStarted) {
            start()
        }
    }


    /**
     * Starts the stopwatch.
     */
    open fun start() {
        elapsedDurationWhenStopped = null

        isRunning = true

        startedAt = System.nanoTime()
    }

    /**
     * Stops the stopwatch.
     */
    open fun stop(): Duration {
        if (isRunning) {
            elapsedDurationWhenStopped = calculateDuration()

            isRunning = false
        }

        return elapsed
    }

    /**
     * Stops the stopwatch and returns its elapsed time formatted by [TimeFormatter] passed to constructor.
     */
    open fun stopAndFormat(): String {
        stop()

        return formatElapsedTime()
    }

    /**
     * Stops the stopwatch and logs the elapsed time formatted to [printer] in format: "<task> <formatted_duration>".
     */
    // overload for programming languages that don't support default parameters
    open fun stopAndLog(task: String): Duration =
        stopAndLog(task, DefaultAddToStatistics)

    /**
     * Stops the stopwatch and logs the elapsed time formatted to [printer] in format: "<task> <formatted_duration>".
     */
    open fun stopAndLog(task: String, addToStatistics: Boolean = DefaultAddToStatistics, printStatisticsNow: Boolean = DefaultPrintStatisticsNow): Duration {
        stop()

        logElapsedTime(task, addToStatistics, printStatisticsNow)

        return elapsed
    }


    /**
     * Returns the elapsed time in a desired time unit.
     */
    open fun getElapsed(desiredUnit: TimeUnit): Long {
        return desiredUnit.convert(elapsedNanos, TimeUnit.NANOSECONDS)
    }

    protected open fun calculateDuration(): Duration {
        val stoppedAt = System.nanoTime()

        return Duration.ofNanos(stoppedAt - startedAt)
    }


    /**
     * Returns the elapsed time formatted by [TimeFormatter] passed to constructor.
     */
    open fun formatElapsedTime(): String {
        return timeFormatter.format(elapsed)
    }

    /**
     * Logs the elapsed time formatted to [printer] in format: "<task> <formatted_duration>".
     */
    // overload for programming languages that don't support default parameters
    open fun logElapsedTime(task: String) =
        logElapsedTime(task, DefaultAddToStatistics)

    /**
     * Logs the elapsed time formatted to [printer] in format: "<task> <formatted_duration>".
     */
    open fun logElapsedTime(task: String, addToStatistics: Boolean = DefaultAddToStatistics, printStatisticsNow: Boolean = DefaultPrintStatisticsNow) {
        val formattedElapsedTime = formatElapsedTime()

        printer.info("$task took $formattedElapsedTime")

        if (addToStatistics) {
            addToStatistics(task, elapsed)
        }

        if (printStatisticsNow) {
            printStatistics(task)
        }
    }

    open fun stopAndAddToStatistics(task: String, printStatisticsNow: Boolean = DefaultPrintStatisticsNow) {
        val elapsed = stop()

        addToStatistics(task, elapsed)

        if (printStatisticsNow) {
            printStatistics(task)
        }
    }

    protected open fun addToStatistics(task: String, elapsed: Duration) {
        statisticsPrinter.addElapsedTime(task, elapsed)
    }

    open fun printStatistics(task: String) {
        statisticsPrinter.printStatistics(task)
    }


    override fun toString(): String {
        if (isRunning) {
            return "Running, ${formatElapsedTime()} elapsed"
        }

        return "Stopped, ${formatElapsedTime()} elapsed"
    }

}