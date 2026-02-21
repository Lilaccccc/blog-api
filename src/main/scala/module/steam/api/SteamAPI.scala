package org.a
package module.steam.api

enum SteamAPI(val url: String, val desc: String) {
  // ISteamUser
  case GetPlayerSummaries extends SteamAPI("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v1/", "玩家资料")
  // IPlayerService
  case GetOwnedGames extends SteamAPI("http://api.steampowered.com/IPlayerService/GetOwnedGames/v1/", "游戏列表")
  case GetRecentlyPlayedGames extends SteamAPI("http://api.steampowered.com/IPlayerService/GetRecentlyPlayedGames/v1/", "最近游玩")
  case ClientGetLastPlayedTimes extends SteamAPI("http://api.steampowered.com/IPlayerService/ClientGetLastPlayedTimes/v1/", "游玩时间")
  case GetSteamLevel extends SteamAPI("http://api.steampowered.com/IPlayerService/GetSteamLevel/v1/", "玩家等级")
  case GetBadges extends SteamAPI("http://api.steampowered.com/IPlayerService/GetBadges/v1/", "玩家徽章")
  // ISteamUserStats
  case GetPlayerAchievements extends SteamAPI("http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v1/", "游戏成就列表及完成状态")
  case GetUserStatsForGame extends SteamAPI("http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v1/", "游戏统计数据")
  case GetSchemaForGame extends SteamAPI("http://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v1/", "游戏成就和统计数据")
}

object SteamAPI {
  case class GameHeaderImg(url: String, desc: String)

  def GameHeaderImgForGame(appId: Long): GameHeaderImg = GameHeaderImg(s"http://shared.fastly.steamstatic.com/store_item_assets/steam/apps/$appId/header.jpg", "游戏封面")
}
