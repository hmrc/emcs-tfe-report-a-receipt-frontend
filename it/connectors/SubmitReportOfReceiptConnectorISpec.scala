package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalTo, equalToJson, post, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.SubmitReportOfReceiptConnector
import fixtures.SubmitReportOfReceiptFixtures
import generators.ModelGenerators
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class SubmitReportOfReceiptConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with ModelGenerators with SubmitReportOfReceiptFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "internal-auth.token" -> "token"
      )
      .build()

  private lazy val connector: SubmitReportOfReceiptConnector = app.injector.instanceOf[SubmitReportOfReceiptConnector]

  ".submit" - {

    val url = s"/emcs-tfe/report-of-receipt/ern/arc"
    val body = Json.toJson(successResponseChRISJson)

    "must return true when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(maxSubmitReportOfReceiptModel))))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(body)))
      )

      connector.submit("ern", maxSubmitReportOfReceiptModel).futureValue mustBe Right(successResponseChRIS)
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(body))))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.submit("ern", maxSubmitReportOfReceiptModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(body))))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.submit("ern", maxSubmitReportOfReceiptModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(body))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.submit("ern", maxSubmitReportOfReceiptModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }


}
