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
import fixtures.messages._
import forms.AddMoreInformationFormProvider
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.CnCodeInformation
import org.jsoup.Jsoup
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, RemoveItemPage, AddItemSealsInformationPage}
import play.api.test.FakeRequest
import views.html.AddItemMoreInformationView

class AddItemMoreInformationViewSpec extends ViewSpecBase with ViewBehaviours {

  lazy val view = app.injector.instanceOf[AddItemMoreInformationView]

  object Selectors extends BaseSelectors

  lazy val form = app.injector.instanceOf[AddMoreInformationFormProvider].apply(RemoveItemPage(1))

  Seq(
    AddItemDamageInformationPage(1) -> Seq(AddItemDamageInformationMessages.English, AddItemDamageInformationMessages.Welsh),
    AddItemSealsInformationPage(1) -> Seq(AddItemSealsInformationMessages.English, AddItemSealsInformationMessages.Welsh)
  ) foreach {

    case (page, langs) => langs foreach { messagesForLanguage =>

      s"For the $page view" - {

        s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

          implicit val msgs = messages(app, messagesForLanguage.lang)
          implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
          implicit val doc = Jsoup.parse(view(form, page, testOnwardRoute, item1, CnCodeInformation("", "", `1`)).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(1),
            Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
            Selectors.h1 -> messagesForLanguage.heading(1),
            Selectors.detailsSummary(1) -> messagesForLanguage.itemDetails(1),
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.id("save-and-exit") -> messagesForLanguage.savePreviousAnswersAndExit
          ))
        }
      }
    }
  }
}
