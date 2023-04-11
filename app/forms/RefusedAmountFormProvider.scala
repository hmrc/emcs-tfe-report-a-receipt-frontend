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
import javax.inject.Inject
import play.api.data.Form

class RefusedAmountFormProvider @Inject() extends Mappings {

  def apply(itemQuantity: BigDecimal): Form[BigDecimal] =
    Form(
      "value" ->
        text("refusedAmount.error.required")
          .verifying(
            firstError(
              maxLength(MAX_LENGTH_15, "refusedAmount.error.maxLength"),
              regexp(NUMERIC_15_3DP_REGEX, "refusedAmount.error.nonNumeric")
            )
          )
          .transform[BigDecimal](BigDecimal(_), _.toString())
          .verifying(decimalMaxAmount(itemQuantity, "refusedAmount.error.tooLarge"))
    )
}
