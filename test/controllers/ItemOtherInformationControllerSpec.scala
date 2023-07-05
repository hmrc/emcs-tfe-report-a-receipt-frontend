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
import mocks.services.{MockReferenceDataService, MockUserAnswersService}
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.individualItems.{ItemOtherInformationPage, SelectItemsPage}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ReferenceDataService, UserAnswersService}
import views.html.ItemOtherInformationView

import scala.concurrent.Future

class ItemOtherInformationControllerSpec extends SpecBase
  with MockUserAnswersService
  with MockReferenceDataService {

  lazy val baseAnswers = emptyUserAnswers.set(SelectItemsPage(item1.itemUniqueReference), item1.itemUniqueReference)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(baseAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[ReferenceDataService].toInstance(mockReferenceDataService),
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      ).build()

    lazy val view = application.injector.instanceOf[ItemOtherInformationView]
  }

  def onwardRoute: Call = Call("GET", "/foo")

  val formProvider = new OtherInformationFormProvider()

  lazy val itemOtherInformationRoute: String = routes.ItemOtherInformationController.onPageLoad(testErn, testArc, idx = 1, NormalMode).url
  lazy val itemOtherInformationSubmitAction: Call = routes.ItemOtherInformationController.onSubmit(testErn, testArc, idx = 1, NormalMode)


  val (page, url, submitAction) = (ItemOtherInformationPage(1), itemOtherInformationRoute, itemOtherInformationSubmitAction)

  s"for the '$page' page" - {

    val form = formProvider(Some(page))

    "onPageLoad" - {

      "must return OK and the correct view for a GET" in new Fixture() {
        running(application) {

          MockReferenceDataService.itemWithReferenceData(item1).onCall {
            MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
          }

          val request = FakeRequest(GET, url)
          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(page, form, submitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
        }
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
        baseAnswers.set(page, "answer")
      )) {
        running(application) {

          MockReferenceDataService.itemWithReferenceData(item1).onCall {
            MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
          }

          val request = FakeRequest(GET, url)
          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(page, form.fill("answer"), submitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
        }
      }
    }

    "onSubmit" - {

      "must redirect to the next page when valid data is submitted" in new Fixture() {
        running(application) {

          val updatedAnswers = baseAnswers.set(page, "answer")
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

          MockReferenceDataService.itemWithReferenceData(item1).onCall {
            MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
          }

          val request =
            FakeRequest(POST, url)
              .withFormUrlEncodedBody(("more-information", ""))

          val boundForm = form.bind(Map("more-information" -> ""))
          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(page, boundForm, submitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
        }
      }

      "must return a Bad Request and errors when only whitespace is submitted" in new Fixture() {
        running(application) {

          MockReferenceDataService.itemWithReferenceData(item1).onCall {
            MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
          }

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
          contentAsString(result) mustEqual view(page, boundForm, submitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
        }
      }

      "must return a Bad Request and errors when a non-FormUrlEncodedBody is submitted" in new Fixture() {
        running(application) {

          MockReferenceDataService.itemWithReferenceData(item1).onCall {
            MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
          }

          val request =
            FakeRequest(POST, url)
              .withJsonBody(Json.obj("more-information" -> ""))

          val boundForm = form.bind(Map("more-information" -> ""))
          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(page, boundForm, submitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
        running(application) {

          MockReferenceDataService.itemWithReferenceData(item1).onCall {
            MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
          }

          val request =
            FakeRequest(POST, url)
              .withFormUrlEncodedBody(("more-information", "<>"))

          val boundForm = form.bind(Map("more-information" -> "<>"))
          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(page, boundForm, submitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
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
