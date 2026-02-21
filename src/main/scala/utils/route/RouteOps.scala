package org.a
package utils.route

import utils.route.HttpService.ec

import org.apache.pekko.http.scaladsl.server.Route
import sttp.tapir.PublicEndpoint
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter

import scala.concurrent.{ExecutionContext, Future}

object RouteOps {
  private def fromEndpoint[I, O](
      endpoint: PublicEndpoint[I, Unit, O, Any]
  )(logic: I => O)(using ec: ExecutionContext): Route =
    PekkoHttpServerInterpreter().toRoute(
      endpoint.serverLogicSuccess(input => Future(logic(input)))
    )

  extension [I, O](point: PublicEndpoint[I, Unit, O, Any]) {
    def logic(logic: I => O)(using ec: ExecutionContext = ec): Route =
      fromEndpoint(point)(logic)
  }
}
