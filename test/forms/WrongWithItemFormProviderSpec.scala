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

import fixtures.messages.WrongWithItemMessages
import forms.behaviours.CheckboxFieldBehaviours
import models.WrongWithMovement
import models.WrongWithMovement.{Excess, Shortage}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.unsatisfactory.individualItems.WrongWithItemPage
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class WrongWithItemFormProviderSpec extends CheckboxFieldBehaviours with GuiceOneAppPerSuite {

  val form = new WrongWithItemFormProvider()()

  val fieldName = "value"
  val requiredKey = s"${WrongWithItemPage(1)}.error.required"
  val shortageOrExcessOnly = s"${WrongWithItemPage(1)}.error.shortageOrExcessOnly"

  s".$fieldName" - {

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

    "error if both Shortage and Excess are picked" in {
      form.bind(Map(
        s"$fieldName[0]" -> Shortage.toString,
        s"$fieldName[1]" -> Excess.toString
      )).errors must contain(FormError(s"$fieldName", shortageOrExcessOnly))
    }
  }

  "Error Messages" - {

    Seq(WrongWithItemMessages.English) foreach { messagesForLanguage =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message when no option is selected" in {
          messages(requiredKey) mustBe messagesForLanguage.requiredError
        }

        "have the correct error message when both Shortage and Excess are selected" in {
          messages(shortageOrExcessOnly) mustBe messagesForLanguage.shortageOrExcessOnlyError
        }
      }
    }
  }
}
