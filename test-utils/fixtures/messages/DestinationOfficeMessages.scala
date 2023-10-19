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

object DestinationOfficeMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: String = "Were the goods received in Great Britain or Northern Ireland?"
    val title: String = s"$heading - Excise Movement and Control System - GOV.UK"
    val gb: String = "Great Britain"
    val ni: String = "Northern Ireland"
    val requiredError: String = "Select if the goods were received in Great Britain or Northern Ireland"
    val checkYourAnswersLabel: String = "Delivery location"
    val hiddenChangeLinkText: String = "the delivery location"
    val cyaGb: String = "Great Britain"
    val cyaNi: String = "Northern Ireland"
  }

  object English extends ViewMessages with BaseEnglish

  object Welsh extends ViewMessages with BaseWelsh
}
