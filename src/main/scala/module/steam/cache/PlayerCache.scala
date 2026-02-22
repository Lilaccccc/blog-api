package org.a
package module.steam.cache

import module.steam.pojo.Player
import utils.redis.RedisOps

object PlayerCache {
  def apply: Option[Player] = {
    RedisOps.get[Player]("steam:player") match
      case Some(player) => Some(player)
      case None         =>
        Player.player match
          case Some(player) =>
            RedisOps.set("steam:player", player, 8 * 3600)
            Some(player)
          case None => None
  }
}
