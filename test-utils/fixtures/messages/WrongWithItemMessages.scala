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

object WrongWithItemMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: Int => String
    val heading: Int => String
    val hint: String
    val moreOrLessThanExpected: String
    val shortage: String
    val excess: String
    val damaged: String
    val brokenSeals: String
    val other: String
    val requiredError: String
    val shortageOrExcessOnlyError: String
    val checkYourAnswersLabel: String
    val checkYourAnswersMoreOrLess: String
    val checkYourAnswersDamaged: String
    val checkYourAnswersBrokenSeals: String
    val checkYourAnswersOther: String
    val hiddenChangeLinkText: String
  }
  object English extends ViewMessages with BaseEnglish {
    override val heading: Int => String = i => s"What’s wrong with item $i?"
    override val title: Int => String = i => title(heading(i))
    override val hint = "Select all that apply."
    override val moreOrLessThanExpected: String = "I received a shortage or excess"
    override val shortage: String = "Shortage"
    override val excess: String = "Excess"
    override val damaged: String = "Damaged goods"
    override val brokenSeals: String = "Broken seal(s)"
    override val other: String = "Other"
    override val requiredError: String = "Select what’s wrong with this item"
    override val shortageOrExcessOnlyError: String = "Select either a shortage or an excess"
    override val checkYourAnswersLabel: String = "What was wrong"
    override val checkYourAnswersMoreOrLess: String = "Shortage"
    override val checkYourAnswersDamaged: String = "Damaged goods"
    override val checkYourAnswersBrokenSeals: String = "Broken seal(s)"
    override val checkYourAnswersOther: String = "Other"
    override val hiddenChangeLinkText: String = "what was wrong"
  }

}
