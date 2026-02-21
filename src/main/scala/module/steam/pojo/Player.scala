package org.a
package module.steam.pojo

import module.steam.api.SteamAPI
import module.steam.cache.SteamInfo
import utils.HttpClient
import utils.route.HttpService.ec

import io.circe.{Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.*
import sttp.tapir.*
import sttp.tapir.SchemaType.*
import sttp.tapir.generic.auto.*

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.*

final case class PlayerResponse(
    players: List[Player]
)

object PlayerResponse {
  given Decoder[PlayerResponse] = (c: HCursor) =>
    for {
      // 直接深入到 response.players.player
      players <- c
        .downField("response")
        .downField("players")
        .downField("player")
        .as[List[Player]]
    } yield PlayerResponse(players)
}

final case class LevelResponse(playerLevel: Int)

object LevelResponse {
  given Decoder[LevelResponse] = (c: HCursor) =>
    for {
      playerLevel <- c.downFields("response", "player_level").as[Int]
    } yield LevelResponse(playerLevel)
}

final case class Player(
    personaname: String,
    avatarfull: String,
    lastlogoff: Long,
    timecreated: Long,
    level: Option[Int] = Some(0)
)

object Player {
  given Encoder[Player] = deriveEncoder[Player]
  given Decoder[Player] = deriveDecoder[Player]

  given Schema[Player] = Schema
    .derived[Player]
    .description("玩家信息")
    .modify(_.personaname)(_.description("名称").encodedExample("猪头少尉"))
    .modify(_.avatarfull)(
      _.description("头像 URL").encodedExample(
        "https://avatars.steamstatic.com/b0e8bca4b270c0e6d5775327925ed58d56adb5f6_full.jpg"
      )
    )
    .modify(_.lastlogoff)(_.description("上次离线时间").encodedExample(1770550506))
    .modify(_.timecreated)(_.description("账号创建时间").encodedExample(1491269596))
    .modify(_.level)(_.description("账号等级").encodedExample(Some(9)))

  def player: Option[Player] = {
    val info = SteamInfo.apply
    val playerRequest: Future[PlayerResponse] =
      HttpClient.sendGetRequest[PlayerResponse](
        SteamAPI.GetPlayerSummaries.url,
        Map("key" -> info.key, "steamids" -> info.id)
      )
    val levelRequest: Future[LevelResponse] =
      HttpClient.sendGetRequest[LevelResponse](
        SteamAPI.GetSteamLevel.url,
        Map("key" -> info.key, "steamid" -> info.id)
      )
    val request: Future[(PlayerResponse, LevelResponse)] = for {
      player <- playerRequest
      level <- levelRequest
    } yield (player, level)
    val (playerResponse, levelResponse) = Await.result(request, 2.seconds)
    playerResponse.players.headOption.map { playerData =>
      playerData.copy(level = Option(levelResponse.playerLevel))
    }
  }
}
