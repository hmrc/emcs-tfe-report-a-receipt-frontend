/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors.emcsTfe

import base.SpecBase
import config.AppConfig
import mocks.MockHttpClient
import models.response.JsonValidationError
import models.response.emcsTfe.GetMovementResponse
import org.scalatest.BeforeAndAfterAll
import play.api.Play
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class GetMovementConnectorSpec extends SpecBase with Status with MimeTypes with HeaderNames with MockHttpClient with BeforeAndAfterAll {

  lazy val app = applicationBuilder(userAnswers = None).build()

  override def beforeAll(): Unit = {
    Play.start(app)
  }

  override def afterAll(): Unit = {
    Play.stop(app)
  }

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  lazy val appConfig = app.injector.instanceOf[AppConfig]

  lazy val connector = new GetMovementConnector(mockHttpClient, appConfig)

  "getMovement" - {

    "should return a successful response" - {

      "when downstream call is successful" in {

        val model: GetMovementResponse = GetMovementResponse(
          localReferenceNumber = "EN", eadStatus = "Accepted", consignorName = "Current 801 Consignor", dateOfDispatch = LocalDate.parse("2008-11-20"), journeyTime = "20 days", numberOfItems = 2
        )

        MockHttpClient.get(s"${appConfig.emcsTfeBaseUrl}/movement/ern/arc").returns(Future.successful(Right(model)))

        connector.getMovement(exciseRegistrationNumber = "ern", arc = "arc").futureValue mustBe Right(model)
      }
    }

    "should return an error response" - {

      "when downstream call fails" in {

        MockHttpClient.get(s"${appConfig.emcsTfeBaseUrl}/movement/ern/arc").returns(Future.successful(Left(JsonValidationError)))

        connector.getMovement(exciseRegistrationNumber = "ern", arc = "arc").futureValue mustBe Left(JsonValidationError)
      }
    }
  }
}
