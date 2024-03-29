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
import mocks.services.{MockGetCnCodeInformationService, MockUserAnswersService}
import mocks.viewmodels.MockCheckAnswersItemHelper
import models.WrongWithMovement.Damaged
import models.{UserAnswers, WrongWithMovement}
import navigation.{FakeNavigator, Navigator}
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, CheckAnswersItemPage, SelectItemsPage, WrongWithItemPage}
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetCnCodeInformationService, UserAnswersService}
import viewmodels.govuk.SummaryListFluency

import scala.concurrent.Future

class CheckYourAnswersItemControllerSpec extends SpecBase
  with SummaryListFluency
  with MockCheckAnswersItemHelper
  with MockUserAnswersService
  with MockGetCnCodeInformationService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          inject.bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
          inject.bind[UserAnswersService].toInstance(mockUserAnswersService),
          inject.bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService)
        )
        .build()
  }

  "Check Your Answers Controller" - {

    ".onPageLoad" - {

      "must redirect to the onward route" in new Fixture(Some(
        emptyUserAnswers
          .set(SelectItemsPage(1), 1)
          .set(WrongWithItemPage(1), Set[WrongWithMovement](Damaged))
          .set(AddItemDamageInformationPage(1), false)
      )) {
        running(application) {

          val updatedAnswers = userAnswers.get.set(CheckAnswersItemPage(1), true)
          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

          val request = FakeRequest(GET, routes.CheckYourAnswersItemController.onPageLoad(testErn, testArc, 1).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
      }

      "must redirect to SelectItemsController for a GET if no items are added" in new Fixture() {
        running(application) {

          val request = FakeRequest(GET, routes.CheckYourAnswersItemController.onPageLoad(testErn, testArc, 1).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.SelectItemsController.onPageLoad(testErn, testArc).url
        }
      }
    }
  }
}
