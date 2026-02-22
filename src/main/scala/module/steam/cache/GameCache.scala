package org.a
package module.steam.cache

import module.steam.pojo.Game
import utils.redis.RedisOps

object GameCache {
  def apply: Option[Game] = {
    RedisOps.get[Game]("steam:games") match
      case Some(game) => Some(game)
      case None       =>
        Game.game match
          case Some(game) =>
            RedisOps.set("steam:games", game, 8 * 3600)
            Some(game)
          case None => None
  }
}
