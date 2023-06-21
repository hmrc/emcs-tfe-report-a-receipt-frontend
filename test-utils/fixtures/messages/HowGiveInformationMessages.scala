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

object HowGiveInformationMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val wholeMovement: String
    val individualItem: String
    val requiredError: String
    val checkYourAnswersLabel: String
    val checkYourAnswersWholeMovement: String
    val checkYourAnswersIndividualItem: String
    val hiddenChangeLinkText: String
    val savePreviousAnswersAndExitLinkText: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title = title("How do you want to give information about this movement?")
    override val heading = "How do you want to give information about this movement?"
    override val wholeMovement = "I want to give information about the whole movement"
    override val individualItem = "I want to choose which item(s) to give information about"
    override val requiredError = "Select how you want to give information about this movement"
    override val checkYourAnswersLabel = "Amount of the movement that is wrong"
    override val checkYourAnswersWholeMovement = "Whole movement"
    override val checkYourAnswersIndividualItem = "Individual items"
    override val hiddenChangeLinkText = "how much of this movement is wrong"
    override val savePreviousAnswersAndExitLinkText = "Save previous answers and exit"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title = title("How do you want to give information about this movement?")
    override val heading = "How do you want to give information about this movement?"
    override val wholeMovement = "I want to give information about the whole movement"
    override val individualItem = "I want to choose which item(s) to give information about"
    override val requiredError = "Select how you want to give information about this movement"
    override val checkYourAnswersLabel = "Amount of the movement that is wrong"
    override val checkYourAnswersWholeMovement = "Whole movement"
    override val checkYourAnswersIndividualItem = "Individual items"
    override val hiddenChangeLinkText = "how much of this movement is wrong"
    override val savePreviousAnswersAndExitLinkText = "Save previous answers and exit"
  }
}
