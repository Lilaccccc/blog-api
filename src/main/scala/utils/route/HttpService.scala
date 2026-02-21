package org.a
package utils.route

import utils.ColoredLogger
import utils.Config.cfg

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Directives.concat
import org.apache.pekko.http.scaladsl.server.Route

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object HttpService extends ColoredLogger {
  given ec: ExecutionContextExecutor = ExecutionContext.fromExecutorService(
    Executors.newVirtualThreadPerTaskExecutor()
  )
  given system: ActorSystem = ActorSystem(cfg.getString("appName"))

  def init(routes: List[Route]) = Thread.startVirtualThread(() =>
    Http()
      .newServerAt("0.0.0.0", cfg.getInt("port"))
      .bind(concat(routes*))
      .foreach { binding =>
        info(s"服务器启动成功: ${binding.localAddress}")
      }
  )
}
