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
import pages.unsatisfactory.OtherInformationPage
import play.api.data.{Form, Mapping}
import play.api.data.Forms.optional
import play.api.data.Forms.{text => playText}

import javax.inject.Inject

class MoreInformationFormProvider @Inject() extends Mappings {

  private def validation(page: QuestionPage[Option[String]], isRequired: Boolean): Mapping[Option[String]] = optional(playText
    .transform[String](
      _.replace("\n", " ")
        .replace("\r", " ")
        .replaceAll(" +", " ")
        .trim,
      identity
    )
    .verifying(maxLength(350, s"$page.error.length"))
    .verifying(regexp("^(?s)(?=.*[A-Za-z0-9]).{1,}$", s"$page.error.character"))
    .verifying(regexp("^(?s)(?!.*javascript)(?!.*[<>;:]).{1,}$", s"$page.error.invalidCharacter"))
  ).verifying(required(isRequired, s"$page.error.required"))

  def apply(page: QuestionPage[Option[String]]): Form[Option[String]] =
    Form(
      "more-information" -> {
        page match {
          case OtherInformationPage => validation(page, isRequired = true)
          case _ => validation(page, isRequired = false)
        }
      }
    )
}
