package org.a
package module.steam.controller

import module.steam.cache.PlayerCache
import module.steam.pojo.Player
import utils.result.{Result, StatusCode}
import utils.route.Controller
import utils.route.RouteOps.logic

import org.apache.pekko.http.scaladsl.server.Route
import sttp.tapir.*
import sttp.tapir.json.circe.*

object ISteamUserEndpoint {
  val getPlayerEndpoint = endpoint.get
    .in("steam-api" / "player")
    .out(jsonBody[Result[Player]].description("玩家信息"))
    .description("获取玩家信息")
    .tag("Stream")

  val endpointList = List(getPlayerEndpoint)
}

class ISteamUserController extends Controller {

  override def route: Route = ISteamUserEndpoint.getPlayerEndpoint.logic { _ =>
    PlayerCache.apply match
      case Some(player) => Result(player)
      case None => Result(StatusCode.DataNotFound, None.asInstanceOf[Player])
  }

  override def endpointList = ISteamUserEndpoint.endpointList
}
