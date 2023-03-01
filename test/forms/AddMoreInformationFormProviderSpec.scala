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

import fixtures.messages.{AddDamageInformationMessages, AddExcessInformationMessages, AddMoreInformationMessages, AddSealsInformationMessages, AddShortageInformationMessages}
import forms.behaviours.BooleanFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.AddMoreInformationPage
import pages.unsatisfactory.{AddDamageInformationPage, AddExcessInformationPage, AddSealsInformationPage, AddShortageInformationPage}
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class AddMoreInformationFormProviderSpec extends BooleanFieldBehaviours with GuiceOneAppPerSuite {

  Seq(
    AddMoreInformationPage,
    AddShortageInformationPage,
    AddExcessInformationPage,
    AddDamageInformationPage,
    AddSealsInformationPage
  ) foreach { page =>

    s"loading the form for the '$page' page" - {

      ".value" - {

        val fieldName = "value"
        val form = new AddMoreInformationFormProvider()(page)
        val requiredKey = s"$page.error.required"
        val invalidKey = "error.boolean"

        behave like booleanField(
          form,
          fieldName,
          invalidError = FormError(fieldName, invalidKey)
        )

        behave like mandatoryField(
          form,
          fieldName,
          requiredError = FormError(fieldName, requiredKey)
        )
      }
    }
  }

  "Error Messages" - {

    "for the AddMoreInformationPage" - {

      Seq(AddMoreInformationMessages.English, AddMoreInformationMessages.Welsh) foreach { messagesForLanguage =>

        implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        s"when output for language code '${messagesForLanguage.lang.code}'" - {

          "have the correct error message when no option is selected" in {
            messages(s"$AddMoreInformationPage.error.required") mustBe messagesForLanguage.requiredError
          }
        }
      }
    }

    "for the AddShortageInformationPage" - {

      Seq(AddShortageInformationMessages.English, AddShortageInformationMessages.Welsh) foreach { messagesForLanguage =>

        implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        s"when output for language code '${messagesForLanguage.lang.code}'" - {

          "have the correct error message when no option is selected" in {
            messages(s"$AddShortageInformationPage.error.required") mustBe messagesForLanguage.requiredError
          }
        }
      }
    }

    "for the AddExcessInformationPage" - {

      Seq(AddExcessInformationMessages.English, AddExcessInformationMessages.Welsh) foreach { messagesForLanguage =>

        implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        s"when output for language code '${messagesForLanguage.lang.code}'" - {

          "have the correct error message when no option is selected" in {
            messages(s"$AddExcessInformationPage.error.required") mustBe messagesForLanguage.requiredError
          }
        }
      }
    }

    "for the AddDamageInformationPage" - {

      Seq(AddDamageInformationMessages.English, AddDamageInformationMessages.Welsh) foreach { messagesForLanguage =>

        implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        s"when output for language code '${messagesForLanguage.lang.code}'" - {

          "have the correct error message when no option is selected" in {
            messages(s"$AddDamageInformationPage.error.required") mustBe messagesForLanguage.requiredError
          }
        }
      }
    }

    "for the AddSealsInformationPage" - {

      Seq(AddSealsInformationMessages.English, AddSealsInformationMessages.Welsh) foreach { messagesForLanguage =>

        implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        s"when output for language code '${messagesForLanguage.lang.code}'" - {

          "have the correct error message when no option is selected" in {
            messages(s"$AddSealsInformationPage.error.required") mustBe messagesForLanguage.requiredError
          }
        }
      }
    }
  }
}
