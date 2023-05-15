package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalTo, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.GetMovementConnector
import generators.ModelGenerators
import models.response.UnexpectedDownstreamResponseError
import models.response.emcsTfe.{GetMovementResponse, MovementItem, Packaging}
import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global

class GetMovementConnectorISpec  extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with MockitoSugar
  with ModelGenerators {

  val exciseRegistrationNumber = "ern"
  val arc = "arc"

  val boxPackage = Packaging(
    typeOfPackage = "BX",
    quantity = 165
  )

  val cratePackage = Packaging(
    typeOfPackage = "CR",
    quantity = 12
  )

  val item1 = MovementItem(
    itemUniqueReference = 1,
    productCode = "W200",
    cnCode = "22041011",
    quantity = BigDecimal(500),
    grossMass = BigDecimal(900),
    netMass = BigDecimal(375),
    alcoholicStrength = Some(BigDecimal(12.7)),
    packaging = Seq(boxPackage)
  )

  val item2 = MovementItem(
    itemUniqueReference = 2,
    productCode = "W300",
    cnCode = "22041011",
    quantity = BigDecimal(550),
    grossMass = BigDecimal(910),
    netMass = BigDecimal(315),
    alcoholicStrength = None,
    packaging = Seq(boxPackage, cratePackage)
  )

  val getMovementResponseModel: GetMovementResponse = GetMovementResponse(
    arc = arc,
    sequenceNumber = 1,
    consigneeTrader = None,
    deliveryPlaceTrader = None,
    localReferenceNumber = "MyLrn",
    eadStatus = "MyEadStatus",
    consignorName = "MyConsignor",
    dateOfDispatch = LocalDate.parse("2010-03-04"),
    journeyTime = "MyJourneyTime",
    items = Seq(item1, item2),
    numberOfItems = 2
  )

  val getMovementResponseJson: JsValue = Json.obj(
    "arc" -> arc,
    "sequenceNumber" -> 1,
    "localReferenceNumber" -> "MyLrn",
    "eadStatus" -> "MyEadStatus",
    "consignorName" -> "MyConsignor",
    "dateOfDispatch" -> "2010-03-04",
    "journeyTime" -> "MyJourneyTime",
    "items" -> Json.arr(
      Json.obj(fields =
        "itemUniqueReference" -> 1,
        "productCode" -> "W200",
        "cnCode" -> "22041011",
        "quantity" -> 500,
        "grossMass" -> 900,
        "netMass" -> 375,
        "alcoholicStrength" -> 12.7,
        "packaging" -> Json.arr(
          Json.obj(fields =
            "typeOfPackage" -> "BX",
            "quantity" -> 165
          )
        )
      ),
      Json.obj(fields =
        "itemUniqueReference" -> 2,
        "productCode" -> "W300",
        "cnCode" -> "22041011",
        "quantity" -> 550,
        "grossMass" -> 910,
        "netMass" -> 315,
        "packaging" -> Json.arr(
          Json.obj(fields =
            "typeOfPackage" -> "BX",
            "quantity" -> 165
          ),
          Json.obj(fields =
            "typeOfPackage" -> "CR",
            "quantity" -> 12
          )
        )
      )
    ),
    "numberOfItems" -> 2
  )

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "internal-auth.token" -> "token"
      )
      .build()

  private lazy val connector: GetMovementConnector = app.injector.instanceOf[GetMovementConnector]

  ".getMovement" - {

    val body = Json.toJson(getMovementResponseJson)

    val url = s"/emcs-tfe/movement/ern/arc?forceFetchNew=true"

    "must return true when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(body)))
      )

      connector.getMovement(exciseRegistrationNumber, arc).futureValue mustBe Right(getMovementResponseModel)
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.getMovement(exciseRegistrationNumber, arc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getMovement(exciseRegistrationNumber, arc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getMovement(exciseRegistrationNumber, arc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }

}
