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

package models.audit

import models.submitReportOfReceipt.{SubmitReportOfReceiptModel, TraderModel}
import play.api.libs.json.{JsValue, Json}


case class SubmitReportOfReceiptAuditModel(credentialId: String,
                                           internalId: String,
                                           correlationId: String,
                                           submission: SubmitReportOfReceiptModel,
                                           ern: String) extends AuditModel {

  val movement = if(submission.acceptMovement.toString == "satisfactory") "accepted" else submission.acceptMovement.toString

  val individualItemsJsonBlock =
    if (submission.individualItems.isEmpty) Json.obj() else Json.obj("individualItems" -> submission.individualItems)

  override val detail: JsValue =
    Json.obj(
      "credentialId" -> credentialId,
      "internalId" -> internalId,
      "correlationId" -> correlationId,
      "ern" -> ern,
      "arc" -> submission.arc,
      "sequenceNumber" -> submission.sequenceNumber,
    ) ++ submission.consigneeTrader.fold(Json.obj())(traderDetail =>
      Json.obj("consigneeTrader"  -> Json.toJson(traderDetail)(TraderModel.auditWrites))) ++
      submission.deliveryPlaceTrader.fold(Json.obj())(traderDetail =>
        Json.obj("deliveryPlaceTrader"  -> Json.toJson(traderDetail)(TraderModel.auditWrites))) ++
      Json.obj(
        "destinationOffice" -> submission.destinationOffice,
        "dateOfArrival" -> submission.dateOfArrival.toString,
        "acceptMovement" ->  movement
      ) ++
      individualItemsJsonBlock ++
      Json.obj("otherInformation" -> submission.otherInformation)


  override val auditType: String = "SubmitReportOfReceipt"
}
