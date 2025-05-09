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
import mocks.services.MockUserAnswersService
import models.requests.OptionalDataRequest
import models.{NormalMode, UserAnswers}
import pages.DateOfArrivalPage
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.ContinueDraftView

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(bind[UserAnswersService].toInstance(mockUserAnswersService))
        .build()
    lazy val view = application.injector.instanceOf[ContinueDraftView]
    implicit val msgs: Messages = messages(application)
  }

  "Index Controller" - {

    ".onPageLoad()" - {

      "when existing UserAnswers don't exist" - {

        "must Initialise the UserAnswers and redirect to DateOfArrival" in new Fixture() {
          running(application) {

            MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))

            val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testArc).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.DateOfArrivalController.onPageLoad(testErn, testArc, NormalMode).url)
          }
        }
      }

      "when existing UserAnswers exist" - {

        "must render the Continue Draft view" in new Fixture(Some(emptyUserAnswers.set(DateOfArrivalPage, testDateOfArrival))) {

          running(application) {

            val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testArc).url)
            implicit val optDataRequest: OptionalDataRequest[_] = optionalDataRequest(request, Some(userAnswers.get))
            val result = route(application, request).value

            status(result) mustEqual OK
            contentAsString(result) mustBe view(
              continue = routes.IndexController.continueOrStartAgain(testErn, testArc, continueDraft = true),
              startAgain = routes.IndexController.continueOrStartAgain(testErn, testArc, continueDraft = false)
            ).toString()
          }
        }
      }
    }

    ".continueOrStartAgain()" - {

      "when user has selected to continueDraft" - {

        "must re-use the UserAnswers and redirect to DateOfArrival" in new Fixture(Some(emptyUserAnswers.set(DateOfArrivalPage, testDateOfArrival))) {
          running(application) {

            MockUserAnswersService.set(userAnswers.get).returns(Future.successful(userAnswers.get))

            val request = FakeRequest(GET, routes.IndexController.continueOrStartAgain(testErn, testArc, continueDraft = true).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.DateOfArrivalController.onPageLoad(testErn, testArc, NormalMode).url)
          }
        }
      }

      "when user has selected to startAgain" - {

        "must re-initialise the UserAnswers and redirect to DateOfArrival" in new Fixture(Some(emptyUserAnswers.set(DateOfArrivalPage, testDateOfArrival))) {
          running(application) {

            MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))

            val request = FakeRequest(GET, routes.IndexController.continueOrStartAgain(testErn, testArc, continueDraft = false).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.DateOfArrivalController.onPageLoad(testErn, testArc, NormalMode).url)
          }
        }
      }
    }

    ".onPageLoadLegacy()" - {
      "must redirect to .onPageLoad()" in new Fixture() {
        running(application) {
          val request = FakeRequest(GET, routes.IndexController.onPageLoadLegacy(testErn, testArc).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.IndexController.onPageLoad(testErn, testArc).url)
        }
      }
    }
  }
}
