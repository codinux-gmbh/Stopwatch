package net.codinux.util.output


/**
 * Prints elapsed messages to `System.out`.
 */
open class ConsoleMessagePrinter : MessagePrinter {

  override fun info(message: String) {
    println(message)
  }

}