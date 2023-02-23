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

object HowMuchIsWrongMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val wholeMovement: String
    val individualItem: String
    val requiredError: String
    val checkYourAnswersLabel: String
    val hiddenChangeLinkText: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title = title("How much of this movement is wrong?")
    override val heading = "How much of this movement is wrong?"
    override val wholeMovement = "The whole movement"
    override val individualItem = "Individual item(s) in this movement"
    override val requiredError = "Select how much of the movement is wrong"
    override val checkYourAnswersLabel = "Amount of the movement that is wrong"
    override val hiddenChangeLinkText = "how much of this movement is wrong"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title = title("How much of this movement is wrong?")
    override val heading = "How much of this movement is wrong?"
    override val wholeMovement = "The whole movement"
    override val individualItem = "Individual item(s) in this movement"
    override val requiredError = "Select how much of the movement is wrong"
    override val checkYourAnswersLabel = "Amount of the movement that is wrong"
    override val hiddenChangeLinkText = "how much of this movement is wrong"
  }
}
