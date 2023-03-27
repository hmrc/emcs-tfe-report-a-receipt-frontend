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
import fixtures.messages.AddedItemsMessages
import forms.AddAnotherItemFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.unsatisfactory.individualItems.{CheckAnswersItemPage, SelectItemsPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.AddedItemsSummary
import views.html.AddedItemsView

class AddedItemsViewSpec extends ViewSpecBase with ViewBehaviours {

  lazy val form = new AddAnotherItemFormProvider()()
  lazy val listHelper = new AddedItemsSummary()

  object Selectors extends BaseSelectors {
    val itemCnCode = (n: Int) => s"main dl div:nth-of-type($n) > :nth-child(1)"
    val itemChangeLink = (n: Int) => s"main dl div:nth-of-type($n) > :nth-child(2) > ul > li:nth-of-type(1)"
    val itemRemoveLink = (n: Int) => s"main dl div:nth-of-type($n) > :nth-child(2) > ul > li:nth-of-type(2)"
  }

  "AddedItemsView" - {

    Seq(AddedItemsMessages.English, AddedItemsMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when being rendered with the YesNo form and single item" - {

          val userAnswers = emptyUserAnswers
            .set(SelectItemsPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[AddedItemsView]

          implicit val doc: Document = Jsoup.parse(view(Some(form), listHelper.itemList(), testOnwardRoute).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleSingular,
            Selectors.h1 -> messagesForLanguage.headingSingular,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.itemCnCode(1) -> item1.cnCode,
            Selectors.itemChangeLink(1) -> messagesForLanguage.change,
            Selectors.itemRemoveLink(1) -> messagesForLanguage.remove,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no,
            Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
          ))
        }

        "when being rendered without the YesNo form and multiple items" - {

          val userAnswers = emptyUserAnswers
            .set(SelectItemsPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemsPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[AddedItemsView]

          implicit val doc: Document = Jsoup.parse(view(None, listHelper.itemList(), testOnwardRoute).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titlePlural(2),
            Selectors.h1 -> messagesForLanguage.headingPlural(2),
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.itemCnCode(1) -> item1.cnCode,
            Selectors.itemChangeLink(1) -> messagesForLanguage.change,
            Selectors.itemRemoveLink(1) -> messagesForLanguage.remove,
            Selectors.itemCnCode(2) -> item1.cnCode,
            Selectors.itemChangeLink(2) -> messagesForLanguage.change,
            Selectors.itemRemoveLink(2) -> messagesForLanguage.remove,
            Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
          ))
        }
      }
    }
  }
}
