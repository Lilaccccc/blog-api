package org.a
package module.steam.controller

import module.steam.pojo.Game
import utils.result.{Result, StatusCode}
import utils.route.Controller
import utils.route.RouteOps.logic

import org.apache.pekko.http.scaladsl.server.Route
import sttp.tapir.*
import sttp.tapir.json.circe.*

object IPlayerServiceEndpoint {
  val getGamesEndpoint = endpoint.get
    .in("steam-api" / "games")
    .out(jsonBody[Result[Game]].description("游戏信息"))
    .description("获取游戏信息")
    .tag("Stream")

  val endpointList = List(getGamesEndpoint)
}

class IPlayerServiceController extends Controller {

  override def route: Route = IPlayerServiceEndpoint.getGamesEndpoint.logic {
    _ =>
      Game.apply match
        case Some(game) => Result(game)
        case None => Result(StatusCode.DataNotFound, None.asInstanceOf[Game])
  }

  override def endpointList = IPlayerServiceEndpoint.endpointList
}
