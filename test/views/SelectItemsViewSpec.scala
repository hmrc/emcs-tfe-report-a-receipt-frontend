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
import fixtures.messages.SelectItemsMessages
import models.ReferenceDataUnitOfMeasure.`1`
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.SelectItemsView

class SelectItemsViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "SelectItemsView" - {

    Seq(SelectItemsMessages.English, SelectItemsMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[SelectItemsView]

        implicit val doc: Document = Jsoup.parse(view(
          Seq(item1, item2)
            .zipWithIndex
            .map { case (l, i) => (l, CnCodeInformation(s"testdata${i + 1}", "", `1`)) }
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.tableHeader(1) -> messagesForLanguage.tableHeadDescription,
          Selectors.tableHeader(2) -> messagesForLanguage.tableHeadQuantity,
          Selectors.tableHeader(3) -> messagesForLanguage.tableHeadAlcohol,
          Selectors.tableHeader(4) -> messagesForLanguage.tableHeadPackaging,
          Selectors.tableRow(1, 1) -> "testdata1",
          Selectors.tableRow(1, 2) -> (item1.quantity.toString() + " kg"),
          Selectors.tableRow(1, 3) -> messagesForLanguage.alcoholRow(item1.alcoholicStrength),
          Selectors.tableRow(2, 1) -> "testdata2",
          Selectors.tableRow(2, 2) -> (item2.quantity.toString() + " kg"),
          Selectors.tableRow(2, 3) -> messagesForLanguage.alcoholRow(item2.alcoholicStrength)
        ))
      }
    }
  }
}
