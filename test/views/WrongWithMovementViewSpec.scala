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
import config.AppConfig
import fixtures.messages.WrongWithMovementMessages
import forms.WrongWithMovementFormProvider
import org.jsoup.Jsoup
import pages.unsatisfactory.WrongWithMovementPage
import play.api.test.FakeRequest
import views.html.WrongWithMovementView

class WrongWithMovementViewSpec extends ViewSpecBase with ViewBehaviours {

  lazy val view = app.injector.instanceOf[WrongWithMovementView]
  implicit val request: models.requests.DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

  object Selectors extends BaseSelectors

  "WrongWithMovement view" - {

    Seq(WrongWithMovementMessages.English, WrongWithMovementMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val config = app.injector.instanceOf[AppConfig]
        implicit val msgs = messages(app, messagesForLanguage.lang)
        lazy val form = app.injector.instanceOf[WrongWithMovementFormProvider].apply()
        implicit val doc = Jsoup.parse(view(WrongWithMovementPage, form, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.checkboxItem(1) -> messagesForLanguage.lessThanExpected,
          Selectors.checkboxItem(2) -> messagesForLanguage.moreThanExpected,
          Selectors.checkboxItem(3) -> messagesForLanguage.damaged,
          Selectors.checkboxItem(4) -> messagesForLanguage.brokenSeals,
          Selectors.checkboxItem(5) -> messagesForLanguage.other,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> messagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }
}
