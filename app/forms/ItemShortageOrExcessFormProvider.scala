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
import models.WrongWithMovement.{Excess, Shortage}
import models.{ItemShortageOrExcessModel, WrongWithMovement}
import pages.QuestionPage
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text => playText}
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject

class ItemShortageOrExcessFormProvider @Inject() extends BaseFormProvider[ItemShortageOrExcessModel] with Mappings {

  def apply(page: Option[QuestionPage[ItemShortageOrExcessModel]] = None): Form[ItemShortageOrExcessModel] =
    Form(mapping(
      "shortageOrExcess" ->
        enumerable[WrongWithMovement]("itemShortageOrExcess.shortageOrExcess.error.required")
          .verifying(isShortageOrExcess())
      ,
      "amount" ->
        text("itemShortageOrExcess.amount.error.required")
          .verifying(
            firstError(
              regexp(NUMERIC_REGEX, "itemShortageOrExcess.amount.error.isNotNumeric"),
              greaterThanValue(MIN_VALUE_0.toString, "itemShortageOrExcess.amount.error.notGreaterThanZero"),
              decimalMaxLength(MAX_LENGTH_15, "itemShortageOrExcess.amount.error.maxLength"),
              regexp(NUMERIC_15_3DP_REGEX, "itemShortageOrExcess.amount.error.threeDecimalPlaces")
            )
          ).transform[BigDecimal](BigDecimal(_), _.toString()),
      "additionalInfo" ->
        optional(
          playText()
            .transform[String](_.replace("\n", " ")
              .replace("\r", " ")
              .replaceAll(" +", " ")
              .trim,
              identity
            )
            .verifying(maxLength(TEXTAREA_MAX_LENGTH, s"itemShortageOrExcess.additionalInfo.error.maxLength"))
            .verifying(regexpUnlessEmpty(ALPHANUMERIC_REGEX, s"itemShortageOrExcess.additionalInfo.error.character"))
            .verifying(regexpUnlessEmpty(XSS_REGEX, s"itemShortageOrExcess.additionalInfo.error.invalidCharacter"))
        )
    )(ItemShortageOrExcessModel.apply)(ItemShortageOrExcessModel.unapply))

  private def isShortageOrExcess(): Constraint[WrongWithMovement] =
    Constraint {
      case Shortage | Excess => Valid
      case _ => Invalid("itemShortageOrExcess.shortageOrExcess.error.required")
    }
}
