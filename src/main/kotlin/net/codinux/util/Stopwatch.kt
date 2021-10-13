package net.codinux.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


open class Stopwatch @JvmOverloads constructor(createStarted: Boolean = true) {

    companion object {
        private val log = LoggerFactory.getLogger(Stopwatch::class.java)


        @JvmStatic
        fun measureDuration(task: Runnable): Duration {
            return measureDuration(Stopwatch()) { task.run() }
        }

        fun measureDuration(task: () -> Unit): Duration {
            return measureDuration(Stopwatch(), task)
        }

        private fun measureDuration(stopwatch: Stopwatch, task: () -> Unit): Duration {
            task()

            return stopwatch.stop()
        }


        @JvmStatic
        fun formatDuration(task: Runnable): String {
            return formatDuration(Stopwatch()) { task.run() }
        }

        fun formatDuration(task: () -> Unit): String {
            return formatDuration(Stopwatch(), task)
        }

        private fun formatDuration(stopwatch: Stopwatch, task: () -> Unit): String {
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
        return formatElapsedTime(elapsed)
    }

    open fun formatElapsedTime(elapsed: Duration): String {

        return if (elapsed.toMinutes() > 0) {
            String.format("%02d:%02d.%03d min", elapsed.toMinutes(), elapsed.toSecondsPart(), elapsed.toMillisPart())
        }
        else if (elapsed.toSeconds() > 0) {
            String.format("%02d.%03d s", elapsed.toSeconds(), elapsed.toMillisPart())
        }
        else {
            val durationMicroseconds = elapsed.toNanos() / 1000
            String.format("%02d.%03d ms", elapsed.toMillis(), (durationMicroseconds % 1000))
        }
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