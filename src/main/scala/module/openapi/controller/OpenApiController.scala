package org.a
package module.openapi.controller

import utils.Config.cfg
import utils.route.HttpService.given
import utils.route.RouteOps.logic

import org.apache.pekko.http.scaladsl.server.Directives.concat
import org.apache.pekko.http.scaladsl.server.Route
import sttp.apispec.openapi.Info
import sttp.apispec.openapi.circe.yaml.*
import sttp.tapir.*
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter

// OpenApi 文档控制层
class OpenApiController(endpointList: List[AnyEndpoint]) {
  // 生成 OpenApi 配置文件
  private val openApiDoc: String = OpenAPIDocsInterpreter()
    .toOpenAPI(endpointList, Info(title = cfg.getString("docName"), version = cfg.getString("docVersion")))
    .toYaml

  // 生成 OpenAPI 路由
  private val openapiDocRoute: Route = endpoint.get
    .in("doc.yaml")
    .out(
      stringBody
        .and(header[String]("Content-Type"))
        .and(header[String]("Cache-Control"))
    )
    .logic(_ => (openApiDoc, "application/yaml", "public, max-age=6000"))

  // 自定义 Swagger 文档首页路由
  private def docRoute: Route = {
    import org.apache.pekko.http.scaladsl.model.ContentTypes
    import org.apache.pekko.http.scaladsl.server.Directives.*

    path("api-docs") {
      get {
        // 从 resources/swagger-ui 目录读取自定义 HTML
        getFromResource(
          "swagger-ui/swagger.html",
          ContentTypes.`text/html(UTF-8)`
        )
      }
    } ~ pathPrefix("swagger-ui") {
      // 提供 Swagger UI 静态资源（需要把 swagger-ui 文件夹放在 resources 下）
      getFromResourceDirectory("swagger-ui")
    }
  }

  val routes: Route = concat(openapiDocRoute, docRoute)
}
