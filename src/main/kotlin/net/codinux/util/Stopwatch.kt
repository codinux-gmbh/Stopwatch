package net.codinux.util

import net.codinux.util.output.MessagePrinter
import net.codinux.util.output.Slf4jOrSystemOutMessagePrinter
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


open class Stopwatch @JvmOverloads constructor(
    createStarted: Boolean = true,
    protected open val printer: MessagePrinter = DefaultPrinter,
    protected open val timeFormatter: TimeFormatter = DefaultTimeFormatter,
    protected open val statisticsPrinter: ElapsedTimeStatisticsPrinter = DefaultStatisticsPrinter
) {

    companion object {

        @JvmStatic
        var DefaultTimeFormatter: TimeFormatter = DefaultTimeFormatter()

        @JvmStatic
        var DefaultPrinter: MessagePrinter = Slf4jOrSystemOutMessagePrinter()

        @JvmStatic
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
        fun formatDuration(task: Runnable): String {
            return formatDuration { task.run() }
        }

        inline fun formatDuration(task: () -> Unit): String {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stopAndFormat()
        }


        @JvmStatic
        @JvmOverloads
        fun logDuration(taskName: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, task: Runnable) {
            return logDuration(taskName, addToStatistics, printStatisticsNow) { task.run() }
        }

        @JvmStatic
        @JvmOverloads
        fun <T> logDuration(taskName: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, task: Supplier<T>): T {
            return logDuration(taskName, addToStatistics, printStatisticsNow) { task.get() }
        }

        inline fun <T> logDuration(taskName: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(taskName, addToStatistics, printStatisticsNow)

            return result
        }


        suspend inline fun measureDurationSuspendable(task: suspend () -> Unit): Duration {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stop()
        }

        suspend inline fun <T> logDurationSuspendable(taskName: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, task: suspend () -> T): T {
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
        @JvmOverloads
        fun <T> addDurationToStatistics(taskName: String, printStatisticsNow: Boolean = false, task: Runnable) {
            return addDurationToStatistics(taskName, printStatisticsNow) { task.run() }
        }

        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        @JvmStatic
        @JvmOverloads
        fun <T> addDurationToStatistics(taskName: String, printStatisticsNow: Boolean = false, task: Supplier<T>): T {
            return addDurationToStatistics(taskName, printStatisticsNow) { task.get() }
        }

        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        inline fun <T> addDurationToStatistics(taskName: String, printStatisticsNow: Boolean = false, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndAddToStatistics(taskName, printStatisticsNow)

            return result
        }

        /**
         * Adds the elapsed time only to [ElapsedTimeStatisticsPrinter] but doesn't print it.
         * Set [printStatisticsNow] to true to print task statistics now. Otherwise statistics will be printed when JVM shuts down or by a call to [printStatistics].
         */
        suspend inline fun <T> addDurationToStatisticsAsync(taskName: String, printStatisticsNow: Boolean = false, task: suspend () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndAddToStatistics(taskName, printStatisticsNow)

            return result
        }

        fun printStatistics(task: String) {
            DefaultStatisticsPrinter.printStatistics(task)
        }

        fun printAllStatistics() {
            DefaultStatisticsPrinter.printAllStatistics()
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
    open fun stopAndFormat(): String {
        stop()

        return formatElapsedTime()
    }

    /**
     * Stops the stopwatch and logs the elapsed time formatted to [printer] in format: "<task> <formatted_duration>".
     */
    @JvmOverloads
    open fun stopAndLog(task: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false): Duration {
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
    @JvmOverloads
    open fun logElapsedTime(task: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false) {
        val formattedElapsedTime = formatElapsedTime()

        printer.info("$task took $formattedElapsedTime")

        if (addToStatistics) {
            addToStatistics(task, elapsed)
        }

        if (printStatisticsNow) {
            printStatistics(task)
        }
    }

    open fun stopAndAddToStatistics(task: String, printStatisticsNow: Boolean = false) {
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