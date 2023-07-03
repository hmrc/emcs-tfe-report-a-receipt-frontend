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
import forms.DetailsSelectItemFormProvider
import mocks.services.{MockReferenceDataService, MockUserAnswersService}
import models.AcceptMovement.{PartiallyRefused, Unsatisfactory}
import models.{NormalMode, UserAnswers}
import pages.AcceptMovementPage
import pages.unsatisfactory.individualItems.SelectItemsPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.mvc.Results.Redirect
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ReferenceDataService, UserAnswersService}
import views.html.DetailsSelectItemView

import scala.concurrent.Future

class DetailsSelectItemControllerSpec extends SpecBase
  with MockUserAnswersService
  with MockReferenceDataService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[ReferenceDataService].toInstance(mockReferenceDataService),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      ).build()

    lazy val view = application.injector.instanceOf[DetailsSelectItemView]
  }

  def onwardRoute = Call("GET", "/foo")

  lazy val detailsSelectItemRoute = routes.DetailsSelectItemController.onPageLoad(testErn, testArc, 1).url

  val formProvider = new DetailsSelectItemFormProvider()
  val form = formProvider()

  "DetailsSelectItemPage Controller" - {

    "when calling .onPageLoad()" - {

      "must return OK and the correct view for a GET" in new Fixture() {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, detailsSelectItemRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
      }

      "must redirect to /select-items" - {

        "when the item doesn't exist in UserAnswers" in new Fixture() {

          val request = FakeRequest(GET, routes.DetailsSelectItemController.onPageLoad(testErn, testArc, 3).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
        }

        "when the call to get CN information returned no data" in new Fixture() {

          MockReferenceDataService.itemWithReferenceData(item1).returns(
            Future.successful(Redirect(routes.SelectItemsController.onPageLoad(testErn, testArc)))
          )

          val request = FakeRequest(GET, detailsSelectItemRoute)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
        }
      }

    }

    "when calling .onPageSubmit()" - {

      "must redirect to /select-items" - {

        "when the item doesn't exist in UserAnswers" in new Fixture() {

          val request = FakeRequest(POST, routes.DetailsSelectItemController.onPageLoad(testErn, testArc, 3).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
        }

        "when the user answer NO to the question on the page" in new Fixture() {

          val request = FakeRequest(POST, detailsSelectItemRoute).withFormUrlEncodedBody(("value", "false"))
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
        }
      }

      "when the user has partially refused the movement" - {

        "must redirect to /choose-refuse-item" - {

          "when the user answers YES to the question on the page" in new Fixture(Some(emptyUserAnswers.set(AcceptMovementPage, PartiallyRefused))) {

            val updatedAnswers = userAnswers.get.set(SelectItemsPage(1), item1.itemUniqueReference)

            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

            val request = FakeRequest(POST, detailsSelectItemRoute).withFormUrlEncodedBody(("value", "true"))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.RefusingAnyAmountOfItemController.onPageLoad(testErn, testArc, 1, NormalMode).url
          }
        }
      }

      "when the user has NOT partially refused the movement" - {

        "must redirect to /what-wrong-item" - {

          "when the user answers YES to the question on the page" in new Fixture(Some(emptyUserAnswers.set(AcceptMovementPage, Unsatisfactory))) {

            val updatedAnswers = userAnswers.get.set(SelectItemsPage(1), item1.itemUniqueReference)
            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

            val request = FakeRequest(POST, detailsSelectItemRoute).withFormUrlEncodedBody(("value", "true"))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.WrongWithMovementController.loadWrongWithItem(testErn, testArc, 1, NormalMode).url
          }
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val boundForm = form.bind(Map("value" -> ""))
        val request = FakeRequest(POST, detailsSelectItemRoute).withFormUrlEncodedBody(("value", ""))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
      }
    }
  }
}
