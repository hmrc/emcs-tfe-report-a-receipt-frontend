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
import fixtures.SubmitReportOfReceiptFixtures
import mocks.connectors.MockHttpClient
import models.response.JsonValidationError
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class SubmitReportOfReceiptConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with SubmitReportOfReceiptFixtures {

  lazy val app = applicationBuilder(userAnswers = None).build()

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  lazy val appConfig = app.injector.instanceOf[AppConfig]

  lazy val connector = new SubmitReportOfReceiptConnector(mockHttpClient, appConfig)

  "submit" - {

    "should return a successful response" - {

      "when downstream call is successful" in {

        MockHttpClient.post(
          url = url"${appConfig.emcsTfeBaseUrl}/report-of-receipt/ern/arc",
          body = Json.toJson(maxSubmitReportOfReceiptModel)
        ).returns(Future.successful(Right(successResponseChRIS)))

        connector.submit(exciseRegistrationNumber = "ern", maxSubmitReportOfReceiptModel).futureValue mustBe Right(successResponseChRIS)
      }
    }

    "should return an error response" - {

      "when downstream call fails" in {

        MockHttpClient.post(
          url = url"${appConfig.emcsTfeBaseUrl}/report-of-receipt/ern/arc",
          body = Json.toJson(maxSubmitReportOfReceiptModel)
        ).returns(Future.successful(Left(JsonValidationError)))

        connector.submit(exciseRegistrationNumber = "ern", maxSubmitReportOfReceiptModel).futureValue mustBe Left(JsonValidationError)
      }
    }
  }
}
