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
import pages.unsatisfactory.individualItems.SelectItemsPage
import play.api.test.FakeRequest
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

class AddedItemsSummarySpec extends SpecBase {

  "AddedItemsSummary" - {

    lazy val acceptMovementSummary = new AddedItemsSummary()

    "should return the expected sequence of ListItem models" in {

      val answers = emptyUserAnswers
        .set(SelectItemsPage(1), item1.itemUniqueReference)
        .set(SelectItemsPage(2), item2.itemUniqueReference)

      implicit val req = dataRequest(FakeRequest(), answers)

      acceptMovementSummary.itemList() mustBe Seq(
        ListItem(
          item1.cnCode,
          routes.CheckYourAnswersItemController.onPageLoad(answers.ern, answers.arc, 1).url,
          testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
        ),
        ListItem(
          item2.cnCode,
          routes.CheckYourAnswersItemController.onPageLoad(answers.ern, answers.arc, 2).url,
          testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
        )
      )
    }
  }
}
