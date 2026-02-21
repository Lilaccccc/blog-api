package org.a
package utils

import com.typesafe.config.{Config, ConfigFactory}

object Config {
  lazy val cfg: Config = ConfigFactory.load
}
