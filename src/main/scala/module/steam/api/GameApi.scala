package org.a
package module.steam.api

enum GameApi(val appId: Long, val name: String) {
  case ATRI extends GameApi(1230150, "ATRI -My Dear Moments-")
  case Vengeance extends GameApi(1875830, "真·女神转生Ⅴ Vengeance")
  case Mirror extends GameApi(644560, "Mirror")
  case Hearts_of_Iron extends GameApi(394360, "Hearts of Iron IV")
  case Civilization_VI extends GameApi(289070, "Sid Meier’s Civilization® VI")
  case Left_4_Dead_2 extends GameApi(550, "Left 4 Dead 2")
  case HELLDIVERS_2 extends GameApi(553850, "HELLDIVERS™ 2")
  case XCOM_2 extends GameApi(268500, "XCOM_2")
}
