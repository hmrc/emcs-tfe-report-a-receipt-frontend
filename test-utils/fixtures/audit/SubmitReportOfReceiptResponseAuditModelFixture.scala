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

package fixtures.audit


import models.audit.SubmitReportOfReceiptResponseAuditModel
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

object SubmitReportOfReceiptResponseAuditModelFixture {
  val time = LocalDate.now().toString

  val submitRORResponseAuditModel: SubmitReportOfReceiptResponseAuditModel =
    SubmitReportOfReceiptResponseAuditModel("credId", "internalId", "correlationId", "arc", "ern", "receipt")

  val submitRORResponseAuditJson : JsValue = Json.parse(
      s"""
        |{
        |   "credentialId": "credId",
        |   "internalId": "internalId",
        |   "correlationId": "correlationId",
        |   "arc": "arc",
        |   "traderId": "ern",
        |   "receipt": "receipt"
        |}
        |""".stripMargin)

}
