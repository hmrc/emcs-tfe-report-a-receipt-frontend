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

package mocks.connectors

import connectors.emcsTfe.SubmitReportOfReceiptConnector
import models.response.ErrorResponse
import models.response.emcsTfe.SubmitReportOfReceiptResponse
import models.submitReportOfReceipt.SubmitReportOfReceiptModel
import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockSubmitReportOfReceiptConnector extends MockFactory {

  lazy val mockSubmitReportOfReceiptConnector: SubmitReportOfReceiptConnector = mock[SubmitReportOfReceiptConnector]

  object MockSubmitReportOfReceiptConnector {

    def submit(ern: String,
               submission: SubmitReportOfReceiptModel): CallHandler4[String, SubmitReportOfReceiptModel, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, SubmitReportOfReceiptResponse]]] =
      (mockSubmitReportOfReceiptConnector.submit(_: String, _: SubmitReportOfReceiptModel)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, submission, *, *)
  }
}
