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

package controllers

import base.SpecBase
import fixtures.SubmitReportOfReceiptFixtures
import handlers.ErrorHandler
import mocks.services.MockSubmitReportOfReceiptService
import mocks.viewmodels.MockCheckAnswersHelper
import models.AcceptMovement.Satisfactory
import models.UserAnswers
import models.response.{MissingMandatoryPage, SubmitReportOfReceiptException}
import navigation.{FakeNavigator, Navigator}
import pages.{AcceptMovementPage, DateOfArrivalPage}
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SubmitReportOfReceiptService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.CheckAnswersHelper
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency with MockCheckAnswersHelper
  with MockSubmitReportOfReceiptService with SubmitReportOfReceiptFixtures {

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          inject.bind[CheckAnswersHelper].toInstance(mockCheckAnswersHelper),
          inject.bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
          inject.bind[SubmitReportOfReceiptService].toInstance(mockSubmitReportOfReceiptService)
        )
        .build()

    lazy val errorHandler = application.injector.instanceOf[ErrorHandler]
    val view = application.injector.instanceOf[CheckYourAnswersView]
  }

  "Check Your Answers Controller" - {

    ".onPageLoad" - {

      def request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(testErn, testArc).url)
      val link = routes.SelectItemsController.onPageLoad(testErn, testArc).url

      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {

          val list = SummaryListViewModel(Seq.empty)
          val itemList = Seq.empty[(String, SummaryList)]

          MockCheckAnswersHelper.summaryList().returns(list)

          val result = route(application, request).value

          status(result) mustBe OK
          contentAsString(result) mustBe view(routes.CheckYourAnswersController.onSubmit(testErn, testArc), link, list, itemList, false)(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    ".onSubmit" - {

      def request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(testErn, testArc).url)

      "when valid data exists so the submission can be generated" - {

        val userAnswers = emptyUserAnswers
          .set(DateOfArrivalPage, testDateOfArrival)
          .set(AcceptMovementPage, Satisfactory)

        "when the submission is successful" - {

          "must redirect to the onward route" in new Fixture(Some(userAnswers)) {

            running(application) {

              MockSubmitReportOfReceiptService.submit(testErn, testArc, getMovementResponseModel, userAnswers)
                .returns(Future.successful(successResponse))

              val result = route(application, request).value

              status(result) mustBe SEE_OTHER
              redirectLocation(result).value mustBe testOnwardRoute.url
            }
          }
        }

        "when the submission fails" - {

          "must render an ISE" in new Fixture(Some(userAnswers)) {

            running(application) {

              MockSubmitReportOfReceiptService.submit(testErn, testArc, getMovementResponseModel, userAnswers)
                .returns(Future.failed(SubmitReportOfReceiptException("bang")))

              val result = route(application, request).value

              status(result) mustBe INTERNAL_SERVER_ERROR
              contentAsString(result) mustBe errorHandler.internalServerErrorTemplate(request).toString()
            }
          }
        }
      }

      "when invalid data exists so the submission can NOT be generated" - {

        "must return BadRequest" in new Fixture(Some(emptyUserAnswers)) {

          running(application) {

            MockSubmitReportOfReceiptService.submit(testErn, testArc, getMovementResponseModel, emptyUserAnswers)
              .returns(Future.failed(MissingMandatoryPage("bang")))

            val result = route(application, request).value

            status(result) mustBe BAD_REQUEST
            contentAsString(result) mustBe errorHandler.badRequestTemplate(request).toString()
          }
        }
      }
    }
  }
}
