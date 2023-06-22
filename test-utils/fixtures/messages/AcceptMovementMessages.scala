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

object AcceptMovementMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val satisfactory: String
    val unsatisfactory: String
    val refused: String
    val partiallyRefused: String
    val requiredError: String
    val checkYourAnswersLabel: String
    val hiddenChangeLinkText: String
    val cyaSatisfactory: String
    val cyaUnsatisfactory: String
    val cyaRefused: String
    val cyaPartiallyRefused: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title = title("Do you want to accept this movement?")
    override val heading = "Do you want to accept this movement?"
    override val satisfactory = "Yes, it’s satisfactory"
    override val unsatisfactory = "Yes, but it’s unsatisfactory"
    override val refused = "No, I want to refuse the whole movement"
    override val partiallyRefused = "No, I want to partially refuse the movement"
    override val requiredError = "Select if you want to accept this movement"
    override val checkYourAnswersLabel = "Receipt status"
    override val hiddenChangeLinkText = "the receipt status"
    override val cyaSatisfactory = "Accepted and satisfactory"
    override val cyaUnsatisfactory = "Accepted and unsatisfactory"
    override val cyaRefused = "Refused"
    override val cyaPartiallyRefused = "Partially refused"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title = title("Do you want to accept this movement?")
    override val heading = "Do you want to accept this movement?"
    override val satisfactory = "Yes, it’s satisfactory"
    override val unsatisfactory = "Yes, but it’s unsatisfactory"
    override val refused = "No, I want to refuse the whole movement"
    override val partiallyRefused = "No, I want to partially refuse the movement"
    override val requiredError: String = "Select if you want to accept this movement"
    override val checkYourAnswersLabel = "Receipt status"
    override val hiddenChangeLinkText = "the receipt status"
    override val cyaSatisfactory = "Accepted and satisfactory"
    override val cyaUnsatisfactory = "Accepted and unsatisfactory"
    override val cyaRefused = "Refused"
    override val cyaPartiallyRefused = "Partially refused"
  }
}
