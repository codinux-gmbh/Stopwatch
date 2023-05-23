@file:JvmName("StopwatchKt")

package net.codinux.util

import java.time.Duration

val Stopwatch.elapsedDuration: Duration
  get() = Duration.ofNanos(this.elapsedNanos)

// methods to make a nicer API for Java

fun measureDuration(task: Runnable): Duration = Duration.ofNanos(Stopwatch.measureDuration { task.run() }.inWholeNanoseconds)

fun stopDuration(stopwatch: Stopwatch): Duration = Duration.ofNanos(stopwatch.stopNanos())
