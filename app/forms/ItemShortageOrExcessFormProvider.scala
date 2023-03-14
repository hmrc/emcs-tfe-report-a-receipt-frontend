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

package forms

import forms.mappings.Mappings
import models.{ItemShortageOrExcessModel, WrongWithMovement}
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text => playText}

import javax.inject.Inject

class ItemShortageOrExcessFormProvider @Inject() extends Mappings {

  def apply(): Form[ItemShortageOrExcessModel] =
    Form(mapping(
      "shortageOrExcess" -> enumerable[WrongWithMovement]("itemShortageOrExcess.shortageOrExcess.error.required"),
      "amount" -> text("itemShortageOrExcess.amount.error.required")
        .verifying(
          firstError(
            maxLength(15, "itemShortageOrExcess.amount.error.maxLength"),
            regexp(NUMERIC_15_3DP_REGEX, "itemShortageOrExcess.amount.error.regex")
          )
        ).transform[BigDecimal](BigDecimal(_), _.toString()),
      "additionalInfo" -> optional(playText().transform[String](x =>
        x.replace("\n", " ")
          .replace("\r", " ")
          .replaceAll(" +", " ")
          .trim,
        identity
      )
        .verifying(maxLength(TEXTAREA_MAX_LENGTH, s"itemShortageOrExcess.additionalInfo.error.length"))
        .verifying(regexp("^(?s)(?=.*[A-Za-z0-9]).{1,}$", s"itemShortageOrExcess.additionalInfo.error.character"))
        .verifying(regexp("^(?s)(?!.*javascript)(?!.*[<>;:]).{1,}$", s"itemShortageOrExcess.additionalInfo.error.invalidCharacter"))
      )
    )(ItemShortageOrExcessModel.apply)(ItemShortageOrExcessModel.unapply))
}
