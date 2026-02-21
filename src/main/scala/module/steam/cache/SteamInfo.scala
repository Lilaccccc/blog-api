package org.a
package module.steam.cache

import utils.Config.cfg
import utils.redis.RedisOps

import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder, HCursor, Json}
import sttp.tapir.*
import sttp.tapir.SchemaType.*
import sttp.tapir.generic.auto.*

import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

final case class SteamInfo(key: String, id: String)

object SteamInfo {
  given Encoder[SteamInfo] = deriveEncoder[SteamInfo]
  given Decoder[SteamInfo] = deriveDecoder[SteamInfo]

  def apply: SteamInfo = {
    val cache = RedisOps.get[SteamInfo]("steam:info")
    cache match
      case Some(info) => info
      case None       =>
        val info =
          SteamInfo(cfg.getString("steam.key"), cfg.getString("steam.id"))
        RedisOps.set("steam:info", info)
        info
  }
}
