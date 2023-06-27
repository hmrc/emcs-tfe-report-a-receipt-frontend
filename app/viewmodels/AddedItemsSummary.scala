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
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import models.{ItemModel, ListItemWithProductCode, NormalMode}

class AddedItemsSummary  {

  def itemList()(implicit request: DataRequest[_]): Seq[ListItemWithProductCode] = {
    request.userAnswers.itemReferences.flatMap {
      uniqueReference =>
        request.movementDetails.copy(items = getFilteredItems).item(uniqueReference).map { item =>
          ListItemWithProductCode(
            productCode = item.productCode,
            cnCode = item.cnCode,
            // TODO: This will be updated as part of the add-to-list alignment story
            changeUrl = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
            removeUrl = routes.RemoveItemController.onPageLoad(request.ern, request.arc, uniqueReference, NormalMode).url
          )
        }
    }
  }

  private def getFilteredItems(implicit request: DataRequest[_]): Seq[MovementItem] = {
    val completedItems: Seq[ItemModel] = request.userAnswers.completedItems

    request.movementDetails.items.filter {
      movementDetailsItem =>
        completedItems.exists(_.itemUniqueReference == movementDetailsItem.itemUniqueReference)
    }
  }
}