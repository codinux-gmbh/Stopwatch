package net.codinux.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


open class Stopwatch @JvmOverloads constructor(
    createStarted: Boolean = true,
    protected open val defaultLogger: Logger = Companion.defaultLogger,
    protected open val defaultTimeFormatter: TimeFormatter = Companion.defaultTimeFormatter
) {

    companion object {

        private val defaultTimeFormatter = DefaultTimeFormatter()

        private val defaultLogger: Logger = LoggerFactory.getLogger(Stopwatch::class.java)

        private val statisticsPrinter = ElapsedTimeStatisticsPrinter(defaultLogger, defaultTimeFormatter)


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
        fun formatDuration(timeFormatter: TimeFormatter = defaultTimeFormatter, task: Runnable): String {
            return formatDuration(timeFormatter) { task.run() }
        }

        inline fun formatDuration(timeFormatter: TimeFormatter = defaultTimeFormatter, task: () -> Unit): String {
            val stopwatch = Stopwatch(defaultTimeFormatter = timeFormatter)

            task()

            return stopwatch.stopAndFormat()
        }


        @JvmStatic
        @JvmOverloads
        fun logDuration(loggedAction: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter, task: Runnable) {
            return logDuration(loggedAction, addToStatistics, printStatisticsNow, logger, timeFormatter) { task.run() }
        }

        @JvmStatic
        @JvmOverloads
        fun <T> logDuration(loggedAction: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter, task: Supplier<T>): T {
            return logDuration(loggedAction, addToStatistics, printStatisticsNow, logger, timeFormatter) { task.get() }
        }

        inline fun <T> logDuration(loggedAction: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(loggedAction, addToStatistics, printStatisticsNow, logger, timeFormatter)

            return result
        }


        suspend inline fun measureDurationSuspendable(task: suspend () -> Unit): Duration {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stop()
        }

        suspend inline fun <T> logDurationSuspendable(loggedAction: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter, task: suspend () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(loggedAction, addToStatistics, printStatisticsNow, logger, timeFormatter)

            return result
        }

        @JvmOverloads
        fun printStatistics(action: String, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter) {
            statisticsPrinter.printStatistics(action, logger, timeFormatter)
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
     * Stops the stopwatch and logs the elapsed time formatted to [logger] in format: "<action> <formatted_duration>".
     */
    @JvmOverloads
    open fun stopAndLog(action: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter): Duration {
        stop()

        logElapsedTime(action, addToStatistics, printStatisticsNow, logger, timeFormatter)

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
     * Logs the elapsed time formatted to [logger] in format: "<action> <formatted_duration>".
     */
    @JvmOverloads
    open fun logElapsedTime(action: String, addToStatistics: Boolean = false, printStatisticsNow: Boolean = false, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter) {
        val formattedElapsedTime = formatElapsedTime(timeFormatter)

        logger.info("$action took $formattedElapsedTime")

        if (addToStatistics) {
            statisticsPrinter.addElapsedTime(action, elapsed)
        }

        if (printStatisticsNow) {
            statisticsPrinter.printStatistics(action, logger, timeFormatter)
        }
    }

    @JvmOverloads
    open fun printStatistics(action: String, logger: Logger = defaultLogger, timeFormatter: TimeFormatter = defaultTimeFormatter) {
        Stopwatch.printStatistics(action, logger, timeFormatter)
    }


    override fun toString(): String {
        if (isRunning) {
            return "Running, ${formatElapsedTime()} elapsed"
        }

        return "Stopped, ${formatElapsedTime()} elapsed"
    }

}