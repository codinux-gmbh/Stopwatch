package net.codinux.util

import net.codinux.util.output.MessagePrinter
import net.codinux.util.output.Slf4jOrSystemOutMessagePrinter
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


open class Stopwatch @JvmOverloads constructor(
    createStarted: Boolean = true,
    protected open val defaultPrinter: MessagePrinter = DefaultPrinter,
    protected open val defaultTimeFormatter: TimeFormatter = DefaultTimeFormatter,
    protected open val defaultStatisticsPrinter: ElapsedTimeStatisticsPrinter = DefaultStatisticsPrinter
) {

    companion object {

        @JvmStatic
        @get:JvmName("getGlobalDefaultTimeFormatter")
        @set:JvmName("setGlobalDefaultTimeFormatter")
        var DefaultTimeFormatter: TimeFormatter = DefaultTimeFormatter()

        @JvmStatic
        @get:JvmName("getGlobalDefaultPrinter")
        @set:JvmName("setGlobalDefaultPrinter")
        var DefaultPrinter: MessagePrinter = Slf4jOrSystemOutMessagePrinter()

        @JvmStatic
        @get:JvmName("getGlobalDefaultStatisticsPrinter")
        @set:JvmName("setGlobalDefaultStatisticsPrinter")
        var DefaultStatisticsPrinter: ElapsedTimeStatisticsPrinter = DefaultElapsedTimeStatisticsPrinter(DefaultPrinter, DefaultTimeFormatter)


        @JvmStatic
        fun measureDuration(task: Runnable): Duration {
            return measureDuration { task.run() }
        }

        inline fun measureDuration(task: () -> Unit): Duration {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stop()
        }


        @JvmStatic
        @JvmOverloads
        fun formatDuration(timeFormatter: TimeFormatter = DefaultTimeFormatter, task: Runnable): String {
            return formatDuration(timeFormatter) { task.run() }
        }

        inline fun formatDuration(timeFormatter: TimeFormatter = DefaultTimeFormatter, task: () -> Unit): String {
            val stopwatch = Stopwatch(defaultTimeFormatter = timeFormatter)

            task()

            return stopwatch.stopAndFormat()
        }


        @JvmStatic
        @JvmOverloads
        fun logDuration(taskName: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, printer: MessagePrinter = DefaultPrinter, timeFormatter: TimeFormatter = DefaultTimeFormatter, task: Runnable) {
            return logDuration(taskName, addToStatistics, printStatisticsNow, printer, timeFormatter) { task.run() }
        }

        @JvmStatic
        @JvmOverloads
        fun <T> logDuration(taskName: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, printer: MessagePrinter = DefaultPrinter, timeFormatter: TimeFormatter = DefaultTimeFormatter, task: Supplier<T>): T {
            return logDuration(taskName, addToStatistics, printStatisticsNow, printer, timeFormatter) { task.get() }
        }

        inline fun <T> logDuration(taskName: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, printer: MessagePrinter = DefaultPrinter, timeFormatter: TimeFormatter = DefaultTimeFormatter, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(taskName, addToStatistics, printStatisticsNow, printer, timeFormatter)

            return result
        }


        suspend inline fun measureDurationSuspendable(task: suspend () -> Unit): Duration {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stop()
        }

        suspend inline fun <T> logDurationSuspendable(taskName: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, printer: MessagePrinter = DefaultPrinter, timeFormatter: TimeFormatter = DefaultTimeFormatter, task: suspend () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(taskName, addToStatistics, printStatisticsNow, printer, timeFormatter)

            return result
        }


        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        @JvmStatic
        @JvmOverloads
        fun <T> addDurationToStatistics(taskName: String, printStatisticsNow: Boolean = false, statisticsPrinter: ElapsedTimeStatisticsPrinter = DefaultStatisticsPrinter, task: Runnable) {
            return addDurationToStatistics(taskName, printStatisticsNow, statisticsPrinter) { task.run() }
        }

        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        @JvmStatic
        @JvmOverloads
        fun <T> addDurationToStatistics(taskName: String, printStatisticsNow: Boolean = false, statisticsPrinter: ElapsedTimeStatisticsPrinter = DefaultStatisticsPrinter, task: Supplier<T>): T {
            return addDurationToStatistics(taskName, printStatisticsNow, statisticsPrinter) { task.get() }
        }

        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        inline fun <T> addDurationToStatistics(taskName: String, printStatisticsNow: Boolean = false, statisticsPrinter: ElapsedTimeStatisticsPrinter = DefaultStatisticsPrinter, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndAddToStatistics(taskName, printStatisticsNow, statisticsPrinter)

            return result
        }

        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        suspend inline fun <T> addDurationToStatisticsAsync(taskName: String, printStatisticsNow: Boolean = false, statisticsPrinter: ElapsedTimeStatisticsPrinter = DefaultStatisticsPrinter, task: suspend () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndAddToStatistics(taskName, printStatisticsNow, statisticsPrinter)

            return result
        }

        fun printStatistics(task: String, statisticsPrinter: ElapsedTimeStatisticsPrinter = DefaultStatisticsPrinter) {
            statisticsPrinter.printStatistics(task)
        }

        @JvmOverloads
        fun printStatistics(task: String, printer: MessagePrinter = DefaultPrinter, timeFormatter: TimeFormatter = DefaultTimeFormatter) {
            DefaultStatisticsPrinter.printStatistics(task, printer, timeFormatter)
        }
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
    @JvmOverloads
    open fun stopAndFormat(timeFormatter: TimeFormatter = defaultTimeFormatter): String {
        stop()

        return formatElapsedTime(timeFormatter)
    }

    /**
     * Stops the stopwatch and logs the elapsed time formatted to [printer] in format: "<task> <formatted_duration>".
     */
    @JvmOverloads
    open fun stopAndLog(task: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, printer: MessagePrinter = defaultPrinter, timeFormatter: TimeFormatter = defaultTimeFormatter): Duration {
        stop()

        logElapsedTime(task, addToStatistics, printStatisticsNow, printer, timeFormatter)

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
    open fun formatElapsedTime(timeFormatter: TimeFormatter = defaultTimeFormatter): String {
        return timeFormatter.format(elapsed)
    }

    /**
     * Logs the elapsed time formatted to [printer] in format: "<task> <formatted_duration>".
     */
    @JvmOverloads
    open fun logElapsedTime(task: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, printer: MessagePrinter = defaultPrinter, timeFormatter: TimeFormatter = defaultTimeFormatter, statisticsPrinter: ElapsedTimeStatisticsPrinter = defaultStatisticsPrinter) {
        val formattedElapsedTime = formatElapsedTime(timeFormatter)

        printer.info("$task took $formattedElapsedTime")

        if (addToStatistics) {
            addToStatistics(task, elapsed)
        }

        if (printStatisticsNow) {
            printStatistics(task, printer, timeFormatter)
        }
    }

    open fun stopAndAddToStatistics(task: String, printStatisticsNow: Boolean = false, statisticsPrinter: ElapsedTimeStatisticsPrinter = defaultStatisticsPrinter) {
        val elapsed = stop()

        addToStatistics(task, elapsed, statisticsPrinter)

        if (printStatisticsNow) {
            printStatistics(task, statisticsPrinter = statisticsPrinter)
        }
    }

    protected open fun addToStatistics(task: String, elapsed: Duration, statisticsPrinter: ElapsedTimeStatisticsPrinter = defaultStatisticsPrinter) {
        statisticsPrinter.addElapsedTime(task, elapsed)
    }

    @JvmOverloads
    open fun printStatistics(task: String, statisticsPrinter: ElapsedTimeStatisticsPrinter = defaultStatisticsPrinter) {
        statisticsPrinter.printStatistics(task)
    }


    override fun toString(): String {
        if (isRunning) {
            return "Running, ${formatElapsedTime()} elapsed"
        }

        return "Stopped, ${formatElapsedTime()} elapsed"
    }

}