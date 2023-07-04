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
import forms.MoreInformationFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems.{AddItemSealsInformationPage, ItemSealsInformationPage}
import pages.{AddMoreInformationPage, MoreInformationPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import utils.JsonOptionFormatter
import views.html.MoreInformationView

import scala.concurrent.Future

class MoreInformationControllerSpec extends SpecBase with JsonOptionFormatter with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      ).build()

    lazy val view = application.injector.instanceOf[MoreInformationView]
  }

  def onwardRoute: Call = Call("GET", "/foo")

  val formProvider = new MoreInformationFormProvider()

  private lazy val moreInformationRoute = routes.MoreInformationController.loadMoreInformation(testErn, testArc, NormalMode).url
  private lazy val moreInformationSubmitAction = routes.MoreInformationController.submitMoreInformation(testErn, testArc, NormalMode)

  private lazy val shortageInformationRoute = routes.MoreInformationController.loadShortageInformation(testErn, testArc, NormalMode).url
  private lazy val shortageInformationSubmitAction = routes.MoreInformationController.submitShortageInformation(testErn, testArc, NormalMode)

  private lazy val excessInformationRoute = routes.MoreInformationController.loadExcessInformation(testErn, testArc, NormalMode).url
  private lazy val excessInformationSubmitAction = routes.MoreInformationController.submitExcessInformation(testErn, testArc, NormalMode)

  private lazy val damageInformationRoute = routes.MoreInformationController.loadDamageInformation(testErn, testArc, NormalMode).url
  private lazy val damageInformationSubmitAction = routes.MoreInformationController.submitDamageInformation(testErn, testArc, NormalMode)

  private lazy val sealsInformationRoute = routes.MoreInformationController.loadSealsInformation(testErn, testArc, NormalMode).url
  private lazy val sealsInformationSubmitAction = routes.MoreInformationController.submitSealsInformation(testErn, testArc, NormalMode)

  private lazy val sealsItemInformationRoute = routes.MoreInformationController.loadItemSealsInformation(testErn, testArc, 1, NormalMode).url
  private lazy val sealsItemInformationSubmitAction = routes.MoreInformationController.submitItemSealsInformation(testErn, testArc, 1, NormalMode)

  "MoreInformation Controller" - {

    Seq(
      (AddMoreInformationPage, MoreInformationPage, moreInformationRoute, moreInformationSubmitAction),
      (AddShortageInformationPage, ShortageInformationPage, shortageInformationRoute, shortageInformationSubmitAction),
      (AddExcessInformationPage, ExcessInformationPage, excessInformationRoute, excessInformationSubmitAction),
      (AddDamageInformationPage, DamageInformationPage, damageInformationRoute, damageInformationSubmitAction),
      (AddSealsInformationPage, SealsInformationPage, sealsInformationRoute, sealsInformationSubmitAction),
      (AddItemSealsInformationPage(1), ItemSealsInformationPage(1), sealsItemInformationRoute, sealsItemInformationSubmitAction)
    ) foreach { case (yesNoPage, page, url, submitAction) =>

      s"for the '$page' page" - {

        val form = formProvider(page)

        "must return OK and the correct view for a GET" in new Fixture() {
          running(application) {
            val request = FakeRequest(GET, url)
            val result = route(application, request).value

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(form, page, submitAction)(dataRequest(request), messages(application)).toString
          }
        }

        "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers.set(page, Some("answer")))) {
          running(application) {
            val request = FakeRequest(GET, url)
            val result = route(application, request).value

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(form.fill(Some("answer")), page, submitAction)(dataRequest(request), messages(application)).toString
          }
        }

        "must redirect to the next page when valid data is submitted" in new Fixture() {
          running(application) {

            val updatedAnswers = emptyUserAnswers
              .set(yesNoPage, true)
              .set(page, Some("answer"))

            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

            val request =
              FakeRequest(POST, url)
                .withFormUrlEncodedBody(("more-information", "answer"))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual onwardRoute.url
          }
        }

        "must redirect to the next page when NO data is submitted" in new Fixture() {
          running(application) {

            val updatedAnswers = emptyUserAnswers
              .set(yesNoPage, false)
              .set(page, None)
            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))
              .returns(Future.successful(updatedAnswers))

            val request =
              FakeRequest(POST, url)
                .withFormUrlEncodedBody(("more-information", ""))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual onwardRoute.url
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
            contentAsString(result) mustEqual view(boundForm, page, submitAction)(dataRequest(request), messages(application)).toString
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
}
