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

import forms.behaviours.StringFieldBehaviours
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems.ItemOtherInformationPage
import play.api.data.FormError

class OtherInformationFormProviderSpec extends StringFieldBehaviours {

  val maxLength = 350
  val aboveMaxLength = 351

  Seq(
    OtherInformationPage,
    ItemOtherInformationPage(1)
  ).foreach { page =>

    s"loading the form for the '$page' page" - {

      val form = new OtherInformationFormProvider()(Some(page))

      ".value" - {

        "form returns no errors when" - {
          "input is valid" in {
            val data = Map("more-information" -> "Test 123.")
            val result = form.bind(data)

            result.errors mustBe Seq()
            result.value mustBe Some("Test 123.")
          }

          "input includes a Carriage Return" in {
            val data = Map("more-information" -> "Test\n123.")
            val result = form.bind(data)

            result.errors mustBe Seq()
            result.value mustBe Some("Test 123.")
          }


          "input is a value of 350 alpha characters that are valid" in {
            val data = Map("more-information" -> "a" * maxLength)
            val result = form.bind(data)

            result.errors mustBe Seq()
            result.value mustBe Some("a" * maxLength)
          }

          "input begins with a valid special character" in {
            val data = Map("more-information" -> ".A")
            val result = form.bind(data)

            result.errors mustBe Seq()
            result.value mustBe Some(".A")
          }

          "input is just numbers" in {
            val data = Map("more-information" -> "123")
            val result = form.bind(data)

            result.errors mustBe Seq()
            result.value mustBe Some("123")
          }
        }

        "form returns an error when" - {
          "alpha numeric data isn't used" in {
            val data = Map("more-information" -> "..")
            val result = form.bind(data)

            result.errors must contain only FormError("more-information", s"$page.error.character", Seq(ALPHANUMERIC_REGEX))
          }

          "more than 350 characters are used" in {
            val data = Map("more-information" -> "a" * aboveMaxLength)
            val result = form.bind(data)

            result.errors must contain only FormError("more-information", s"$page.error.length", Seq(maxLength))
          }

          "invalid characters are used" in {
            val data = Map("more-information" -> "<>")
            val result = form.bind(data)

            result.errors must contain only (Seq(
              FormError("more-information", s"$page.error.character", Seq(ALPHANUMERIC_REGEX)),
              FormError("more-information", s"$page.error.invalidCharacter", Seq(XSS_REGEX))
            ): _*)
          }

          "the required field is empty" in {
            val data = Map("more-information" -> "")
            val result = form.bind(data)

            result.errors must contain only FormError("more-information", s"$page.error.required")
          }

          "the required field contains only whitespace" in {
            val data = Map("more-information" ->
              """
                |
                |
                |
                |
                |
                |
                |""".stripMargin)
            val result = form.bind(data)

            result.errors must contain only FormError("more-information", s"$page.error.required")
          }

          "the required field is not present" in {
            val data = Map("something" -> "")
            val result = form.bind(data)

            result.errors must contain only FormError("more-information", s"$page.error.required")
          }
        }

      }
    }
  }
}
