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
import models.HowMuchIsWrong.IndividualItem
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import pages.QuestionPage
import pages.unsatisfactory.HowMuchIsWrongPage
import pages.unsatisfactory.individualItems.{CheckAnswersItemPage, RemoveItemPage, SelectItemsPage}
import play.api.inject.bind
import play.api.libs.json.JsPath
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.AddMoreInformationView

import scala.concurrent.Future

class RemoveItemControllerSpec extends SpecBase with MockUserAnswersService {

  private val idx = 1

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AddMoreInformationFormProvider()

  lazy val removeItemRoute: Call = routes.RemoveItemController.onPageLoad(testErn, testArc, idx)
  lazy val removeItemSubmitAction: Call = routes.RemoveItemController.onSubmit(testErn, testArc, idx)

  lazy val page: QuestionPage[Boolean] = RemoveItemPage(1)

  lazy val baseAnswers: UserAnswers = emptyUserAnswers
    .set(HowMuchIsWrongPage, IndividualItem)
    .set(SelectItemsPage(1), item1.itemUniqueReference)
    .set(CheckAnswersItemPage(1), true)

  s"for the '$page' page" - {

    val form = formProvider(page)

    "RemoveItem Controller" - {

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, removeItemRoute.url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[AddMoreInformationView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, page, removeItemSubmitAction)(dataRequest(request), messages(application)).toString
        }
      }

      "must populate the view correctly on a GET when the question has previously been answered" in {

        val userAnswers = baseAnswers.set(page, true)

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, removeItemRoute.url)

          val view = application.injector.instanceOf[AddMoreInformationView]

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form.fill(true), page, removeItemSubmitAction)(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to the next page when valid data is submitted (true)" in {

        object ItemsPage extends QuestionPage[Seq[String]] {
          override def path: JsPath = JsPath \ "items"
        }

        val updatedAnswers = emptyUserAnswers
          .set(HowMuchIsWrongPage, IndividualItem)
          .set(ItemsPage, Seq())
        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers)).once()

        val application =
          applicationBuilder(userAnswers = Some(baseAnswers))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(POST, removeItemSubmitAction.url)
              .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must redirect to the next page when valid data is submitted (false)" in {

        val updatedAnswers = baseAnswers.set(page, false)

        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers)).once()

        val application =
          applicationBuilder(userAnswers = Some(baseAnswers))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(POST, removeItemSubmitAction.url)
              .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

        running(application) {
          val request =
            FakeRequest(POST, removeItemSubmitAction.url)
              .withFormUrlEncodedBody(("value", ""))

          val boundForm = form.bind(Map("value" -> ""))

          val view = application.injector.instanceOf[AddMoreInformationView]

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(boundForm, page, removeItemSubmitAction)(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request = FakeRequest(GET, removeItemRoute.url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request =
            FakeRequest(POST, removeItemSubmitAction.url)
              .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}
