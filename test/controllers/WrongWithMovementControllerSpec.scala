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
import forms.WrongWithMovementFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, WrongWithMovement}
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.WrongWithMovementPage
import pages.unsatisfactory.individualItems.WrongWithItemPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.WrongWithMovementView

import scala.concurrent.Future

class WrongWithMovementControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute: Call = Call("GET", "/foo")

  val formProvider = new WrongWithMovementFormProvider()

  lazy val wrongWithMovementRoute: String = routes.WrongWithMovementController.loadWrongWithMovement(testErn, testArc, NormalMode).url
  lazy val wrongWithMovementSubmitAction: Call = routes.WrongWithMovementController.submitWrongWithMovement(testErn, testArc, NormalMode)

  def wrongWithItemRoute(idx: Int): String = routes.WrongWithMovementController.loadwrongWithItem(testErn, testArc, idx, NormalMode).url
  def wrongWithItemSubmitAction(idx: Int): Call = routes.WrongWithMovementController.submitwrongWithItem(testErn, testArc, idx, NormalMode)

  "WrongWithMovement Controller" - {

    Seq(
      (WrongWithMovementPage, wrongWithMovementRoute, wrongWithMovementSubmitAction),
      (WrongWithItemPage(1), wrongWithItemRoute(1), wrongWithItemSubmitAction(1))
    ) foreach { case (page, url, submitAction) =>

      s"for the '$page' page" - {

        val form = formProvider(page)

        "must return OK and the correct view for a GET" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          running(application) {
            val request = FakeRequest(GET, url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[WrongWithMovementView]

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(page, form, submitAction)(dataRequest(request), messages(application)).toString
          }
        }

        "must populate the view correctly on a GET when the question has previously been answered" in {

          val userAnswers = emptyUserAnswers.set(page, WrongWithMovement.values.toSet)

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          running(application) {
            val request = FakeRequest(GET, url)

            val view = application.injector.instanceOf[WrongWithMovementView]

            val result = route(application, request).value

            status(result) mustEqual OK
            contentAsString(result) mustEqual
              view(page, form.fill(WrongWithMovement.values.toSet), submitAction)(dataRequest(request), messages(application)).toString
          }
        }

        "must redirect to the next page when valid data is submitted" in {

          val updatedAnswers = emptyUserAnswers.set(page, Set(WrongWithMovement.values.head))
          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

          val application =
            applicationBuilder(userAnswers = Some(emptyUserAnswers))
              .overrides(
                bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
                bind[UserAnswersService].toInstance(mockUserAnswersService)
              )
              .build()

          running(application) {
            val request =
              FakeRequest(POST, url)
                .withFormUrlEncodedBody(("value[0]", WrongWithMovement.values.head.toString))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual onwardRoute.url
          }
        }

        "must return a Bad Request and errors when invalid data is submitted" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          running(application) {
            val request =
              FakeRequest(POST, url)
                .withFormUrlEncodedBody(("value", "invalid value"))

            val boundForm = form.bind(Map("value" -> "invalid value"))

            val view = application.injector.instanceOf[WrongWithMovementView]

            val result = route(application, request).value

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual view(page, boundForm, submitAction)(dataRequest(request), messages(application)).toString
          }
        }

        "must redirect to Journey Recovery for a GET if no existing data is found" in {

          val application = applicationBuilder(userAnswers = None).build()

          running(application) {
            val request = FakeRequest(GET, url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
          }
        }

        "must redirect to Journey Recovery for a POST if no existing data is found" in {

          val application = applicationBuilder(userAnswers = None).build()

          running(application) {
            val request =
              FakeRequest(POST, url)
                .withFormUrlEncodedBody(("value[0]", WrongWithMovement.values.head.toString))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
          }
        }
      }
    }
  }
}
