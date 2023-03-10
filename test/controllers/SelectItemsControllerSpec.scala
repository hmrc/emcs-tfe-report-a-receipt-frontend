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
import mocks.services.MockUserAnswersService
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.individualItems.SelectItemsPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import utils.JsonOptionFormatter
import views.html.SelectItemsView

import scala.concurrent.Future

class SelectItemsControllerSpec extends SpecBase with JsonOptionFormatter with MockUserAnswersService {

  lazy val loadListUrl: String = routes.SelectItemsController.onPageLoad(testErn, testArc).url
  lazy val addItemUrl: String = routes.SelectItemsController.addItemToList(testErn, testArc, item1.itemUniqueReference).url

  "SelectItems Controller" - {

    "when calling .onPageLoad()" - {

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, loadListUrl)

          val result = route(application, request).value

          val view = application.injector.instanceOf[SelectItemsView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(Seq(item1))(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request = FakeRequest(GET, loadListUrl)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "when calling .addItem()" - {

      "when no items have been added" - {

        "must save the UniqueReference of the item to the array of items and redirect to onward route" in {

          val updatedAnswers = emptyUserAnswers.set(SelectItemsPage(1), item1.itemUniqueReference)

          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

          val application =
            applicationBuilder(userAnswers = Some(emptyUserAnswers))
              .overrides(
                bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
                bind[UserAnswersService].toInstance(mockUserAnswersService)
              )
              .build()

          running(application) {
            val request = FakeRequest(GET, addItemUrl)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(testOnwardRoute.url)
          }
        }
      }

      "when items have already been added" - {

        "must save the UniqueReference of the item to the next IDX array of items and redirect to onward route" in {

          val userAnswers = emptyUserAnswers.set(SelectItemsPage(1), item2.itemUniqueReference)
          val updatedAnswers = userAnswers.set(SelectItemsPage(2), item1.itemUniqueReference)

          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

          val application =
            applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(
                bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
                bind[UserAnswersService].toInstance(mockUserAnswersService)
              )
              .build()

          running(application) {
            val request = FakeRequest(GET, addItemUrl)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(testOnwardRoute.url)
          }
        }
      }

      "when adding the same item again" - {

        "must redirect to onward route without saving any data to the backend as it's unchanged" in {

          val userAnswers = emptyUserAnswers
            .set(SelectItemsPage(1), item1.itemUniqueReference)
            .set(SelectItemsPage(2), item2.itemUniqueReference)

          MockUserAnswersService.set().never()

          val application =
            applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(
                bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
                bind[UserAnswersService].toInstance(mockUserAnswersService)
              )
              .build()

          running(application) {
            val request = FakeRequest(GET, addItemUrl)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(testOnwardRoute.url)
          }
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request = FakeRequest(GET, loadListUrl)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}
