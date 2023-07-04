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
import models.HowGiveInformation.IndividualItem
import models.{Mode, NormalMode, ReviewMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.QuestionPage
import pages.unsatisfactory.HowGiveInformationPage
import pages.unsatisfactory.individualItems.{CheckAnswersItemPage, RemoveItemPage, SelectItemsPage}
import play.api.inject.bind
import play.api.libs.json.{JsObject, JsPath, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.RemoveItemView

import scala.concurrent.Future

class RemoveItemControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val baseAnswers: UserAnswers = emptyUserAnswers
    .set(HowGiveInformationPage, IndividualItem)
    .set(SelectItemsPage(1), item1.itemUniqueReference)
    .set(CheckAnswersItemPage(1), true)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(baseAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    lazy val view = application.injector.instanceOf[RemoveItemView]
  }

  private val idx = 1

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AddMoreInformationFormProvider()

  def removeItemRoute(mode: Mode = NormalMode): Call = routes.RemoveItemController.onPageLoad(testErn, testArc, idx, mode)
  def removeItemSubmitAction(mode: Mode = NormalMode): Call = routes.RemoveItemController.onSubmit(testErn, testArc, idx, mode)

  lazy val page: RemoveItemPage = RemoveItemPage(1)

  s"for the '$page' page" - {

    val form = formProvider(page)

    "RemoveItem Controller" - {

      "must return OK and the correct view for a GET" in new Fixture() {
        running(application) {

          val request = FakeRequest(GET, removeItemRoute().url)
          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, page, removeItemSubmitAction())(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to the AddedItemsController when valid data is submitted (true) and remove the item" in new Fixture() {
        running(application) {

          object ItemsPage extends QuestionPage[JsObject] {
            override def path: JsPath = JsPath \ "items"
          }

          val updatedAnswers = emptyUserAnswers
            .set(HowGiveInformationPage, IndividualItem)
            .set(ItemsPage, Json.obj())
          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers)).once()

          val request =
            FakeRequest(POST, removeItemSubmitAction().url)
              .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.AddedItemsController.onPageLoad(testErn, testArc).url
        }
      }

      "must redirect to the AddedItems page when valid data is submitted (false) without removing the item (in NormalMode)" in new Fixture() {
        running(application) {

          val request =
            FakeRequest(POST, removeItemSubmitAction().url)
              .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.AddedItemsController.onPageLoad(testErn, testArc).url
        }
      }

      "must redirect to the CheckAnswers page when valid data is submitted (false) without removing the item (in ReviewMode)" in new Fixture() {
        running(application) {

          val request =
            FakeRequest(POST, removeItemSubmitAction(ReviewMode).url)
              .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad(testErn, testArc).url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
        running(application) {

          val request =
            FakeRequest(POST, removeItemSubmitAction().url)
              .withFormUrlEncodedBody(("value", ""))

          val boundForm = form.bind(Map("value" -> ""))
          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(boundForm, page, removeItemSubmitAction())(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
        running(application) {

          val request = FakeRequest(GET, removeItemRoute().url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
        running(application) {

          val request =
            FakeRequest(POST, removeItemSubmitAction().url)
              .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }
    }
  }
}
