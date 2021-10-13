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
        fun formatDuration(task: Runnable): String {
            return formatDuration(defaultTimeFormatter) { task.run() }
        }

        @JvmStatic
        fun formatDuration(timeFormatter: TimeFormatter, task: Runnable): String {
            return formatDuration(timeFormatter) { task.run() }
        }

        fun formatDuration(task: () -> Unit): String {
            return formatDuration(defaultTimeFormatter, task)
        }

        fun formatDuration(timeFormatter: TimeFormatter, task: () -> Unit): String {
            val stopwatch = Stopwatch()

            task()

            return stopwatch.stopAndFormat()
        }


        @JvmStatic
        fun logDuration(loggedAction: String, task: Runnable) {
            return logDuration(loggedAction, log, task)
        }

        @JvmStatic
        fun <T> logDuration(loggedAction: String, task: Supplier<T>): T {
            return logDuration(loggedAction, log, task)
        }

        @JvmStatic
        fun logDuration(loggedAction: String, logger: Logger, task: Runnable) {
            return logDuration(loggedAction, logger) { task.run() }
        }

        @JvmStatic
        fun <T> logDuration(loggedAction: String, logger: Logger, task: Supplier<T>): T {
            return logDuration(loggedAction, logger) { task.get() }
        }

        fun <T> logDuration(loggedAction: String, task: () -> T): T {
            return logDuration(loggedAction, log, task)
        }

        fun <T> logDuration(loggedAction: String, logger: Logger, task: () -> T): T {
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

        suspend fun <T> logDurationSuspendable(loggedAction: String, task: suspend () -> T): T {
            return logDurationSuspendable(loggedAction, log, task)
        }

        suspend fun <T> logDurationSuspendable(loggedAction: String, logger: Logger, task: suspend () -> T): T {
            val stopwatch = Stopwatch()

            val result = task()

            stopwatch.stopAndLog(loggedAction, logger)

            return result
        }
    }


    open var isRunning = false
        protected set

    protected var startedAt = 0L

    protected var elapsedDurationWhenStopped: Duration? = null

    open val elapsed: Duration
        get() {
            elapsedDurationWhenStopped?.let { return it }

            if (isRunning) {
                return calculateDuration()
            }

            return Duration.ofNanos(0) // not running and never started
        }

    open val elapsedNanos: Long
        get() = elapsed.toNanos()


    init {
        if (createStarted) {
            start()
        }
    }


    open fun start() {
        elapsedDurationWhenStopped = null

        isRunning = true

        startedAt = System.nanoTime()
    }

    open fun stop(): Duration {
        if (isRunning) {
            elapsedDurationWhenStopped = calculateDuration()

            isRunning = false
        }

        return elapsed
    }

    open fun stopAndFormat(): String {
        stop()

        return formatElapsedTime()
    }

    open fun stopAndLog(loggedAction: String): Duration {
        return stopAndLog(loggedAction, log)
    }

    open fun stopAndLog(loggedAction: String, logger: Logger): Duration {
        stop()

        logElapsedTime(loggedAction, logger)

        return elapsed
    }


    open fun elapsed(desiredUnit: TimeUnit): Long {
        return desiredUnit.convert(elapsedNanos, TimeUnit.NANOSECONDS)
    }

    protected open fun calculateDuration(): Duration {
        val stoppedAt = System.nanoTime()

        return Duration.ofNanos(stoppedAt - startedAt)
    }


    open fun formatElapsedTime(): String {
        return timeFormatter.format(elapsed)
    }

    open fun logElapsedTime(loggedAction: String, logger: Logger) {
        val formattedElapsedTime = formatElapsedTime()

        logger.info("$loggedAction took $formattedElapsedTime")
    }


    override fun toString(): String {
        if (isRunning) {
            return "Running, ${formatElapsedTime()} elapsed"
        }

        return "Stopped, ${formatElapsedTime()} elapsed"
    }

}