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

package views

trait BaseSelectors {

  val title = "title"
  val h1: String = "h1"
  def h2(i: Int) = s"main h2:nth-of-type($i)"
  val p: Int => String = i => s"main p:nth-of-type($i)"
  val link: Int => String = i => s"main a:nth-of-type($i)"
  val button = ".govuk-button"
  val secondaryButton = ".govuk-button--secondary"
  def radioButton(radioIndex: Int) = s".govuk-radios > div:nth-child($radioIndex) > label"
  val dateDay = s".govuk-date-input .govuk-date-input__item:nth-of-type(1)"
  val dateMonth = s".govuk-date-input .govuk-date-input__item:nth-of-type(2)"
  val dateYear = s".govuk-date-input .govuk-date-input__item:nth-of-type(3)"
}

object BaseSelectors extends BaseSelectors

