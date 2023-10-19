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
import models.WrongWithMovement._
import models.response.emcsTfe.GetMovementResponse
import models.{AcceptMovement, DestinationOffice, DestinationType, UserAnswers}
import pages.{AcceptMovementPage, DateOfArrivalPage, DestinationOfficePage, MoreInformationPage}
import play.api.libs.json.{Format, Json}
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

  implicit val fmt: Format[SubmitReportOfReceiptModel] = Json.format

  private[models] val DESTINATION_OFFICE_PREFIX_GB = "GB"
  private[models] val DESTINATION_OFFICE_PREFIX_XI = "XI"

  private[models] def destinationOfficePrefix(implicit userAnswers: UserAnswers): String = {
    if(userAnswers.isNorthernIrelandTrader) {
      mandatoryPage(DestinationOfficePage).toString
    } else {
      DestinationOffice.GreatBritain.toString
    }
  }

  def apply(movementDetails: GetMovementResponse)(implicit userAnswers: UserAnswers, appConfig: AppConfig): SubmitReportOfReceiptModel = {

    SubmitReportOfReceiptModel(
      arc = movementDetails.arc,
      sequenceNumber = movementDetails.sequenceNumber,
      destinationType = movementDetails.destinationType,
      consigneeTrader = movementDetails.consigneeTrader,
      deliveryPlaceTrader = movementDetails.deliveryPlaceTrader,
      destinationOffice = destinationOfficePrefix + appConfig.destinationOfficeSuffix,
      dateOfArrival = mandatoryPage(DateOfArrivalPage),
      acceptMovement = mandatoryPage(AcceptMovementPage),
      individualItems = ReceiptedItemsModel(movementDetails),
      otherInformation = userAnswers.get(MoreInformationPage).flatten
    )
  }
}
