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
import forms.OtherInformationFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems.ItemOtherInformationPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import utils.JsonOptionFormatter
import views.html.OtherInformationView

import scala.concurrent.Future

class OtherInformationControllerSpec extends SpecBase with JsonOptionFormatter with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      ).build()

    lazy val view = application.injector.instanceOf[OtherInformationView]
  }

  def onwardRoute: Call = Call("GET", "/foo")

  val formProvider = new OtherInformationFormProvider()

  lazy val otherInformationRoute: String = routes.OtherInformationController.loadOtherInformation(testErn, testArc, NormalMode).url
  lazy val otherInformationSubmitAction: Call = routes.OtherInformationController.submitOtherInformation(testErn, testArc, NormalMode)

  lazy val itemOtherInformationRoute: String = routes.OtherInformationController.loadItemOtherInformation(testErn, testArc, idx = 1, NormalMode).url
  lazy val itemOtherInformationSubmitAction: Call = routes.OtherInformationController.submitItemOtherInformation(testErn, testArc, idx = 1, NormalMode)

  Seq(
    (OtherInformationPage, otherInformationRoute, otherInformationSubmitAction),
    (ItemOtherInformationPage(1), itemOtherInformationRoute, itemOtherInformationSubmitAction)
  ) foreach { case (page, url, submitAction) =>

    s"for the '$page' page" - {

      val form = formProvider(Some(page))

      "must return OK and the correct view for a GET" in new Fixture() {
        running(application) {

          val request = FakeRequest(GET, url)
          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(page, form, submitAction)(dataRequest(request), messages(application)).toString
        }
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
        emptyUserAnswers.set(page, "answer")
      )) {
        running(application) {

          val request = FakeRequest(GET, url)
          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(page, form.fill("answer"), submitAction)(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to the next page when valid data is submitted" in new Fixture() {
        running(application) {

          val updatedAnswers = emptyUserAnswers.set(page, "answer")
          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

          val request =
            FakeRequest(POST, url)
              .withFormUrlEncodedBody(("more-information", "answer"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must return a Bad Request and errors when NO data is submitted" in new Fixture() {
        running(application) {

          val request =
            FakeRequest(POST, url)
              .withFormUrlEncodedBody(("more-information", ""))

          val boundForm = form.bind(Map("more-information" -> ""))
          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(page, boundForm, submitAction)(dataRequest(request), messages(application)).toString
        }
      }

      "must return a Bad Request and errors when only whitespace is submitted" in new Fixture() {
        running(application) {

          val request =
            FakeRequest(POST, url)
              .withFormUrlEncodedBody(("more-information",
                """
                  |
                  |
                  |
                  |
                  |
                  |
                  |
                  |""".stripMargin))

          val boundForm = form.bind(Map("more-information" -> ""))
          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(page, boundForm, submitAction)(dataRequest(request), messages(application)).toString
        }
      }

      "must return a Bad Request and errors when a non-FormUrlEncodedBody is submitted" in new Fixture() {
        running(application) {

          val request =
            FakeRequest(POST, url)
              .withJsonBody(Json.obj("more-information" -> ""))

          val boundForm = form.bind(Map("more-information" -> ""))
          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(page, boundForm, submitAction)(dataRequest(request), messages(application)).toString
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
        running(application) {

          val request =
            FakeRequest(POST, url)
              .withFormUrlEncodedBody(("more-information", "<>"))

          val boundForm = form.bind(Map("more-information" -> "<>"))
          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(page, boundForm, submitAction)(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
        running(application) {

          val request = FakeRequest(GET, url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
        running(application) {

          val request =
            FakeRequest(POST, url)
              .withFormUrlEncodedBody(("more-information", "answer"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }
    }
  }
}
