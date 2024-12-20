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
import fixtures.messages.ItemOtherInformationMessages
import forms.OtherInformationFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.unsatisfactory.individualItems.ItemOtherInformationPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ItemOtherInformationView

class ItemOtherInformationViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "Rendering for the ItemOtherInformationPage" - {

    Seq(ItemOtherInformationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[OtherInformationFormProvider].apply(Some(ItemOtherInformationPage(1)))
        val view = app.injector.instanceOf[ItemOtherInformationView]

        implicit val doc: Document = Jsoup.parse(view(ItemOtherInformationPage(1), form, testOnwardRoute, item1, cnCodeInfo).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(1),
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading(1),
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> messagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }
}
