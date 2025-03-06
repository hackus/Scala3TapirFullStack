package com.learn

import com.learn.Endpoints.{*, given}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.testing.SttpBackendStub
import sttp.client3.{UriContext, basicRequest}
import sttp.tapir.server.stub.TapirStubInterpreter
import Library.*
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.circe.generic.auto.*
import sttp.apispec.openapi.OpenAPI
import sttp.client3.circe.*
import sttp.tapir.integ.cats.effect.CatsMonadError
import sttp.apispec.openapi.circe.yaml.*
import sttp.apispec.openapi.OpenAPI
import sttp.tapir.*
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.apispec.openapi.circe.yaml.*

import scala.io.Source
import sttp.tapir.docs.openapi.*

class EndpointsSpec extends AnyFlatSpec with Matchers with EitherValues:

  it should "return hello message" in {
    // given
    val backendStub = TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
      .whenServerEndpointRunLogic(helloServerEndpoint)
      .backend()

    // when
    val response = basicRequest
      .get(uri"http://test.com/hello?name=adam")
      .send(backendStub)

    // then
    response.map(_.body.value shouldBe """{"name":"adam"}""").unwrap
  }

  it should "list available books" in {
    // given
    val backendStub = TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
      .whenServerEndpointRunLogic(booksListingServerEndpoint)
      .backend()

    // when
    val response = basicRequest
      .get(uri"http://test.com/books/list/all")
      .response(asJson[List[Book]])
      .send(backendStub)

    // then
    response.map(_.body.value shouldBe books).unwrap
  }

  it should "generate OpenApi docs" in {
    // given
    val endpoints = Endpoints.apiEndpointsSwagger
    val expected = Source.fromResource("OpenApiGenerated.yaml").mkString

    // when
    val docs: OpenAPI = OpenAPIDocsInterpreter().toOpenAPI(endpoints, "My Bookshop", "1.0")

    // when
    assert(docs.toYaml == expected)
  }

  extension [T](t: IO[T]) def unwrap: T = t.unsafeRunSync()
