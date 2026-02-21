package org.a
package utils.redis

import utils.Config.cfg

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.{Connection, DefaultJedisClientConfig, RedisClient}

import java.time.Duration
import java.time.temporal.ChronoUnit

object RedisFactory {
  lazy val redisClient: RedisClient = {
    val redisConfig = cfg.getConfig("redis")

    // 构建连接池配置
    val poolConfig = new GenericObjectPoolConfig[Connection]()
    poolConfig.setMaxTotal(redisConfig.getInt("maxTotal"))
    poolConfig.setMaxIdle(redisConfig.getInt("maxIdle"))
    poolConfig.setMinIdle(redisConfig.getInt("minIdle"))
    poolConfig.setEvictorShutdownTimeout(
      Duration.of(
        redisConfig.getLong("evictorShutdownTimeout"),
        ChronoUnit.MILLIS
      )
    )
    poolConfig.setTestOnBorrow(redisConfig.getBoolean("testOnBorrow"))
    poolConfig.setTestOnReturn(redisConfig.getBoolean("testOnReturn"))
    poolConfig.setTestWhileIdle(redisConfig.getBoolean("testWhileIdle"))

    // 创建 RedisClient
    RedisClient.builder
      .hostAndPort(redisConfig.getString("host"), redisConfig.getInt("port"))
      .clientConfig(
        DefaultJedisClientConfig.builder
          .password(redisConfig.getString("password"))
          .database(redisConfig.getInt("database"))
          .timeoutMillis(redisConfig.getInt("timeoutMillis"))
          .connectionTimeoutMillis(
            redisConfig.getInt("connectionTimeoutMillis")
          )
          .socketTimeoutMillis(redisConfig.getInt("socketTimeoutMillis"))
          .blockingSocketTimeoutMillis(
            redisConfig.getInt("blockingSocketTimeoutMillis")
          )
          .build
      )
      .poolConfig(poolConfig)
      .build
  }

  // 关闭线程池
  def shutdown: Unit = redisClient.close
}
