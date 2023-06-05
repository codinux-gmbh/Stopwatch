package net.codinux.util.stopwatch.output

import net.codinux.log.LoggerFactory
import kotlin.reflect.KClass

/**
 * Logs messages to a platform specific appender:
 * - JVM: If slf4j is on the classpath to slf4j, to console otherwise
 * - Android: Logcat
 * - On Apply systems (iOS, macOS, ...): OSLog if available, NSLog otherwise
 * - Other native platforms: Console
 * - JavaScript: JavaScript console
 *
 * See [KMP-Log](https://github.com/codinux-gmbh/KMP-Log) for more information.
 */
open class KmpLogMessageLogger(private val log: net.codinux.log.Logger) : MessageLogger {

  constructor(name: String) : this(LoggerFactory.getLogger(name))

  constructor(forClass: KClass<*>) : this(LoggerFactory.getLogger(forClass))

  override fun info(message: String) {
    log.info(message)
  }

}