package net.codinux.util.output


/**
 * Logs messages to stdout.
 */
open class ConsoleLogger : MessageLogger {

  override fun info(message: String) {
    println(message)
  }

}