package org.a
package module.steam.controller

import org.a.module.steam.cache.PlayerCache
import org.a.utils.result.StatusCode
import org.apache.pekko.http.scaladsl.server.Route
import sttp.tapir.*
import sttp.tapir.json.circe.*

import module.steam.pojo.Player
import utils.result.Result
import utils.result.StatusCode.DataNotFound
import utils.route.Controller
import utils.route.RouteOps.logic

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
