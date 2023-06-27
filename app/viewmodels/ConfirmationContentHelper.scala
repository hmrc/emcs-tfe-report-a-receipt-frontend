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

import models.{AcceptMovement, ConfirmationDetails}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import views.html.partials.confirmation._

import javax.inject.Inject

class ConfirmationContentHelper @Inject()(shortageContent: ShortageContent,
                                          excessContent: ExcessContent,
                                          refusedContent: RefusedContent)  {
  def renderRefusedContent(confirmationDetails: ConfirmationDetails)(implicit messages: Messages): Option[HtmlFormat.Appendable] = {
    val isRefusedOrPartiallyRefused = confirmationDetails.receiptStatus match {
      case AcceptMovement.Refused.toString => true
      case AcceptMovement.PartiallyRefused.toString => true
      case _ => false
    }

    if (isRefusedOrPartiallyRefused) {
      Some(refusedContent())
    } else {
      None
    }
  }

  def renderShortageContent(confirmationDetails: ConfirmationDetails)(implicit messages: Messages): Option[HtmlFormat.Appendable] = {
    val hasMovementShortage = confirmationDetails.hasMovementShortage
    val hasItemShortage = confirmationDetails.hasItemShortage

    if(hasMovementShortage || hasItemShortage) {
      Some(shortageContent())
    } else {
      None
    }
  }

  def renderExcessContent(confirmationDetails: ConfirmationDetails)(implicit messages: Messages): Option[HtmlFormat.Appendable] = {
    val hasMovementExcess = confirmationDetails.hasMovementExcess
    val hasItemExcess = confirmationDetails.hasItemExcess

    if (hasMovementExcess || hasItemExcess) {
      Some(excessContent())
    } else {
      None
    }
  }
}