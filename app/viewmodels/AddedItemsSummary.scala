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

import controllers.routes
import models.ItemModel
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import play.api.libs.json.__
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

class AddedItemsSummary  {

  def itemList()(implicit request: DataRequest[_]): Seq[ListItem] = {
    request.userAnswers.getList[Int](__ \ "items")(MovementItem.readItemUniqueReference).zipWithIndex.flatMap {
      case (uniqueReference, idx) =>
        request.movementDetails.copy(items = getFilteredItems).item(uniqueReference).map { item =>
          val pageIdx = idx + 1 // zipWithIndex is zero-indexed so need to + 1 to match what we're doing everywhere else
          ListItem(
            name = item.cnCode,
            routes.CheckYourAnswersItemController.onPageLoad(request.ern, request.arc, pageIdx).url,
            testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
          )
        }
    }
  }

  private def getFilteredItems(implicit request: DataRequest[_]): Seq[MovementItem] = {
    val userAnswerItems: Seq[ItemModel] = request.userAnswers.getList(__ \ "items")(ItemModel.reads)

    request.movementDetails.items.filter {
      movementDetailsItem =>
        userAnswerItems
          .find(_.itemUniqueReference == movementDetailsItem.itemUniqueReference)
          .exists(_.checkAnswersItem.contains(true))
    }
  }
}