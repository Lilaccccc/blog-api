package org.a
package utils.redis

import io.circe.Decoder
import io.circe.Encoder
import io.circe.parser.*
import io.circe.syntax.*
import redis.clients.jedis.params.ScanParams
import redis.clients.jedis.params.SetParams

import utils.ColoredLogger
import utils.Config.cfg
import utils.redis.RedisFactory.redisClient

object RedisOps extends ColoredLogger {
  lazy val keyPrefix: String = cfg.getString("redis.keyPrefix")

  def set(key: String, value: String, params: SetParams): Unit = {
    redisClient.set(s"$keyPrefix$key", value, params)
    debug(s"Set key: $keyPrefix$key-$value")
  }

  def getString(key: String): Option[String] = Option(
    redisClient.get(s"$keyPrefix$key")
  )

  def set[T: Encoder](key: String, value: T): Unit = {
    val jsonStr = value.asJson.noSpaces
    set(key, jsonStr, SetParams())
  }

  def set[T: Encoder](key: String, value: T, ttlSeconds: Long): Unit = {
    require(ttlSeconds > 0, "ttlSeconds 必须大于 0")
    val jsonStr = value.asJson.noSpaces
    set(key, jsonStr, SetParams().ex(ttlSeconds))
  }

  def get[T: Decoder](key: String): Option[T] = getString(key).flatMap {
    jsonStr =>
      decode[T](jsonStr) match
        case Right(value) => Some(value)
        case Left(e)      =>
          error(s"Failed to decode JSON for key $key: $e")
          None
  }

  // 删除指定键前缀的键
  def deleteByPrefix(prefix: String, count: Int): Unit = {
    var cursor = ScanParams.SCAN_POINTER_START
    val scanParams = ScanParams()
    scanParams.`match`(s"$prefix*")
    scanParams.count(count)

    while {
      val scan = redisClient.scan(cursor, scanParams)
      val keys = scan.getResult
      cursor = scan.getCursor
      if !keys.isEmpty then {
        val pipeline = redisClient.pipelined
        keys.forEach(key => pipeline.del(key))
        pipeline.sync
      }
      cursor != ScanParams.SCAN_POINTER_START
    } do ()
  }

  def init = deleteByPrefix(keyPrefix, 100)
}
