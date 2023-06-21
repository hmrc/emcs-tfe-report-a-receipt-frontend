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

package models.submitReportOfReceipt

import models.HowGiveInformation.TheWholeMovement
import models.WrongWithMovement._
import models.response.emcsTfe.GetMovementResponse
import models.{UserAnswers, WrongWithMovement}
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems._
import play.api.libs.json.{Format, Json}
import utils.JsonOptionFormatter

case class ReceiptedItemsModel(eadBodyUniqueReference: Int,
                               productCode: String,
                               excessAmount: Option[BigDecimal],
                               shortageAmount: Option[BigDecimal],
                               refusedAmount: Option[BigDecimal],
                               unsatisfactoryReasons: Seq[UnsatisfactoryModel])

object ReceiptedItemsModel extends JsonOptionFormatter {

  implicit val fmt: Format[ReceiptedItemsModel] = Json.format

  private def getOptionalItemReason(item: Int, wrongWith: WrongWithMovement)(implicit userAnswers: UserAnswers): Option[String] = wrongWith match {
    case ShortageOrExcess => userAnswers.get(ItemShortageOrExcessPage(item)).flatMap(_.additionalInfo)
    case Damaged => userAnswers.get(ItemDamageInformationPage(item)).flatten
    case BrokenSeals => userAnswers.get(ItemSealsInformationPage(item)).flatten
    case _ => userAnswers.get(ItemOtherInformationPage(item))
  }

  private def getOptionalReason(wrongWith: WrongWithMovement)(implicit userAnswers: UserAnswers): Option[String] = wrongWith match {
    case Excess => userAnswers.get(ExcessInformationPage).flatten
    case Shortage => userAnswers.get(ShortageInformationPage).flatten
    case Damaged => userAnswers.get(DamageInformationPage).flatten
    case BrokenSeals => userAnswers.get(SealsInformationPage).flatten
    case _ => userAnswers.get(OtherInformationPage)
  }

  private def wholeMovementApply(movementDetails: GetMovementResponse)(implicit userAnswers: UserAnswers): Seq[ReceiptedItemsModel] = {
    movementDetails.items.map { item =>
      ReceiptedItemsModel(
        eadBodyUniqueReference = item.itemUniqueReference,
        productCode = item.productCode,
        excessAmount = None,
        shortageAmount = None,
        refusedAmount = None,
        unsatisfactoryReasons = userAnswers.get(WrongWithMovementPage).fold[Seq[UnsatisfactoryModel]](Seq())(_.map { wrongWith =>
          UnsatisfactoryModel(
            reason = wrongWith,
            additionalInformation = getOptionalReason(wrongWith)
          )
        }.toSeq.sortBy(_.reason.toString))
      )
    }
  }

  private def individualItemsApply(movementDetails: GetMovementResponse)(implicit userAnswers: UserAnswers): Seq[ReceiptedItemsModel] = {
    userAnswers
      .completedItems
      .flatMap { item =>
        movementDetails.item(item.itemUniqueReference).map { itemDetails =>
          val shortageOrExcess = userAnswers.get(ItemShortageOrExcessPage(item.itemUniqueReference))
          ReceiptedItemsModel(
            eadBodyUniqueReference = item.itemUniqueReference,
            productCode = itemDetails.productCode,
            excessAmount = shortageOrExcess.flatMap(_.excessAmount),
            shortageAmount = shortageOrExcess.flatMap(_.shortageAmount),
            refusedAmount = userAnswers.get(RefusedAmountPage(item.itemUniqueReference)),
            unsatisfactoryReasons = userAnswers.get(WrongWithItemPage(item.itemUniqueReference)).fold[Seq[UnsatisfactoryModel]](Seq())(
              _.flatMap { wrongWith =>
                if (wrongWith == ShortageOrExcess) {
                  shortageOrExcess.map { model =>
                    UnsatisfactoryModel(
                      reason = model.wrongWithItem,
                      additionalInformation = getOptionalItemReason(item.itemUniqueReference, wrongWith)
                    )
                  }
                } else {
                  Some(
                    UnsatisfactoryModel(
                      reason = wrongWith,
                      additionalInformation = getOptionalItemReason(item.itemUniqueReference, wrongWith)
                    )
                  )
                }
              }.toSeq.sortBy(_.reason.toString)
            )
          )
        }
      }
  }

  def apply(movementDetails: GetMovementResponse)(implicit userAnswers: UserAnswers): Seq[ReceiptedItemsModel] = {
    userAnswers.get(HowGiveInformationPage) match {
      case Some(TheWholeMovement) => wholeMovementApply(movementDetails)
      case _ => individualItemsApply(movementDetails)
    }
  }
}
