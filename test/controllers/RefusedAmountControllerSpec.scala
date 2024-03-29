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
import forms.RefusedAmountFormProvider
import mocks.services.{MockReferenceDataService, MockUserAnswersService}
import models.UnitOfMeasure.Kilograms
import models.WrongWithMovement.Shortage
import models.{ItemShortageOrExcessModel, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, RefusedAmountPage, SelectItemsPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ReferenceDataService, UserAnswersService}
import views.html.RefusedAmountView

import scala.concurrent.Future

class RefusedAmountControllerSpec extends SpecBase with MockUserAnswersService with MockReferenceDataService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[ReferenceDataService].toInstance(mockReferenceDataService)
      )
      .build()

    lazy val view = application.injector.instanceOf[RefusedAmountView]
  }

  val formProvider = new RefusedAmountFormProvider()
  val form = formProvider(itemQuantity = 10, None)

  lazy val url = "testurl"

  def onwardRoute = Call("GET", "/foo")

  val validAnswer: BigDecimal = 1.001

  lazy val refusedAmountRoute = routes.RefusedAmountController.onPageLoad(testErn, testArc, 1, NormalMode).url
  lazy val refusedAmountSubmitAction = routes.RefusedAmountController.onSubmit(testErn, testArc, 1, NormalMode)

  "RefusedAmount Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers.set(SelectItemsPage(1), 1))) {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, refusedAmountRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, refusedAmountSubmitAction, Kilograms, item1WithReferenceData, cnCodeInfo, 1)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      emptyUserAnswers
        .set(SelectItemsPage(1), 1)
        .set(RefusedAmountPage(1), validAnswer)
    )) {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, refusedAmountRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), refusedAmountSubmitAction, Kilograms, item1WithReferenceData, cnCodeInfo, 1)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(
      emptyUserAnswers
        .set(SelectItemsPage(1), 1)
        .set(ItemShortageOrExcessPage(1), ItemShortageOrExcessModel(Shortage, 5, None))
    )) {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(userAnswers.get))

        val request =
          FakeRequest(POST, refusedAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers.set(SelectItemsPage(1), 1))) {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request =
          FakeRequest(POST, refusedAmountRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, refusedAmountSubmitAction, Kilograms, item1WithReferenceData, cnCodeInfo, 1)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {
        val request = FakeRequest(GET, refusedAmountRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request =
          FakeRequest(POST, refusedAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
