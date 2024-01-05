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
import config.AppConfig
import forms.WrongWithMovementFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers, WrongWithMovement}
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.WrongWithMovementPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.WrongWithMovementView

import scala.concurrent.Future

class WrongWithMovementControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    implicit lazy val config: AppConfig = application.injector.instanceOf[AppConfig]

    lazy val view = application.injector.instanceOf[WrongWithMovementView]
  }

  def onwardRoute: Call = Call("GET", "/foo")

  val formProvider = new WrongWithMovementFormProvider()

  lazy val wrongWithMovementRoute: String = routes.WrongWithMovementController.loadWrongWithMovement(testErn, testArc, NormalMode).url
  lazy val wrongWithMovementSubmitAction: Call = routes.WrongWithMovementController.submitWrongWithMovement(testErn, testArc, NormalMode)

  "WrongWithMovement Controller" - {

    val form = formProvider()

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        val request = FakeRequest(GET, wrongWithMovementRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(WrongWithMovementPage, form, wrongWithMovementSubmitAction)(dataRequest(request), messages(application), config).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      emptyUserAnswers.set(WrongWithMovementPage, WrongWithMovement.values.toSet)
    )) {
      running(application) {

        val request = FakeRequest(GET, wrongWithMovementRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(WrongWithMovementPage, form.fill(WrongWithMovement.values.toSet), wrongWithMovementSubmitAction)(dataRequest(request), messages(application), config).toString
      }
    }

    "must redirect to the next page when valid data is submitted" - {

      val options: Set[WrongWithMovement] =
        Set(
          WrongWithMovement.Shortage,
          WrongWithMovement.Excess,
          WrongWithMovement.Damaged,
          WrongWithMovement.BrokenSeals,
          WrongWithMovement.Other,
        )

      options.foreach {
        option =>
          s"and only keep option $option when $option is selected" in new Fixture(
            Some(emptyUserAnswers.set(WrongWithMovementPage, options))
          ) {

            val updatedAnswers: UserAnswers =
              emptyUserAnswers.set(WrongWithMovementPage, Set(option))

            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

            running(application) {
              val request =
                FakeRequest(POST, wrongWithMovementRoute)
                  .withFormUrlEncodedBody(("value[0]", option.toString))

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual onwardRoute.url
            }
          }
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {

        val request =
          FakeRequest(POST, wrongWithMovementRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(WrongWithMovementPage, boundForm, wrongWithMovementSubmitAction)(dataRequest(request), messages(application), config).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(GET, wrongWithMovementRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request =
          FakeRequest(POST, wrongWithMovementRoute)
            .withFormUrlEncodedBody(("value[0]", WrongWithMovement.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
