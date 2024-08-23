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

package services

import base.SpecBase
import featureswitch.core.config.EnableNRS
import fixtures.{NRSBrokerFixtures, SubmitReportOfReceiptFixtures}
import mocks.config.MockAppConfig
import mocks.connectors.MockSubmitReportOfReceiptConnector
import mocks.services.{MockAuditingService, MockNRSBrokerService}
import models.AcceptMovement._
import models.audit.{SubmitReportOfReceiptAuditModel, SubmitReportOfReceiptResponseAuditModel}
import models.response.{SubmitReportOfReceiptException, UnexpectedDownstreamResponseError}
import models.submitReportOfReceipt.SubmitReportOfReceiptModel
import pages._
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitReportOfReceiptServiceSpec extends SpecBase
  with MockSubmitReportOfReceiptConnector
  with SubmitReportOfReceiptFixtures
  with MockAuditingService
  with MockAppConfig
  with MockNRSBrokerService
  with NRSBrokerFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  lazy val testService = new SubmitReportOfReceiptService(mockSubmitReportOfReceiptConnector, mockNRSBrokerService, mockAuditingService, mockAppConfig)

  class Fixture(isNRSEnabled: Boolean) {
    (() => mockAppConfig.destinationOfficeSuffix).expects().returns("004098").anyNumberOfTimes()
    MockAppConfig.getFeatureSwitchValue(EnableNRS).returns(isNRSEnabled)
  }

  ".submit(ern: String, submission: SubmitReportOfReceiptModel)" - {

    Seq(true, false).foreach { nrsEnabled =>

      s"when NRS enabled is '$nrsEnabled'" - {

        "should return Success response" - {

          "when Connector returns success from downstream" in new Fixture(nrsEnabled) {

            val userAnswers = emptyUserAnswers
              .set(DateOfArrivalPage, testDateOfArrival)
              .set(AcceptMovementPage, Satisfactory)

            val request = dataRequest(FakeRequest(), userAnswers)
            val submission = SubmitReportOfReceiptModel(getMovementResponseModel)(userAnswers, mockAppConfig)

            MockAuditingService.verifyAudit(SubmitReportOfReceiptAuditModel("credId", "internalId", "correlationId", submission, "ern")).noMoreThanOnce()

            MockAuditingService.verifyAudit(
              SubmitReportOfReceiptResponseAuditModel("credId", "internalId", "correlationId", "arc", "ern", successResponseChRIS.receipt)
            ).noMoreThanOnce()

            MockSubmitReportOfReceiptConnector.submit(testErn, submission).returns(Future.successful(Right(successResponseChRIS)))

            if(nrsEnabled) {
              MockNRSBrokerService.submitPayload(submission, testErn).returns(Future.successful(Right(nrsBrokerResponseModel)))
            } else {
              MockNRSBrokerService.submitPayload(submission, testErn).never()
            }

            testService.submit(testErn, testArc)(hc, request).futureValue mustBe successResponseChRIS
          }
        }

        "should return Failure response" - {

          "when Connector returns failure from downstream" in new Fixture(nrsEnabled) {

            val userAnswers = emptyUserAnswers
              .set(DateOfArrivalPage, testDateOfArrival)
              .set(AcceptMovementPage, Satisfactory)

            val request = dataRequest(FakeRequest(), userAnswers)
            val submission = SubmitReportOfReceiptModel(getMovementResponseModel)(userAnswers, mockAppConfig)

            MockAuditingService.verifyAudit(SubmitReportOfReceiptAuditModel("credId", "internalId", "correlationId", submission, "ern")).noMoreThanOnce()
            MockSubmitReportOfReceiptConnector.submit(testErn, submission).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

            if(nrsEnabled) {
              MockNRSBrokerService.submitPayload(submission, testErn).returns(Future.successful(Right(nrsBrokerResponseModel)))
            } else {
              MockNRSBrokerService.submitPayload(submission, testErn).never()
            }

            intercept[SubmitReportOfReceiptException](await(testService.submit(testErn, testArc)(hc, request))).getMessage mustBe
              s"Failed to submit Report of Receipt to emcs-tfe for ern: '$testErn' & arc: '$testArc'"
          }
        }
      }
    }
  }
}
