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

import fixtures.messages.{wrongWithItemMessages, WrongWithMovementMessages}
import forms.behaviours.CheckboxFieldBehaviours
import models.WrongWithMovement
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.unsatisfactory.WrongWithMovementPage
import pages.unsatisfactory.individualItems.WrongWithItemPage
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class WrongWithMovementFormProviderSpec extends CheckboxFieldBehaviours with GuiceOneAppPerSuite {

  Seq(
    WrongWithMovementPage,
    WrongWithItemPage(1)
  ) foreach { page =>

    s"loading the form for the '$page' page" - {

      val form = new WrongWithMovementFormProvider()(page)

      ".value" - {

        val fieldName = "value"
        val requiredKey = s"$page.error.required"

        behave like checkboxField[WrongWithMovement](
          form,
          fieldName,
          validValues = WrongWithMovement.values,
          invalidError = FormError(s"$fieldName[0]", "error.invalid")
        )

        behave like mandatoryCheckboxField(
          form,
          fieldName,
          requiredKey
        )
      }
    }
  }

  "Error Messages" - {

    "for the WrongWithMovementPage" - {

      Seq(WrongWithMovementMessages.English, WrongWithMovementMessages.Welsh) foreach { messagesForLanguage =>

        implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        s"when output for language code '${messagesForLanguage.lang.code}'" - {

          "have the correct error message when no option is selected" in {
            messages(s"$WrongWithMovementPage.error.required") mustBe messagesForLanguage.requiredError
          }
        }
      }
    }

    "for the wrongWithItemPage" - {

      Seq(wrongWithItemMessages.English, wrongWithItemMessages.Welsh) foreach { messagesForLanguage =>

        implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        s"when output for language code '${messagesForLanguage.lang.code}'" - {

          "have the correct error message when no option is selected" in {
            messages(s"${WrongWithItemPage(1)}.error.required") mustBe messagesForLanguage.requiredError
          }
        }
      }
    }
  }
}
