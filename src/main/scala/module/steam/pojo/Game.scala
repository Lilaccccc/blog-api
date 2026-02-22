package org.a
package module.steam.pojo

import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.generic.semiauto.*
import sttp.tapir.*
import sttp.tapir.SchemaType.*
import sttp.tapir.generic.auto.*

import scala.collection.parallel.CollectionConverters.*
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import module.steam.api.{GameApi, SteamAPI}
import module.steam.cache.SteamInfo
import utils.ColoredLogger
import utils.HttpClient
import utils.route.HttpService.ec

final case class Game(
    gameCount: Int,
    games: List[GameItem]
)

object Game extends ColoredLogger {
  given Encoder[Game] = deriveEncoder[Game]
  given Decoder[Game] = deriveDecoder[Game]

  object HttpDecoder {
    given Decoder[Game] = (c: HCursor) =>
      for {
        gameCount <- c.downFields("response", "game_count").as[Int]
        games <- c.downFields("response", "games").as[List[GameItem]]
      } yield Game(gameCount, games)
  }

  given Schema[Game] = Schema
    .derived[Game]
    .description("游戏信息")
    .modify(_.gameCount)(_.description("游戏数量").encodedExample(81))
    .modify(_.games)(_.description("游戏列表"))

  def game: Option[Game] = {
    val info = SteamInfo.apply
    val future = for {
      game <- {
        import Game.HttpDecoder.given
        HttpClient.sendGetRequest[Game](
          SteamAPI.GetOwnedGames.url,
          Map("key" -> info.key, "steamid" -> info.id)
        )
      }

      achievements <- Future.sequence:
        GameApi.values.map: gameApi =>
          HttpClient
            .sendGetRequest[AchievementResponse](
              SteamAPI.GetPlayerAchievements.url,
              Map(
                "key" -> info.key,
                "steamid" -> info.id,
                "appid" -> gameApi.appId.toString
              )
            )
            .map(response => gameApi.appId -> response)
    } yield {
      val achievementMap = achievements.toMap
      game.copy(games =
        game.games
          .filter(game => GameApi.fromAppId(game.appid).isDefined)
          .par
          .map(game =>
            val achievements: List[Achievement] = achievementMap
              .getOrElse(game.appid, AchievementResponse(List.empty))
              .achievements
            val unLock = achievements.filter(_.achieved == 1).size
            val lock = achievements.size - unLock
            game.copy(
              name = GameApi.getName(game.appid),
              achievementCount = Some(achievements.size),
              unLock = Some(unLock),
              lock = Some(lock)
            )
          )
          .toList
      )
    }

    Try(Await.result(future, 10.seconds)) match
      case Failure(exception) =>
        error(s"获取游戏信息失败：${exception.getMessage}")
        None
      case Success(value) => Some(value)
  }
}

final case class AchievementResponse(achievements: List[Achievement])

object AchievementResponse {
  given Decoder[AchievementResponse] = (c: HCursor) =>
    for {
      achievements <- c
        .downFields("playerstats", "achievements")
        .as[List[Achievement]]
    } yield AchievementResponse(achievements)
}

final case class Achievement(achieved: Int)

object Achievement {
  given Decoder[Achievement] = deriveDecoder[Achievement]
}

// http://api.steampowered.com/IPlayerService/GetOwnedGames/v1/
// http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v1/
final case class GameItem(
    appid: Long,
    name: Option[String],
    playtime_forever: Long,
    achievementCount: Option[Int],
    unLock: Option[Int],
    lock: Option[Int]
)

object GameItem {
  given Encoder[GameItem] = deriveEncoder[GameItem]
  given Decoder[GameItem] = deriveDecoder[GameItem]

  given Schema[GameItem] = Schema
    .derived[GameItem]
    .description("游戏详细信息")
    .modify(_.appid)(_.description("游戏编号").encodedExample(1669980))
    .modify(_.name)(_.description("游戏名称").encodedExample("火山的女儿"))
    .modify(_.playtime_forever)(_.description("总游玩时间（分钟）").encodedExample(1676))
    .modify(_.achievementCount)(_.description("成就总数").encodedExample(234))
    .modify(_.unLock)(_.description("已解锁成就").encodedExample(117))
    .modify(_.lock)(_.description("未解锁成就").encodedExample(117))
}
