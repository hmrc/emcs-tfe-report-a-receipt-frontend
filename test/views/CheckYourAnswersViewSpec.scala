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
import controllers.routes
import fixtures.messages.CheckYourAnswersMessages
import models.UnitOfMeasure.Kilograms
import models.{ItemShortageOrExcessModel, ReviewMode, WrongWithMovement}
import models.WrongWithMovement.{Shortage, ShortageOrExcess}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, SelectItemsPage, WrongWithItemPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import viewmodels.checkAnswers.CheckAnswersItemHelper
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val addAnotherItemButton = secondaryButton
    val submitButton = button + s":not($addAnotherItemButton)"
    val itemCardTitle = (item: Int) => s".govuk-summary-card__title-wrapper:nth-of-type($item) h3"
    val itemDetailsLink = (item: Int) => s".govuk-summary-card__title-wrapper:nth-of-type($item) li:nth-of-type(1) a"
    val itemRemoveLink = (item: Int) => s".govuk-summary-card__title-wrapper:nth-of-type($item) li:nth-of-type(2) a"
  }

  "CheckYourAnswers view" - {

    Seq(CheckYourAnswersMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        val link = routes.SelectItemsController.onPageLoad(testErn, testArc).url

        val userAnswers = emptyUserAnswers
          .set(SelectItemsPage(item1.itemUniqueReference), item1.itemUniqueReference)
          .set[Set[WrongWithMovement]](WrongWithItemPage(item1.itemUniqueReference), Set(ShortageOrExcess))
          .set(ItemShortageOrExcessPage(item1.itemUniqueReference), ItemShortageOrExcessModel(Shortage, 10, None))

        implicit val config: AppConfig = app.injector.instanceOf[AppConfig]
        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

        val view = app.injector.instanceOf[CheckYourAnswersView]
        val helper = app.injector.instanceOf[CheckAnswersItemHelper]

        val item1Summary = helper.summaryList(item1.itemUniqueReference, Kilograms, true)

        implicit val doc: Document = Jsoup.parse(view(
          submitAction = controllers.routes.CheckYourAnswersController.onSubmit(testErn, testArc),
          addItemLink = link,
          list = SummaryList(Seq()),
          itemList = Seq(item1.itemUniqueReference -> item1Summary),
          itemsToAdd = true
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(2) -> messagesForLanguage.movementDetailsH2,
          Selectors.h2(3) -> messagesForLanguage.itemsH2,
          Selectors.itemCardTitle(1) -> messagesForLanguage.item(item1.itemUniqueReference),
          Selectors.itemDetailsLink(1) -> messagesForLanguage.itemDetails(item1.itemUniqueReference),
          Selectors.itemRemoveLink(1) -> messagesForLanguage.itemRemove(item1.itemUniqueReference),
          Selectors.addAnotherItemButton -> messagesForLanguage.addAnotherItem,
          Selectors.h2(4) -> messagesForLanguage.submitH2,
          Selectors.p(2) -> messagesForLanguage.declaration,
          Selectors.bullet(1) -> messagesForLanguage.bullet1,
          Selectors.bullet(2) -> messagesForLanguage.bullet2,
          Selectors.submitButton -> messagesForLanguage.submitButton
        ))

        "have a link to view the Item details" in {

          doc.select(Selectors.itemDetailsLink(1)).attr("href") mustBe
            routes.ItemDetailsController.onPageLoad(testErn, testArc, item1.itemUniqueReference).url
        }

        "have a link to remove the Item" in {

          doc.select(Selectors.itemRemoveLink(1)).attr("href") mustBe
            routes.RemoveItemController.onPageLoad(testErn, testArc, item1.itemUniqueReference, ReviewMode).url
        }
      }
    }
  }
}
