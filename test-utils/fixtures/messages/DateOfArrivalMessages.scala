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

object DateOfArrivalMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val hint: String
    val checkYourAnswersLabel: String
    val hiddenChangeLinkText: String
    val requiredError: String
    def twoRequiredError(field1: String, field2: String): String
    def oneRequiredError(field1: String): String
    val invalidDate: String
    def notBeforeDateOfDispatch(date: String): String
    val notInFuture: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title = "When did this movement arrive?"
    override val heading = "When did this movement arrive?"
    override val hint = "For example, 31 8 2022"
    override val checkYourAnswersLabel = "When the movement arrived"
    override val hiddenChangeLinkText = "when the movement arrived"
    override val requiredError = "Enter the date you received the movement"
    override def twoRequiredError(field1: String, field2: String) = s"The date you received the movement must include a $field1 and $field2"
    override def oneRequiredError(field: String): String = s"The date you received the movement must include a $field"
    override val invalidDate = "The date you received the movement must be a real date"
    override def notBeforeDateOfDispatch(date: String) = s"The date you received the movement must be the same as or after $date when the movement started"
    override val notInFuture = "The date you received the movement must be today or in the past"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title = "When did this movement arrive?"
    override val heading = "When did this movement arrive?"
    override val hint = "For example, 31 8 2022"
    override val checkYourAnswersLabel = "When the movement arrived"
    override val hiddenChangeLinkText = "when the movement arrived"
    override val requiredError = "Enter the date you received the movement"
    override def twoRequiredError(field1: String, field2: String) = s"The date you received the movement must include a $field1 and $field2"
    override def oneRequiredError(field: String): String = s"The date you received the movement must include a $field"
    override val invalidDate = "The date you received the movement must be a real date"
    override def notBeforeDateOfDispatch(date: String) = s"The date you received the movement must be the same as or after $date when the movement started"
    override val notInFuture = "The date you received the movement must be today or in the past"
  }
}
