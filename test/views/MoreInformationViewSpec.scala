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
import fixtures.messages.{MoreInformationMessages, ShortageInformationMessages}
import forms.MoreInformationFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.MoreInformationPage
import pages.unsatisfactory.ShortageInformationPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.MoreInformationView

class MoreInformationViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "MoreInformation variant of view" - {

    Seq(MoreInformationMessages.English, MoreInformationMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[MoreInformationFormProvider].apply(MoreInformationPage)
        val view = app.injector.instanceOf[MoreInformationView]

        implicit val doc: Document = Jsoup.parse(view(form, MoreInformationPage, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }

  "ShortageInformation variant of view" - {

    Seq(ShortageInformationMessages.English, ShortageInformationMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[MoreInformationFormProvider].apply(ShortageInformationPage)
        val view = app.injector.instanceOf[MoreInformationView]

        implicit val doc: Document = Jsoup.parse(view(form, ShortageInformationPage, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }
}
