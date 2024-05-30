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

package fixtures.messages

object RefusedAmountMessages {

  sealed trait ViewMessages { _: i18n =>
    def title(unitOfMeasure: String): String
    def heading(unitOfMeasure: String): String
    val requiredError: String
    val nonNumeric: String
    val maxLength: Int => String
    val tooLarge: BigDecimal => String
    val notGreaterThanZero: String
    val threeDecimalPlaces: String
  }

  object English extends ViewMessages with BaseEnglish {
    override def heading(unitOfMeasureMsg: String) = s"How many $unitOfMeasureMsg of item 1 are you refusing?"
    override def title(unitOfMeasureMsg: String) = super.title(heading(unitOfMeasureMsg))

    override val requiredError: String = "Enter the amount you are refusing"
    override val nonNumeric: String = "The amount you are refusing must be a number, like 150 or 12.694"
    override val tooLarge: BigDecimal => String = (max: BigDecimal) => s"The amount you are refusing must be $max or less"
    override val maxLength: Int => String = (max: Int) => s"The amount you are refusing must be $max characters or less"
    override val notGreaterThanZero: String = "The amount you are refusing must be more than 0"
    override val threeDecimalPlaces: String = "The amount you are refusing must have 3 decimals or less"
  }

}
