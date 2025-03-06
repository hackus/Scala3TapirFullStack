package com.learn

import sttp.tapir.*
import Library.*
import cats.effect.IO
import io.circe.generic.auto.*
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import scala.util.{Success, Failure, Try};

object Endpoints:
//  val helloEndpoint: PublicEndpoint[User, Unit, String, Any] = endpoint
//    .get
//    .in(("hello"))
//    .in(query[User]("name"))
//    .errorOut(stringBody.description("Invalid value for: query parameter name").and(statusCode(sttp.model.StatusCode(400))))
//    .out(stringBody.description(""))
//
//  ServerEndpoint[Any, IO]
//
//  val helloServerEndpoint: ServerEndpoint[Any, IO] = helloEndpoint.serverLogicSuccess(user => IO.pure(s"Hello ${user.name}"))


//  case class User(name: String) extends AnyVal

//  val userInput: EndpointIO[User] = jsonBody[User]

  def decode(s: String): DecodeResult[User] = User.parse(s) match {
    case Success(v) => DecodeResult.Value(v)
    case Failure(f) => DecodeResult.Error(s, f)
  }

  def encode(name: User): String = name.toString

  implicit val myIdCodec: Codec[String, User, TextPlain] =
    Codec.string.mapDecode(decode)(encode)

  val helloEndpoint: PublicEndpoint[User, Unit, User, Any] = endpoint.get
    .in("hello")
    .in(query[User]("name"))
    .out(jsonBody[User])
  val helloServerEndpoint: ServerEndpoint[Any, IO] = helloEndpoint.serverLogicSuccess(user => IO.pure(user))

  val booksListing: PublicEndpoint[Unit, Unit, List[Book], Any] = endpoint.get
    .in("books" / "list" / "all")
    .out(jsonBody[List[Book]])
  val booksListingServerEndpoint: ServerEndpoint[Any, IO] = booksListing.serverLogicSuccess(_ => IO.pure(Library.books))

  val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(helloServerEndpoint, booksListingServerEndpoint)

  val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
      .fromServerEndpoints[IO](apiEndpoints, "scala3tapirfullstack", "1.0.0")

  val prometheusMetrics: PrometheusMetrics[IO] = PrometheusMetrics.default[IO]()
  val metricsEndpoint: ServerEndpoint[Any, IO] = prometheusMetrics.metricsEndpoint

  val all: List[ServerEndpoint[Any, IO]] = apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)

  val apiEndpointsSwagger = List(helloEndpoint, booksListing)
  
object Library:

  val books = List(
    Book("The Sorrows of Young Werther", 1774, Author("Johann Wolfgang von Goethe")),
    Book("On the Niemen", 1888, Author("Eliza Orzeszkowa")),
    Book("The Art of Computer Programming", 1968, Author("Donald Knuth")),
    Book("Pharaoh", 1897, Author("Boleslaw Prus"))
  )