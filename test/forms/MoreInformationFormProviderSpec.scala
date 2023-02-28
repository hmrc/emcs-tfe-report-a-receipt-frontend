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
import pages.MoreInformationPage
import pages.unsatisfactory.{ExcessInformationPage, ShortageInformationPage}
import play.api.data.FormError

class MoreInformationFormProviderSpec extends StringFieldBehaviours {

  val maxLength = 350
  val aboveMaxLength = 351

  Seq(MoreInformationPage, ShortageInformationPage, ExcessInformationPage) foreach { page =>

    s"loading the form for the '$page' page" - {

      val form = new MoreInformationFormProvider()(page)

      ".value" - {

        "more information form accepts a value that is valid" in {
          val data = Map("more-information" -> "Test 123.")
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some("Test 123.")
        }

        "more information form accepts a value that includes a Carriage Return" in {
          val data = Map("more-information" -> "Test\n123.")
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some("Test 123.")
        }


        "more information form accepts a value of 350 alpha characters that are valid" in {
          val data = Map("more-information" -> "a" * maxLength)
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some("a" * maxLength)
        }

        "more information form accepts a value that begins with a valid special character" in {
          val data = Map("more-information" -> ".A")
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some(".A")
        }

        "more information form accepts just numbers" in {
          val data = Map("more-information" -> "123")
          val result = form.bind(data)

          result.errors mustBe Seq()
          result.value.flatten mustBe Some("123")
        }

        "return an error if alpha numeric data isn't used" in {
          val data = Map("more-information" -> "..")
          val result = form.bind(data)

          result.errors must contain only FormError("more-information", s"$page.error.character", Seq("^(?s)(?=.*[A-Za-z0-9]).{1,}$"))
        }

        "return an error if more than 350 characters are used" in {
          val data = Map("more-information" -> "a" * aboveMaxLength)
          val result = form.bind(data)

          result.errors must contain only FormError("more-information", s"$page.error.length", Seq(maxLength))
        }

        "return errors if invalid characters are used" in {
          val data = Map("more-information" -> "<>")
          val result = form.bind(data)

          result.errors must contain only(
            FormError("more-information", s"$page.error.character", Seq("^(?s)(?=.*[A-Za-z0-9]).{1,}$")),
            FormError("more-information", s"$page.error.invalidCharacter", Seq("^(?s)(?!.*javascript)(?!.*[<>;:]).{1,}$"))
          )
        }
      }
    }
  }
}
