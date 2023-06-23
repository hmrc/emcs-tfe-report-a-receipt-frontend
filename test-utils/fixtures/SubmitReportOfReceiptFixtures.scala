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

package fixtures

import models.AcceptMovement._
import models.DestinationType.TaxWarehouse
import models.response.emcsTfe.SubmitReportOfReceiptResponse
import models.submitReportOfReceipt.SubmitReportOfReceiptModel
import play.api.libs.json.Json

import java.time.LocalDate

trait SubmitReportOfReceiptFixtures extends BaseFixtures
  with TraderModelFixtures
  with ReceiptedItemsModelFixtures {

  val arrivalDate = LocalDate.now()
  val destinationOfficeId = "GB000434"

  val maxSubmitReportOfReceiptModel = SubmitReportOfReceiptModel(
    arc = testArc,
    sequenceNumber = 1,
    destinationType = TaxWarehouse,
    consigneeTrader = Some(maxTraderModel),
    deliveryPlaceTrader = Some(maxTraderModel.copy(eoriNumber = None)),
    destinationOffice = destinationOfficeId,
    dateOfArrival = arrivalDate,
    acceptMovement = PartiallyRefused,
    individualItems = Seq(
      excessReceiptedItemsModel,
      excessReceiptedItemsModel.copy(eadBodyUniqueReference = 2)
    ),
    otherInformation = Some("other")
  )


  val minSubmitReportOfReceiptModel = SubmitReportOfReceiptModel(
    arc = testArc,
    sequenceNumber = 1,
    destinationType = TaxWarehouse,
    consigneeTrader = Some(minTraderModel),
    deliveryPlaceTrader = Some(minTraderModel),
    destinationOffice = destinationOfficeId,
    dateOfArrival = arrivalDate,
    acceptMovement = Satisfactory,
    individualItems = Seq(),
    otherInformation = None
  )

  val successResponse = SubmitReportOfReceiptResponse(receipt = testConfirmationReference, receiptDate = testReceiptDate)
  val successResponseJson = Json.obj("receipt" -> testConfirmationReference, "receiptDate" -> testReceiptDate)

}
