package net.codinux.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


open class Stopwatch @JvmOverloads constructor(
    createStarted: Boolean = true,
    protected open val logger: Logger = Companion.logger,
    protected open val timeFormatter: TimeFormatter = defaultTimeFormatter
) {

    companion object {

        private val defaultTimeFormatter = DefaultTimeFormatter()

        private val logger = LoggerFactory.getLogger(Stopwatch::class.java)

        private val statisticsPrinter = ElapsedTimeStatisticsPrinter(logger, defaultTimeFormatter)


        @JvmStatic
        fun measureDuration(task: Runnable): Duration {
            return measureDuration { task.run() }
        }

        fun measureDuration(task: () -> Unit): Duration {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stop()
        }


        @JvmStatic
        @JvmOverloads
        fun formatDuration(timeFormatter: TimeFormatter = defaultTimeFormatter, task: Runnable): String {
            return formatDuration(timeFormatter) { task.run() }
        }

        fun formatDuration(timeFormatter: TimeFormatter = defaultTimeFormatter, task: () -> Unit): String {
            val stopwatch = Stopwatch(timeFormatter = timeFormatter)

            task()

            return stopwatch.stopAndFormat()
        }


        @JvmStatic
        @JvmOverloads
        fun logDuration(loggedAction: String, printStatistics: Boolean = false, printStatisticsImmediately: Boolean = false, logger: Logger = this.logger, timeFormatter: TimeFormatter = defaultTimeFormatter, task: Runnable) {
            return logDuration(loggedAction, printStatistics, printStatisticsImmediately, logger, timeFormatter) { task.run() }
        }

        @JvmStatic
        @JvmOverloads
        fun <T> logDuration(loggedAction: String, printStatistics: Boolean = false, printStatisticsImmediately: Boolean = false, logger: Logger = this.logger, timeFormatter: TimeFormatter = defaultTimeFormatter, task: Supplier<T>): T {
            return logDuration(loggedAction, printStatistics, printStatisticsImmediately, logger, timeFormatter) { task.get() }
        }

        fun <T> logDuration(loggedAction: String, printStatistics: Boolean = false, printStatisticsImmediately: Boolean = false, logger: Logger = this.logger, timeFormatter: TimeFormatter = defaultTimeFormatter, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(loggedAction, printStatistics, printStatisticsImmediately, logger, timeFormatter)

            return result
        }


        suspend fun measureDurationSuspendable(task: suspend () -> Unit): Duration {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stop()
        }

        suspend fun <T> logDurationSuspendable(loggedAction: String, printStatistics: Boolean = false, printStatisticsImmediately: Boolean = false, logger: Logger = this.logger, timeFormatter: TimeFormatter = defaultTimeFormatter, task: suspend () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(loggedAction, printStatistics, printStatisticsImmediately, logger, timeFormatter)

            return result
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
    open fun stopAndFormat(timeFormatter: TimeFormatter = this.timeFormatter): String {
        stop()

        return formatElapsedTime(timeFormatter)
    }

    /**
     * Stops the stopwatch and logs the elapsed time formatted to [logger] in format: "<action> <formatted_duration>".
     */
    @JvmOverloads
    open fun stopAndLog(action: String, printStatistics: Boolean = false, printStatisticsImmediately: Boolean = false, logger: Logger = this.logger, timeFormatter: TimeFormatter = this.timeFormatter): Duration {
        stop()

        logElapsedTime(action, printStatistics, printStatisticsImmediately, logger, timeFormatter)

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
    open fun formatElapsedTime(timeFormatter: TimeFormatter = this.timeFormatter): String {
        return timeFormatter.format(elapsed)
    }

    /**
     * Logs the elapsed time formatted to [logger] in format: "<action> <formatted_duration>".
     */
    @JvmOverloads
    open fun logElapsedTime(action: String, printStatistics: Boolean = false, printStatisticsImmediately: Boolean = false, logger: Logger = this.logger, timeFormatter: TimeFormatter = this.timeFormatter) {
        val formattedElapsedTime = formatElapsedTime(timeFormatter)

        logger.info("$action took $formattedElapsedTime")

        if (printStatistics || printStatisticsImmediately) {
            statisticsPrinter.addElapsedTime(action, elapsed)

            if (printStatisticsImmediately) {
                statisticsPrinter.printStatistics(action, logger, timeFormatter)
            }
        }
    }


    override fun toString(): String {
        if (isRunning) {
            return "Running, ${formatElapsedTime()} elapsed"
        }

        return "Stopped, ${formatElapsedTime()} elapsed"
    }

}