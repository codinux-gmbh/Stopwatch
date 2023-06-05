package net.codinux.util.stopwatch

fun Duration.toJavaDuration() = java.time.Duration.ofNanos(this.inWholeNanoseconds)