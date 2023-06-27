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

package viewmodels

import base.SpecBase
import controllers.routes
import models.{ListItemWithProductCode, NormalMode}
import pages.unsatisfactory.individualItems.{CheckAnswersItemPage, SelectItemsPage}
import play.api.test.FakeRequest

class AddedItemsSummarySpec extends SpecBase {

  "AddedItemsSummary" - {

    lazy val acceptMovementSummary = new AddedItemsSummary()

    "should return the expected sequence of ListItemWithProductCode models" in {

      val answers = emptyUserAnswers
        .set(SelectItemsPage(1), item1.itemUniqueReference)
        .set(CheckAnswersItemPage(1), true)
        .set(SelectItemsPage(2), item2.itemUniqueReference)
        .set(CheckAnswersItemPage(2), true)

      implicit val req = dataRequest(FakeRequest(), answers)

      acceptMovementSummary.itemList() mustBe Seq(
        ListItemWithProductCode(
          productCode = item1.productCode,
          cnCode = item1.cnCode,
          changeUrl = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
          removeUrl = routes.RemoveItemController.onPageLoad(answers.ern, answers.arc, 1, NormalMode).url
        ),
        ListItemWithProductCode(
          productCode = item2.productCode,
          cnCode = item2.cnCode,
          changeUrl = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
          removeUrl = routes.RemoveItemController.onPageLoad(answers.ern, answers.arc, 2, NormalMode).url
        )
      )
    }

    "should return a filtered Seq" - {
      "when some items don't have CheckAnswersItemPage = true" in {

        val answers = emptyUserAnswers
          .set(SelectItemsPage(1), item1.itemUniqueReference)
          .set(SelectItemsPage(2), item2.itemUniqueReference)
          .set(CheckAnswersItemPage(2), true)

        implicit val req = dataRequest(FakeRequest(), answers)

        acceptMovementSummary.itemList() mustBe Seq(
          ListItemWithProductCode(
            productCode = item2.productCode,
            cnCode = item2.cnCode,
            changeUrl = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
            removeUrl = routes.RemoveItemController.onPageLoad(answers.ern, answers.arc, 2, NormalMode).url
          )
        )
      }
      "when no items have CheckAnswersItemPage = true" in {

        val answers = emptyUserAnswers
          .set(SelectItemsPage(1), item1.itemUniqueReference)
          .set(CheckAnswersItemPage(1), false)
          .set(SelectItemsPage(2), item2.itemUniqueReference)
          .set(CheckAnswersItemPage(2), false)

        implicit val req = dataRequest(FakeRequest(), answers)

        acceptMovementSummary.itemList() mustBe Seq()
      }
      "when no items have CheckAnswersItemPage" in {

        val answers = emptyUserAnswers
          .set(SelectItemsPage(1), item1.itemUniqueReference)
          .set(SelectItemsPage(2), item2.itemUniqueReference)

        implicit val req = dataRequest(FakeRequest(), answers)

        acceptMovementSummary.itemList() mustBe Seq()
      }
    }
  }
}
