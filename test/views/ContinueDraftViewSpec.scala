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
import fixtures.messages.ContinueDraftMessages
import org.jsoup.Jsoup
import play.api.test.FakeRequest
import views.html.ContinueDraftView

class ContinueDraftViewSpec extends ViewSpecBase with ViewBehaviours {

  lazy val view = app.injector.instanceOf[ContinueDraftView]

  object Selectors extends BaseSelectors

  "For the ContinueDraftView view" - {

    Seq(ContinueDraftMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = optionalDataRequest(FakeRequest())
        implicit val doc = Jsoup.parse(view(
          controllers.routes.IndexController.continueOrStartAgain(testErn, testArc, continueDraft = true),
          controllers.routes.IndexController.continueOrStartAgain(testErn, testArc, continueDraft = false)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.p(1) -> messagesForLanguage.p1,
          Selectors.inset(1) -> messagesForLanguage.inset,
          Selectors.link(1) -> messagesForLanguage.continueButton,
          Selectors.link(2) -> messagesForLanguage.startAgainLink
        ))
      }
    }
  }
}
