package com.learn

import cats.effect.std.Dispatcher
import cats.effect.{ExitCode, IO, IOApp}
import sttp.tapir.server.netty.cats.{NettyCatsServer, NettyCatsServerOptions}

object Main extends IOApp:
  val port = sys.env.get("HTTP_PORT").flatMap(_.toIntOption).getOrElse(9000)

  override def run(args: List[String]): IO[ExitCode] =
    Dispatcher.parallel[IO]
      .map(d => {
        NettyCatsServer.apply[IO]({
          NettyCatsServerOptions
            .customiseInterceptors(d)
            .metricsInterceptor(Endpoints.prometheusMetrics.metricsInterceptor())
            .options
        })
      })
      .use: server =>
        for
          bind <- server
            .port(port)
            .host("localhost")
            .addEndpoints(Endpoints.all)
            .start()
          _ <- IO.println(s"Go to http://localhost:${bind.port}/docs to open SwaggerUI. Press ENTER key to exit.")
          _ <- IO.readLine
          _ <- bind.stop()
        yield bind
      .as(ExitCode.Success)
