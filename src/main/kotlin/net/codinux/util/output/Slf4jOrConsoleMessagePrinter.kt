package net.codinux.util.output

import net.codinux.util.Stopwatch


/**
 * Uses slf4j if it's on the classpath, System.out otherwise.
 */
open class Slf4jOrConsoleMessagePrinter : MessagePrinter {

  protected open val isSlf4jOnClasspath = try {
    Class.forName("org.slf4j.Logger")
    true
  } catch (e: Exception)  {
    false
  }

  protected open val slf4jPrinter by lazy { Slf4jMessagePrinter(Stopwatch::class.java) }

  protected open val consolePrinter by lazy { ConsoleMessagePrinter() }

  override fun info(message: String) {
    if (isSlf4jOnClasspath) {
      slf4jPrinter.info(message)
    } else {
      consolePrinter.info(message)
    }
  }

}