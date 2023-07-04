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
import forms.WrongWithItemFormProvider
import mocks.services.{MockReferenceDataService, MockUserAnswersService}
import models.{NormalMode, UserAnswers, WrongWithMovement}
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.individualItems.{SelectItemsPage, WrongWithItemPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ReferenceDataService, UserAnswersService}
import views.html.WrongWithItemView

import scala.concurrent.Future

class WrongWithItemControllerSpec extends SpecBase
  with MockUserAnswersService
  with MockReferenceDataService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[ReferenceDataService].toInstance(mockReferenceDataService),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    implicit lazy val config = application.injector.instanceOf[AppConfig]
    lazy val view = application.injector.instanceOf[WrongWithItemView]
  }

  def onwardRoute: Call = Call("GET", "/foo")

  val formProvider = new WrongWithItemFormProvider()

  lazy val page = WrongWithItemPage(1)
  lazy val url: String = routes.WrongWithItemController.loadWrongWithItem(testErn, testArc, 1, NormalMode).url
  lazy val submitAction: Call = routes.WrongWithItemController.submitWrongWithItem(testErn, testArc, 1, NormalMode)

  "WrongWithItemController" - {

    val form = formProvider()

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall {
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        }

        val request = FakeRequest(GET, url)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(page, form, submitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application), config).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      emptyUserAnswers.set(page, WrongWithMovement.values.toSet)
    )) {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall {
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        }

        val request = FakeRequest(GET, url)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(page, form.fill(WrongWithMovement.values.toSet), submitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application), config).toString
      }
    }

    "must redirect to the next page when valid data is submitted" - {

      val options: Set[WrongWithMovement] =
        Set(
          WrongWithMovement.ShortageOrExcess,
          WrongWithMovement.Damaged,
          WrongWithMovement.BrokenSeals,
          WrongWithMovement.Other,
        )

      options.foreach {
        option =>
          s"and only keep option $option when $option is selected" in new Fixture(
            Some(emptyUserAnswers
              .set(page, options)
              .set(SelectItemsPage(1), 1))
          ) {

            val updatedAnswers: UserAnswers =
              emptyUserAnswers
                .set(page, Set(option))
                .set(SelectItemsPage(1), 1)

            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

            running(application) {
              val request =
                FakeRequest(POST, url)
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

        MockReferenceDataService.itemWithReferenceData(item1).onCall {
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        }

        val request =
          FakeRequest(POST, url)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(page, boundForm, submitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application), config).toString
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
            .withFormUrlEncodedBody(("value[0]", WrongWithMovement.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
