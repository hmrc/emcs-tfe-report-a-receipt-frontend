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
import featureswitch.core.config.{FeatureSwitching, NewShortageExcessFlow}
import fixtures.messages.WrongWithItemMessages
import forms.WrongWithItemFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import pages.unsatisfactory.individualItems.WrongWithItemPage
import play.api.test.FakeRequest
import views.html.WrongWithItemView

class WrongWithItemViewSpec extends ViewSpecBase with ViewBehaviours with FeatureSwitching {

  lazy val view = app.injector.instanceOf[WrongWithItemView]
  implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

  object Selectors extends BaseSelectors

  "WrongWithItem view for an Individual Item" - {

    Seq(WrongWithItemMessages.English, WrongWithItemMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        lazy val form = app.injector.instanceOf[WrongWithItemFormProvider].apply()
        implicit val config = app.injector.instanceOf[AppConfig]

        "when the feature switch for the new flow is enabled" - {

          enable(NewShortageExcessFlow)

          implicit val doc = Jsoup.parse(view(WrongWithItemPage(1), form, testOnwardRoute, item1, cnCodeInfo).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(1),
            Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
            Selectors.h1 -> messagesForLanguage.heading(1),
            Selectors.hint -> messagesForLanguage.hint,
            Selectors.checkboxItem(1) -> messagesForLanguage.shortage,
            Selectors.checkboxItem(2) -> messagesForLanguage.excess,
            Selectors.checkboxItem(3) -> messagesForLanguage.damaged,
            Selectors.checkboxItem(4) -> messagesForLanguage.brokenSeals,
            Selectors.checkboxItem(5) -> messagesForLanguage.other,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.id("save-and-exit") -> messagesForLanguage.savePreviousAnswersAndExit
          ))
        }

        "when the feature switch for the new flow is disabled" - {

          disable(NewShortageExcessFlow)

          implicit val doc = Jsoup.parse(view(WrongWithItemPage(1), form, testOnwardRoute, item1, cnCodeInfo).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(1),
            Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
            Selectors.h1 -> messagesForLanguage.heading(1),
            Selectors.hint -> messagesForLanguage.hint,
            Selectors.checkboxItem(1) -> messagesForLanguage.moreOrLessThanExpected,
            Selectors.checkboxItem(2) -> messagesForLanguage.damaged,
            Selectors.checkboxItem(3) -> messagesForLanguage.brokenSeals,
            Selectors.checkboxItem(4) -> messagesForLanguage.other,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.id("save-and-exit") -> messagesForLanguage.savePreviousAnswersAndExit
          ))
        }
      }
    }
  }
}
