package org.a
package module.steam.pojo

final case class Game(
    gameCount: Int,
    games: List[GameItem]
)

final case class GameItem(
    appid: Long,
    playtimeForever: Long,
)
