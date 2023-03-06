package net.codinux.util.output


/**
 * We just didn't want to call it Logger as there already exist so many classes and interfaces named Logger.
 * In this way we don't add another Logger to your autocompletion list.
 */
interface MessagePrinter {

  fun info(message: String)

}