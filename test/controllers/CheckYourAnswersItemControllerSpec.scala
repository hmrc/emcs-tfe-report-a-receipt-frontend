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
import mocks.viewmodels.MockCheckAnswersItemHelper
import models.WrongWithMovement
import models.WrongWithMovement.Damaged
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, CheckAnswersItemPage, SelectItemsPage, WrongWithItemPage}
import play.api.inject
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import viewmodels.checkAnswers.CheckAnswersItemHelper
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersItemView

import scala.concurrent.Future

class CheckYourAnswersItemControllerSpec extends SpecBase with SummaryListFluency with MockCheckAnswersItemHelper with MockUserAnswersService {

  private lazy val userAnswers = emptyUserAnswers
    .set(SelectItemsPage(1), 1)
    .set(WrongWithItemPage(1), Set[WrongWithMovement](Damaged))
    .set(AddItemDamageInformationPage(1), false)

  "Check Your Answers Controller" - {

    ".onPageLoad" - {

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(inject.bind[CheckAnswersItemHelper].toInstance(mockCheckAnswersItemHelper))
          .build()

        running(application) {

          val list = SummaryListViewModel(Seq.empty)

          MockCheckAnswersItemHelper.summaryList().returns(list)
          MockCheckAnswersItemHelper.itemName().returns("name")

          val request = FakeRequest(GET, routes.CheckYourAnswersItemController.onPageLoad(testErn, testArc, 1).url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[CheckYourAnswersItemView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            routes.CheckYourAnswersItemController.onSubmit(testErn, testArc, 1),
            "name",
            list
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request = FakeRequest(GET, routes.CheckYourAnswersItemController.onPageLoad(testErn, testArc, 1).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }

      "must redirect to Journey Recovery for a GET if no item is found in userAnswers" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, routes.CheckYourAnswersItemController.onPageLoad(testErn, testArc, 1).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
        }
      }
    }

    ".onSubmit" - {

      "must redirect to the onward route" in {

        val updatedAnswers = userAnswers.set(CheckAnswersItemPage(1), true)

        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(
              inject.bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {

          val request = FakeRequest(POST, routes.CheckYourAnswersItemController.onSubmit(testErn, testArc, 1).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val request = FakeRequest(POST, routes.CheckYourAnswersItemController.onSubmit(testErn, testArc, 1).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
        }
      }
    }
  }
}