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

package forms

import forms.mappings.Mappings
import pages.QuestionPage
import play.api.data.Form

import javax.inject.Inject

class OtherInformationFormProvider @Inject() extends BaseFormProvider[String] with Mappings {
  def apply(page: Option[QuestionPage[String]]): Form[String] = {
    assert(page.isDefined) // only use Some(_) with this FormProvider
    val p = page.get
    Form(
      "more-information" -> text(errorKey = s"$p.error.required")
        .transform[String](
          _.replace("\n", " ")
            .replace("\r", " ")
            .replaceAll(" +", " ")
            .trim,
          identity
        )
        .verifying(maxLength(TEXTAREA_MAX_LENGTH, s"$p.error.length"))
        .verifying(regexp(ALPHANUMERIC_REGEX, s"$p.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, s"$p.error.invalidCharacter"))
    )
  }
}
