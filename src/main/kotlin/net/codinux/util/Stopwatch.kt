package net.codinux.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


open class Stopwatch @JvmOverloads constructor(
    createStarted: Boolean = true,
    protected open val timeFormatter: TimeFormatter = DefaultTimeFormatter()
) {

    companion object {

        private val defaultTimeFormatter = DefaultTimeFormatter()

        private val log = LoggerFactory.getLogger(Stopwatch::class.java)


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
        fun logDuration(loggedAction: String, logger: Logger = log, task: Runnable) {
            return logDuration(loggedAction, logger) { task.run() }
        }

        @JvmStatic
        @JvmOverloads
        fun <T> logDuration(loggedAction: String, logger: Logger = log, task: Supplier<T>): T {
            return logDuration(loggedAction, logger) { task.get() }
        }

        fun <T> logDuration(loggedAction: String, logger: Logger = log, task: () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(loggedAction, logger)

            return result
        }


        suspend fun measureDurationSuspendable(task: suspend () -> Unit): Duration {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stop()
        }

        suspend fun <T> logDurationSuspendable(loggedAction: String, logger: Logger = log, task: suspend () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(loggedAction, logger)

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
    open fun stopAndFormat(): String {
        stop()

        return formatElapsedTime()
    }

    /**
     * Stops the stopwatch and logs the elapsed time formatted to [logger] in format: "<action> <formatted_duration>".
     */
    @JvmOverloads
    open fun stopAndLog(action: String, logger: Logger = log): Duration {
        stop()

        logElapsedTime(action, logger)

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
     * Logs the elapsed time formatted to [logger] in format: "<action> <formatted_duration>".
     */
    open fun logElapsedTime(action: String, logger: Logger) {
        val formattedElapsedTime = formatElapsedTime()

        logger.info("$action took $formattedElapsedTime")
    }


    override fun toString(): String {
        if (isRunning) {
            return "Running, ${formatElapsedTime()} elapsed"
        }

        return "Stopped, ${formatElapsedTime()} elapsed"
    }

}