package net.codinux.util.output


/**
 * Prints elapsed messages and warnings with `System.out` / `System.err`.
 */
open class SystemOutMessagePrinter : MessagePrinter {

  override fun info(message: String) {
    println(message)
  }

  override fun warn(message: String) {
    System.err.println(message)
  }

}