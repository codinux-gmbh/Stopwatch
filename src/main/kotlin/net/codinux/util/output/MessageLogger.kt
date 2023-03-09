package net.codinux.util.output


/**
 * We just didn't want to call it Logger as there already exist so many classes and interfaces named Logger.
 * So not another Logger entry gets to your autocompletion list :).
 */
interface MessageLogger {

  fun info(message: String)

}