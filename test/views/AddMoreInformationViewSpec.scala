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

package views

import base.ViewSpecBase
import fixtures.messages.AddMoreInformationMessages
import forms.AddMoreInformationFormProvider
import models.NormalMode
import org.jsoup.Jsoup
import play.api.test.FakeRequest
import views.html.AddMoreInformationView

class AddMoreInformationViewSpec extends ViewSpecBase with ViewBehaviours {

  lazy val form = app.injector.instanceOf[AddMoreInformationFormProvider].apply()
  lazy val view = app.injector.instanceOf[AddMoreInformationView]

  object Selectors extends BaseSelectors

  "AcceptMovement view" - {

    Seq(AddMoreInformationMessages.English, AddMoreInformationMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
        implicit val doc = Jsoup.parse(view(form, NormalMode).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }
}
