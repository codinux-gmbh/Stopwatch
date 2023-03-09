package net.codinux.util.output

import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * Logs elapsed times and warnings with slf4j.
 *
 * In order to use it add dependency `org.slf4j:slf4j-api` to the classpath.
 */
open class Slf4jLogger(protected open val logger: Logger) : MessageLogger {

  constructor(clazz: Class<*>) : this(LoggerFactory.getLogger(clazz))

  constructor(loggerName: String) : this(LoggerFactory.getLogger(loggerName))

  override fun info(message: String) {
    logger.info(message)
  }

}