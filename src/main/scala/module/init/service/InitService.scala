package org.a
package module.init.service

import utils.ColoredLogger
import utils.redis.RedisOps

object InitService extends ColoredLogger {
  def apply: Unit = {
    RedisOps.init
    info("应用初始化完毕")
  }
}
