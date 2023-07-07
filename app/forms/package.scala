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

package object forms {
  private[forms] val MAX_LENGTH_15 = 15
  private[forms] val MIN_VALUE_0 = 0
  private[forms] val TEXTAREA_MAX_LENGTH = 350
  private[forms] val ALPHANUMERIC_REGEX = "^(?s)(?=.*[A-Za-z0-9]).{1,}$"
  private[forms] val XSS_REGEX = "^(?s)(?!.*javascript)(?!.*[<>;:]).{1,}$"
  private[forms] val NUMERIC_15_3DP_REGEX: String =
    "^[1-9]\\d{0,14}$|^([1-9]\\d{0,13}|0)\\.[0-9]$|^([1-9]\\d{0,12}|0)\\.\\d[0-9]$|^([1-9]\\d{0,11}|0)\\.\\d\\d[0-9]$"

  private[forms] val NUMERIC_REGEX: String = "^(([0-9]*)|(([0-9]*)\\.([0-9]*)))$" // This regex does not accept comma separated numbers e.g. 2,000,000
}
