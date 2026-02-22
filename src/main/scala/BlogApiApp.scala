package org.a

import module.init.service.InitService
import module.openapi.controller.OpenApiController
import utils.route.{Controller, HttpService, initControllers}

import kyo.*

object BlogApiApp extends KyoApp:
  val apis = initControllers[Controller]
  val routes = apis
    .map(_.corsRoute)
    .appended(OpenApiController(apis.flatMap(_.endpointList)).routes)

  run {
    direct {
      InitService.apply.now
      HttpService.init(routes).now
    }
  }
