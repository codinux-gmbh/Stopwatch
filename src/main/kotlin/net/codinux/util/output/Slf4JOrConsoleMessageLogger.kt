package net.codinux.util.output

import net.codinux.util.Stopwatch


/**
 * Uses slf4j if it's on the classpath, System.out otherwise.
 */
open class Slf4JOrConsoleMessageLogger : MessageLogger {

  protected open val isSlf4jOnClasspath = try {
    Class.forName("org.slf4j.Logger")
    true
  } catch (e: Exception)  {
    false
  }

  protected open val slf4jLogger by lazy { Slf4jLogger(Stopwatch::class.java) }

  protected open val consoleLogger by lazy { ConsoleLogger() }

  override fun info(message: String) {
    if (isSlf4jOnClasspath) {
      slf4jLogger.info(message)
    } else {
      consoleLogger.info(message)
    }
  }

}