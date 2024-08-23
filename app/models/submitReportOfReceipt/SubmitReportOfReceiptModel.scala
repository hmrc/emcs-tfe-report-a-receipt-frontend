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

import config.AppConfig
import models.DestinationType.TemporaryRegisteredConsignee
import models.{AcceptMovement, DestinationType, UserAnswers}
import models.WrongWithMovement._
import models.response.emcsTfe.GetMovementResponse
import pages.{AcceptMovementPage, DateOfArrivalPage, MoreInformationPage}
import play.api.libs.json.{Json, OFormat}
import utils.{JsonOptionFormatter, ModelConstructorHelpers}

import java.time.LocalDate

case class SubmitReportOfReceiptModel(arc: String,
                                      sequenceNumber: Int,
                                      destinationType: DestinationType,
                                      consigneeTrader: Option[TraderModel],
                                      deliveryPlaceTrader: Option[TraderModel],
                                      destinationOffice: String,
                                      dateOfArrival: LocalDate,
                                      acceptMovement: AcceptMovement,
                                      individualItems: Seq[ReceiptedItemsModel],
                                      otherInformation: Option[String])

object SubmitReportOfReceiptModel extends JsonOptionFormatter with ModelConstructorHelpers {

  implicit val fmt: OFormat[SubmitReportOfReceiptModel] = Json.format

  private[models] val DESTINATION_OFFICE_PREFIX_GB = "GB"
  private[models] val DESTINATION_OFFICE_PREFIX_XI = "XI"

  private[models] def destinationOfficePrefix(deliveryPlaceTrader: Option[TraderModel])(implicit userAnswers: UserAnswers): String = {
    if(userAnswers.isNorthernIrelandTrader) {
      deliveryPlaceTrader.flatMap(_.traderExciseNumber).map(_.take(2)).getOrElse(DESTINATION_OFFICE_PREFIX_XI)
    } else {
      DESTINATION_OFFICE_PREFIX_GB
    }
  }

  private[models] def consigneeTraderDetails(movementDetails: GetMovementResponse)(implicit userAnswers: UserAnswers): Option[TraderModel] = {
    (movementDetails.destinationType, movementDetails.consigneeTrader) match {
      case (TemporaryRegisteredConsignee, Some(consignee: TraderModel)) =>
        Some(consignee.copy(traderExciseNumber = Some(userAnswers.ern)))
      case (_, consignee) =>
        consignee
    }
  }

  def apply(movementDetails: GetMovementResponse)(implicit userAnswers: UserAnswers, appConfig: AppConfig): SubmitReportOfReceiptModel = {

    SubmitReportOfReceiptModel(
      arc = movementDetails.arc,
      sequenceNumber = movementDetails.sequenceNumber,
      destinationType = movementDetails.destinationType,
      consigneeTrader = consigneeTraderDetails(movementDetails),
      deliveryPlaceTrader = movementDetails.deliveryPlaceTrader,
      destinationOffice = destinationOfficePrefix(movementDetails.deliveryPlaceTrader) + appConfig.destinationOfficeSuffix,
      dateOfArrival = mandatoryPage(DateOfArrivalPage),
      acceptMovement = mandatoryPage(AcceptMovementPage),
      individualItems = ReceiptedItemsModel(movementDetails),
      otherInformation = userAnswers.get(MoreInformationPage).flatten
    )
  }
}
