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
import models.DestinationType.{Export, TemporaryRegisteredConsignee}
import models.GBOrXI.{GB, XI, fromTwoChars}
import models.WrongWithMovement._
import models.response.emcsTfe.GetMovementResponse
import models.{AcceptMovement, DestinationType, GBOrXI, UserAnswers}
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

  private[models] def destinationOfficePrefix(maybeDeliveryPlaceTrader: Option[TraderModel])(implicit userAnswers: UserAnswers): GBOrXI = {
    val gbOrXiFromUserAnswers = if (userAnswers.isNorthernIrelandTrader) XI else GB

    // If this Option is Some, it's value can only be GB or XI.
    // We allow for the possibility that it might be neither (None) because the DeliveryPlaceTrader's trader ID can be anything when the DestinationType
    // is not a warehouse.
    // Even though it seems most EMCS systems in the UK and EU use ERNs for trader ID regardless of DestinationType,
    // we have seen non-ERNs in use in the real world - see DLSC-1775 for an example.
    val maybeGBOrXIFromDeliveryPlaceTraderPrefix = for {
      deliveryPlaceTrader <- maybeDeliveryPlaceTrader
      traderId <- deliveryPlaceTrader.traderId
      gbOrXi <- fromTwoChars(traderId.take(2))
    } yield gbOrXi

    // If the ERN from the user's answers starts with XI, and the DeliveryPlaceTrader's trader ID starts with GB or XI, we use the latter.
    // Otherwise, we use the GB or XI from the user's answers.
    // We've checked with Core that this behaviour is correct.
    (gbOrXiFromUserAnswers, maybeGBOrXIFromDeliveryPlaceTraderPrefix) match {
      case (XI, Some(gbOrXiFromPrefix)) => gbOrXiFromPrefix
      case _ => gbOrXiFromUserAnswers
    }
  }

  private[models] def consigneeTraderDetails(movementDetails: GetMovementResponse)(implicit userAnswers: UserAnswers): Option[TraderModel] = {
    (movementDetails.destinationType, movementDetails.consigneeTrader) match {
      case (TemporaryRegisteredConsignee, Some(consignee: TraderModel)) =>
        Some(consignee.copy(traderId = Some(userAnswers.ern)))
      case (destinationType, Some(consignee: TraderModel)) if (destinationType != Export) =>
        Some(consignee.copy(eoriNumber = None))
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
      destinationOffice = s"${destinationOfficePrefix(movementDetails.deliveryPlaceTrader)}${appConfig.destinationOfficeSuffix}",
      dateOfArrival = mandatoryPage(DateOfArrivalPage),
      acceptMovement = mandatoryPage(AcceptMovementPage),
      individualItems = ReceiptedItemsModel(movementDetails),
      otherInformation = userAnswers.get(MoreInformationPage).flatten
    )
  }
}
