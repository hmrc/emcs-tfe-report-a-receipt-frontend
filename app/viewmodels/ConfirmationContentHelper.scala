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

import models.AcceptMovement.{PartiallyRefused, Refused}
import models.WrongWithMovement.{Excess, Shortage}
import models.requests.DataRequest
import pages.AcceptMovementPage
import pages.unsatisfactory.WrongWithMovementPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import views.html.partials.confirmation._

import javax.inject.Inject

class ConfirmationContentHelper @Inject()(shortageContent: ShortageContent,
                                          excessContent: ExcessContent,
                                          refusedContent: RefusedContent)  {
  def renderRefusedContent()(implicit request: DataRequest[_], messages: Messages): Option[HtmlFormat.Appendable] = {
    val isRefusedOrPartiallyRefused = request.userAnswers.get(AcceptMovementPage).exists(
      acceptMovement => acceptMovement == Refused || acceptMovement == PartiallyRefused
    )
    if (isRefusedOrPartiallyRefused) {
      Some(refusedContent())
    } else {
      None
    }
  }

  def renderShortageContent()(implicit request: DataRequest[_], messages: Messages): Option[HtmlFormat.Appendable] = {

    val hasMovementShortage = request.userAnswers.get(WrongWithMovementPage).exists(_.contains(Shortage))
    val hasItemShortage = request.getItemsAdded.exists(_.itemShortageOrExcess.exists(_.wrongWithItem == Shortage))

    println("********************\n" + request.getItemsAdded + "\n*********************")

    if(hasMovementShortage || hasItemShortage) {
      Some(shortageContent())
    } else {
      None
    }
  }

  def renderExcessContent()(implicit request: DataRequest[_], messages: Messages): Option[HtmlFormat.Appendable] = {
    val hasMovementExcess = request.userAnswers.get(WrongWithMovementPage).exists(_.contains(Excess))
    val hasItemExcess = request.getItemsAdded.exists(_.itemShortageOrExcess.exists(_.wrongWithItem == Excess))
    if (hasMovementExcess || hasItemExcess) {
      Some(excessContent())
    } else {
      None
    }
  }
}