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

package models.requests

import models.{UserAnswers, TraderKnownFacts}
import models.response.emcsTfe.{GetMovementResponse, MovementItem}
import pages.unsatisfactory.individualItems.SelectItemsPage
import play.api.mvc.WrappedRequest

case class DataRequest[A](request: MovementRequest[A],
                          userAnswers: UserAnswers,
                          traderKnownFacts: TraderKnownFacts) extends WrappedRequest[A](request) {

  val internalId: String = request.internalId
  val ern: String = request.ern
  val arc: String = request.arc
  val movementDetails: GetMovementResponse = request.movementDetails

  def getItemDetails(idx: Int): Option[MovementItem] =
    userAnswers.get(SelectItemsPage(idx)).flatMap {
      reference =>
        request.movementDetails.item(reference)
    }

  def getAllCompletedItemDetails: Seq[MovementItem] =
    userAnswers.completedItems.flatMap {
      itemModel =>
        request.movementDetails.item(itemModel.itemUniqueReference)
    }
}
