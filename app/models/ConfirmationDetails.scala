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

package models

import play.api.i18n.Messages
import play.api.libs.json.{Json, OFormat}
import utils.DateUtils

import java.time.LocalDateTime

case class ConfirmationDetails(
                                receipt: String,
                                receiptDate: String,
                                receiptStatus: String,
                                hasMovementShortage: Boolean = false,
                                hasItemShortage: Boolean = false,
                                hasMovementExcess: Boolean = false,
                                hasItemExcess: Boolean = false
                              ) extends DateUtils {

  def formatReciptDateForUIOutput()(implicit messages: Messages): String = {
    LocalDateExtensions(LocalDateTime.parse(receiptDate).toLocalDate).formatDateForUIOutput()
  }

}

object ConfirmationDetails {
  implicit def format: OFormat[ConfirmationDetails] = Json.format[ConfirmationDetails]
}
