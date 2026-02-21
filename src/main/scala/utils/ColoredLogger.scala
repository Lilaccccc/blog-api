package org.a
package utils

import com.typesafe.scalalogging.LazyLogging
import kyo.Ansi
import kyo.Ansi.*

trait ColoredLogger extends LazyLogging {
  def error(message: String): Unit = logger.error(s"$message".red)
  def warn(message: String): Unit = logger.warn(s"$message".yellow)
  def info(message: String): Unit = logger.info(s"$message".green)
  def debug(message: String): Unit = logger.debug(s"$message".blue)
}
