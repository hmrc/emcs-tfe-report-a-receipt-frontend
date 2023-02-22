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

object WrongWithMovementMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val hint: String
    val lessThanExpected: String
    val moreThanExpected: String
    val damaged: String
    val brokenSeals: String
    val other: String
    val requiredError: String
    val checkYourAnswersLabel: String
    val hiddenChangeLinkText: String
  }
  object English extends ViewMessages with BaseEnglish {
    override val heading = "What is wrong with this movement?"
    override val title = title(heading)
    override val hint = "Select all that apply."
    override val lessThanExpected: String = "I was sent less items than expected"
    override val moreThanExpected: String = "I was sent more items than expected"
    override val damaged: String = "Items were damaged"
    override val brokenSeals: String = "Broken seal(s)"
    override val other: String = "Other"
    override val requiredError: String = "Select what is wrong with the movement"
    override val checkYourAnswersLabel: String = "What is wrong with this movement?"
    override val hiddenChangeLinkText: String = "what is wrong with this movement"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val heading = "What is wrong with this movement?"
    override val title = title(heading)
    override val hint = "Select all that apply."
    override val lessThanExpected: String = "I was sent less items than expected"
    override val moreThanExpected: String = "I was sent more items than expected"
    override val damaged: String = "Items were damaged"
    override val brokenSeals: String = "Broken seal(s)"
    override val other: String = "Other"
    override val requiredError: String = "Select what is wrong with the movement"
    override val checkYourAnswersLabel: String = "What is wrong with this movement?"
    override val hiddenChangeLinkText: String = "what is wrong with this movement"
  }
}