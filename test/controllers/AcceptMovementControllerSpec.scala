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
import forms.AcceptMovementFormProvider
import mocks.services.MockUserAnswersService
import models.{AcceptMovement, NormalMode}
import navigation.{FakeNavigator, Navigator}
import pages.AcceptMovementPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.AcceptMovementView

import scala.concurrent.Future

class AcceptMovementControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  lazy val acceptMovementRoute = routes.AcceptMovementController.onPageLoad(testErn, testArc, NormalMode).url

  val formProvider = new AcceptMovementFormProvider()
  val form = formProvider()

  "AcceptMovement Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, acceptMovementRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AcceptMovementView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(AcceptMovementPage, AcceptMovement.values.head)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, acceptMovementRoute)

        val view = application.injector.instanceOf[AcceptMovementView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(AcceptMovement.values.head), NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" - {

      "and delete the rest of the answers when the input answer isn't the same as the current answer" in {
        val updatedAnswers = emptyUserAnswers.set(AcceptMovementPage, AcceptMovement.values.head)

        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers.set(AcceptMovementPage, AcceptMovement.values.last)))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(POST, acceptMovementRoute)
              .withFormUrlEncodedBody(("value", AcceptMovement.values.head.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "and not delete the rest of the answers when the input answer is the same as the current answer" in {
        MockUserAnswersService.set().never()

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers.set(AcceptMovementPage, AcceptMovement.values.head)))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(POST, acceptMovementRoute)
              .withFormUrlEncodedBody(("value", AcceptMovement.values.head.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, acceptMovementRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AcceptMovementView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, acceptMovementRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, acceptMovementRoute)
            .withFormUrlEncodedBody(("value", AcceptMovement.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
