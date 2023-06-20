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

object ShortageInformationMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val hint: String
    val validationErrorInvalidChars: String
    val validationError: String
    val lengthError: String
    val checkYourAnswersLabel: String
    val hiddenChangeLink: String
    val addMoreInformation: String
    val savePreviousAnswersAndExitLinkText: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val heading = "Give information about receiving less goods than expected"
    override val title: String = title(heading)
    override val hint = "Give more information (optional)"
    override val validationErrorInvalidChars = "Information must not include < and > and : and ;"
    override val validationError = "Information must contain letters or numbers"
    override val lengthError = "Information must be 350 characters or less"
    override val checkYourAnswersLabel = "Information about shortage"
    override val hiddenChangeLink = "information about shortage"
    override val addMoreInformation = "Enter more information about shortage (optional)"
    override val savePreviousAnswersAndExitLinkText = "Save previous answers and exit"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val heading = "Give information about receiving less goods than expected"
    override val title: String = title(heading)
    override val hint = "Give more information (optional)"
    override val validationErrorInvalidChars = "Information must not include < and > and : and ;"
    override val validationError = "Information must contain letters or numbers"
    override val lengthError = "Information must be 350 characters or less"
    override val checkYourAnswersLabel = "Information about shortage"
    override val hiddenChangeLink = "information about shortage"
    override val addMoreInformation = "Enter more information about shortage (optional)"
    override val savePreviousAnswersAndExitLinkText = "Save previous answers and exit"
  }
}
