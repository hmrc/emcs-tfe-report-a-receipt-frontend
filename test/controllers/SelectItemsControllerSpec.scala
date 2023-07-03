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
import mocks.services.{MockReferenceDataService, MockUserAnswersService}
import models.WrongWithMovement.Damaged
import models.{UserAnswers, WrongWithMovement}
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, CheckAnswersItemPage, SelectItemsPage, WrongWithItemPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.ReferenceDataService
import utils.JsonOptionFormatter
import views.html.SelectItemsView

import scala.concurrent.Future

class SelectItemsControllerSpec extends SpecBase
  with JsonOptionFormatter
  with MockUserAnswersService
  with MockReferenceDataService {

  lazy val loadListUrl: String = routes.SelectItemsController.onPageLoad(testErn, testArc).url

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[ReferenceDataService].toInstance(mockReferenceDataService)
      )
      .build()

    lazy val view = application.injector.instanceOf[SelectItemsView]
  }

  "SelectItems Controller" - {

    "when calling .onPageLoad()" - {

      "must return OK and the correct view for a GET" in new Fixture() {

        MockReferenceDataService.getCnCodeInformationWithMovementItems(Seq(item1, item2)).returns(
          Future.successful(Seq(
            item1WithReferenceData -> cnCodeInfo,
            item2WithReferenceData -> cnCodeInfo
          ))
        )

        val request = FakeRequest(GET, loadListUrl)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(Seq(
          item1WithReferenceData -> cnCodeInfo,
          item2WithReferenceData -> cnCodeInfo
        ))(dataRequest(request), messages(application)).toString
      }

      "must redirect to /add-to-list if filteredItems is empty" - {

        "when userAnswers only contains CheckAnswersItemPage = true" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemsPage(1), 1)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemsPage(2), 2)
            .set(CheckAnswersItemPage(2), true)
        )) {

          val request = FakeRequest(GET, loadListUrl)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.AddedItemsController.onPageLoad(testErn, testArc).url
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

        val request = FakeRequest(GET, loadListUrl)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "when calling .getFilteredItems" - {
      val controller = baseApplicationBuilder.build().injector.instanceOf[SelectItemsController]
      "must return the full unfiltered list" - {
        "when no items have checkAnswersItem = true" in {
          val userAnswers = emptyUserAnswers
            .set(SelectItemsPage(1), 1)
            .set(WrongWithItemPage(1), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(1), false)
            .set(SelectItemsPage(2), 2)
            .set(WrongWithItemPage(2), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(2), false)
          controller.incompleteItems()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq(item1, item2)
        }

        "when nothing has been selected yet" in {
          val userAnswers = emptyUserAnswers
          controller.incompleteItems()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq(item1, item2)
        }
      }

      "must return a filtered list" - {
        "when the first item has checkAnswersItem = true" in {
          val userAnswers = emptyUserAnswers
            .set(SelectItemsPage(1), 1)
            .set(WrongWithItemPage(1), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(1), false)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemsPage(2), 2)
            .set(WrongWithItemPage(2), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(2), false)
          controller.incompleteItems()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq(item2)
        }

        "when the second item has checkAnswersItem = true" in {
          val userAnswers = emptyUserAnswers
            .set(SelectItemsPage(1), 1)
            .set(WrongWithItemPage(1), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(1), false)
            .set(SelectItemsPage(2), 2)
            .set(WrongWithItemPage(2), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(2), false)
            .set(CheckAnswersItemPage(2), true)
          controller.incompleteItems()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq(item1)
        }

        "when both items have checkAnswersItem = true" in {
          val userAnswers = emptyUserAnswers
            .set(SelectItemsPage(1), 1)
            .set(WrongWithItemPage(1), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(1), false)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemsPage(2), 2)
            .set(WrongWithItemPage(2), Set[WrongWithMovement](Damaged))
            .set(AddItemDamageInformationPage(2), false)
            .set(CheckAnswersItemPage(2), true)
          controller.incompleteItems()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq()
        }
      }
    }
  }
}
