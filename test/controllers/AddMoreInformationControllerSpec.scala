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
import forms.AddMoreInformationFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory._
import pages.{AddMoreInformationPage, MoreInformationPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.AddMoreInformationView

import scala.concurrent.Future

class AddMoreInformationControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()
    lazy val view = application.injector.instanceOf[AddMoreInformationView]
  }

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AddMoreInformationFormProvider()

  lazy val addMoreInformationRoute = routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode).url
  lazy val addMoreInformationSubmitAction = routes.AddMoreInformationController.submitMoreInformation(testErn, testArc, NormalMode)

  lazy val addShortageInformationRoute = routes.AddMoreInformationController.loadShortageInformation(testErn, testArc, NormalMode).url
  lazy val addShortageInformationSubmitAction = routes.AddMoreInformationController.submitShortageInformation(testErn, testArc, NormalMode)

  lazy val addExcessInformationRoute = routes.AddMoreInformationController.loadExcessInformation(testErn, testArc, NormalMode).url
  lazy val addExcessInformationSubmitAction = routes.AddMoreInformationController.submitExcessInformation(testErn, testArc, NormalMode)

  lazy val addDamageInformationRoute = routes.AddMoreInformationController.loadDamageInformation(testErn, testArc, NormalMode).url
  lazy val addDamageInformationSubmitAction = routes.AddMoreInformationController.submitDamageInformation(testErn, testArc, NormalMode)

  lazy val addSealsInformationRoute = routes.AddMoreInformationController.loadSealsInformation(testErn, testArc, NormalMode).url
  lazy val addSealsInformationSubmitAction = routes.AddMoreInformationController.submitSealsInformation(testErn, testArc, NormalMode)

  Seq(
    (AddMoreInformationPage, MoreInformationPage, addMoreInformationRoute, addMoreInformationSubmitAction),
    (AddShortageInformationPage, ShortageInformationPage, addShortageInformationRoute, addShortageInformationSubmitAction),
    (AddExcessInformationPage, ExcessInformationPage, addExcessInformationRoute, addExcessInformationSubmitAction),
    (AddDamageInformationPage, DamageInformationPage, addDamageInformationRoute, addDamageInformationSubmitAction),
    (AddSealsInformationPage, SealsInformationPage, addSealsInformationRoute, addSealsInformationSubmitAction),
  ) foreach { case (yesNoPage, infoPage, url, submitAction) =>

    s"for the '$yesNoPage' page" - {

      val form = formProvider(yesNoPage)

      "AddMoreInformation Controller" - {

        "must return OK and the correct view for a GET" in new Fixture() {
          running(application) {

            val request = FakeRequest(GET, url)
            val result = route(application, request).value

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(form, yesNoPage, submitAction)(dataRequest(request), messages(application)).toString
          }
        }

        "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
          emptyUserAnswers.set(yesNoPage, true)
        )) {
          running(application) {

            val request = FakeRequest(GET, url)
            val result = route(application, request).value

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(form.fill(true), yesNoPage, submitAction)(dataRequest(request), messages(application)).toString
          }
        }

        "must redirect to the next page when valid data is submitted (true)" in new Fixture() {
          running(application) {

            val updatedAnswers = emptyUserAnswers.set(yesNoPage, true)
            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers)).once()

            val request = FakeRequest(POST, url).withFormUrlEncodedBody(("value", "true"))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual onwardRoute.url
          }
        }

        "must redirect to the next page when valid data is submitted (false) AND clear answers" in new Fixture() {
          running(application) {

            val updatedAnswers = emptyUserAnswers
              .set(yesNoPage, false)
              .set(infoPage, None)

            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers)).once()

            val request = FakeRequest(POST, url).withFormUrlEncodedBody(("value", "false"))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual onwardRoute.url
          }
        }

        "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
          running(application) {

            val request = FakeRequest(POST, url).withFormUrlEncodedBody(("value", ""))
            val boundForm = form.bind(Map("value" -> ""))
            val result = route(application, request).value

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual view(boundForm, yesNoPage, submitAction)(dataRequest(request), messages(application)).toString
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

            val request = FakeRequest(POST, url).withFormUrlEncodedBody(("value", "true"))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
          }
        }
      }
    }
  }
}
