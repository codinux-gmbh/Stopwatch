package net.codinux.util.stopwatch

import net.codinux.util.stopwatch.formatter.DefaultTimeFormatter
import net.codinux.util.stopwatch.formatter.TimeFormatter
import net.codinux.util.stopwatch.output.KmpMessageLogger
import net.codinux.util.stopwatch.output.MessageLogger
import net.codinux.util.stopwatch.statistics.DefaultTaskStatisticsCollector
import net.codinux.util.stopwatch.statistics.TaskStatisticsCollector
import kotlin.jvm.JvmStatic
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource


@OptIn(ExperimentalTime::class)
open class Stopwatch constructor(
    createStarted: Boolean = true,
    protected open val logger: MessageLogger = DefaultLogger,
    protected open val timeFormatter: TimeFormatter = DefaultTimeFormatter,
    protected open val statisticsCollector: TaskStatisticsCollector = DefaultStatisticsCollector
) {

    // overload for programming languages that don't support default parameters
    constructor(createStarted: Boolean) : this(createStarted, DefaultLogger)

    companion object {

        @JvmStatic
        var DefaultTimeFormatter: TimeFormatter = DefaultTimeFormatter()

        @JvmStatic
        var DefaultLogger: MessageLogger = KmpMessageLogger(Stopwatch::class)

        @JvmStatic
        var DefaultStatisticsCollector: TaskStatisticsCollector = DefaultTaskStatisticsCollector(DefaultLogger, DefaultTimeFormatter)

        const val DefaultAddToStatistics = false

        const val DefaultLogStatisticsNow = false


        @JvmStatic
        inline fun measureDuration(task: () -> Unit): Duration {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stop()
        }


        @JvmStatic
        inline fun formatDuration(task: () -> Unit): String {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stopAndFormat()
        }


        @JvmStatic
        // overload for programming languages that don't support default parameters
        inline fun <T> logDuration(taskName: String, task: () -> T): T =
            logDuration(taskName, DefaultAddToStatistics, DefaultLogStatisticsNow, task)

        @JvmStatic
        inline fun <T> logDuration(taskName: String, addToStatistics: Boolean = DefaultAddToStatistics, logStatisticsNow: Boolean = DefaultLogStatisticsNow, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(taskName, addToStatistics, logStatisticsNow)

            return result
        }


        /**
         * Adds the elapsed time only to [TaskStatisticsCollector] but doesn't log it.
         * Set [logStatisticsNow] to true to log task statistics now. Otherwise statistics will be logged when JVM shuts down or by a call to [logStatistics].
         */
        @JvmStatic
        // overload for programming languages that don't support default parameters
        inline fun <T> measureAndToStatistics(taskName: String, task: () -> T) =
            measureAndToStatistics(taskName, DefaultLogStatisticsNow, task)

        /**
         * Adds the elapsed time only to [TaskStatisticsCollector] but doesn't log it.
         * Set [logStatisticsNow] to true to log task statistics now. Otherwise statistics will be logged when JVM shuts down or by a call to [logStatistics].
         */
        @JvmStatic
        inline fun <T> measureAndToStatistics(taskName: String, logStatisticsNow: Boolean = DefaultLogStatisticsNow, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndAddToStatistics(taskName, logStatisticsNow)

            return result
        }

        fun logStatistics(task: String) = DefaultStatisticsCollector.logStatistics(task)

        fun logAllStatistics() = DefaultStatisticsCollector.logAllStatistics()
    }


    /**
     * Returns if the stopwatch currently is running (or if it's in stopped state).
     */
    open var isRunning = false
        protected set

    protected var startedAt: TimeSource.Monotonic.ValueTimeMark? = null

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

            return Duration.ZERO // not running and never started
        }

    /**
     * Returns the elapsed time in nanoseconds.
     */
    open val elapsedNanos: Long
        get() = elapsed.inWholeNanoseconds


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

        startedAt = TimeSource.Monotonic.markNow()
    }

    /**
     * Stops the stopwatch and returns the elapsed time as [Duration].
     */
    open fun stop(): Duration {
        if (isRunning) {
            elapsedDurationWhenStopped = calculateDuration()

            isRunning = false
        }

        return elapsed
    }

    /**
     * Stops the stopwatch and returns the elapsed time as nanoseconds.
     */
    open fun stopNanos(): Long {
        stop()

        return elapsedNanos
    }

    /**
     * Stops the stopwatch and returns its elapsed time formatted by [TimeFormatter] passed to constructor.
     */
    open fun stopAndFormat(): String {
        stop()

        return formatElapsedTime()
    }

    /**
     * Stops the stopwatch and logs the elapsed time formatted to [logger] in format: "<task> <formatted_duration>".
     */
    // overload for programming languages that don't support default parameters
    open fun stopAndLog(task: String) =
        stopAndLog(task, DefaultAddToStatistics)

    /**
     * Stops the stopwatch and logs the elapsed time formatted to [logger] in format: "<task> <formatted_duration>".
     */
    open fun stopAndLog(task: String, addToStatistics: Boolean = DefaultAddToStatistics, logStatisticsNow: Boolean = DefaultLogStatisticsNow) {
        stop()

        logElapsedTime(task, addToStatistics, logStatisticsNow)
    }


    /**
     * Returns the elapsed time in a desired time unit.
     */
    open fun getElapsed(desiredUnit: DurationUnit): Long {
        return elapsed.toLong(desiredUnit)
    }

    protected open fun calculateDuration(): Duration {
        val elapsed = startedAt?.elapsedNow()

        if (startedAt == null || elapsed == null) {
            throw createStopwatchHasNotBeenStartedException()
        }

        return elapsed
    }

    protected open fun createStopwatchHasNotBeenStartedException(): Throwable =
        IllegalStateException("Stopwatch has not been started. Start Stopwatch before calling [stop()] or [elapsed] on it.")


    /**
     * Returns the elapsed time formatted by [TimeFormatter] passed to constructor.
     */
    open fun formatElapsedTime(): String {
        return timeFormatter.format(elapsed)
    }

    /**
     * Logs the elapsed time formatted to [logger] in format: "<task> <formatted_duration>".
     */
    // overload for programming languages that don't support default parameters
    open fun logElapsedTime(task: String) =
        logElapsedTime(task, DefaultAddToStatistics)

    /**
     * Logs the elapsed time formatted to [logger] in format: "<task> <formatted_duration>".
     */
    open fun logElapsedTime(task: String, addToStatistics: Boolean = DefaultAddToStatistics, logStatisticsNow: Boolean = DefaultLogStatisticsNow) {
        val formattedElapsedTime = formatElapsedTime()

        logger.info("$task took $formattedElapsedTime")

        if (addToStatistics) {
            addToStatistics(task, elapsed)
        }

        if (logStatisticsNow) {
            logStatistics(task)
        }
    }

    open fun stopAndAddToStatistics(task: String, logStatisticsNow: Boolean = DefaultLogStatisticsNow) {
        val elapsed = stop()

        addToStatistics(task, elapsed)

        if (logStatisticsNow) {
            logStatistics(task)
        }
    }

    protected open fun addToStatistics(task: String, elapsed: Duration) {
        statisticsCollector.addElapsedTime(task, elapsed)
    }

    open fun logStatistics(task: String) {
        statisticsCollector.logStatistics(task)
    }


    override fun toString(): String {
        if (isRunning) {
            return "Running, ${formatElapsedTime()} elapsed"
        }

        return "Stopped, ${formatElapsedTime()} elapsed"
    }

}