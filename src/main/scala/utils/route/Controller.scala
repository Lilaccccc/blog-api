package org.a
package utils.route

import org.apache.pekko.http.cors.scaladsl.CorsDirectives.*
import org.apache.pekko.http.cors.scaladsl.model.HttpOriginMatcher
import org.apache.pekko.http.cors.scaladsl.settings.CorsSettings
import org.apache.pekko.http.scaladsl.model.HttpMethods.*
import org.apache.pekko.http.scaladsl.model.headers.HttpOrigin
import org.apache.pekko.http.scaladsl.server.Route
import sttp.tapir.AnyEndpoint

// 控制层特质
trait Controller {
  def route: Route

  // 暴露 API 端点
  def endpointList: List[AnyEndpoint]

  // 组合跨域路由
  def corsRoute: Route = cors() {
    route
  }
}
