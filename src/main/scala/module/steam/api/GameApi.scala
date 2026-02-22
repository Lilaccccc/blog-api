package org.a
package module.steam.api

enum GameApi(val appId: Long, val name: String) {
  case Volcano_Princess extends GameApi(1669980, "火山的女儿")
  case Tiny_Dream extends GameApi(1002560, "茸雪")
  case Fluffy_Store extends GameApi(1038740, "Fluffy Store")
  case Fox_Hime_Zero extends GameApi(844930, "Fox Hime Zero")
  case Vampires_Melody extends GameApi(1377360, "Vampires' Melody")
  case Vengeance extends GameApi(1875830, "真·女神转生Ⅴ Vengeance")
  case FINAL_FANTASY_VII_REBIRTH
      extends GameApi(2909400, "FINAL_FANTASY_VII_REBIRTH")
  case Mirror extends GameApi(644560, "Mirror")
  case Hearts_of_Iron extends GameApi(394360, "Hearts of Iron IV")
  case Civilization_VI extends GameApi(289070, "Sid Meier’s Civilization® VI")
  case XCOM_2 extends GameApi(268500, "XCOM_2")
  case Tale_of_Immortal extends GameApi(1468810, "鬼谷八荒")
}

object GameApi {
  private val appIdToValue: Map[Long, GameApi] = GameApi.values.map(api => api.appId -> api).toMap

  def fromAppId(appId: Long): Option[GameApi] = appIdToValue.get(appId)

  def getName(appId: Long): Option[String] = fromAppId(appId).map(_.name)
}
