package net.codinux.util.stopwatch.output

import net.codinux.log.LoggerFactory
import kotlin.reflect.KClass

/**
 * Logs messages to an appender depending on platform:
 * - JVM: If slf4j is on the classpath to slf4j, to console otherwise
 * - Android: Logcat
 * - Darwin (iOS, macOS, ...): NSLog
 * - Other native platforms: Console
 * - JavaScript: JavaScript console
 */
open class KmpMessageLogger(private val log: net.codinux.log.Logger) : MessageLogger {

  constructor(name: String) : this(LoggerFactory.getLogger(name))

  constructor(forClass: KClass<*>) : this(LoggerFactory.getLogger(forClass))

  override fun info(message: String) {
    log.info(message)
  }

}