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
import fixtures.messages.ConfirmationMessages
import models.AcceptMovement.{Refused, Satisfactory, Unsatisfactory}
import models.WrongWithMovement.{Excess, Shortage}
import models.requests.DataRequest
import models.{ConfirmationDetails, ItemShortageOrExcessModel, WrongWithMovement}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.AcceptMovementPage
import pages.unsatisfactory.WrongWithMovementPage
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, SelectItemsPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ConfirmationView

class ConfirmationViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "ConfirmationView" - {

    Seq(ConfirmationMessages.English, ConfirmationMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when movement was Satisfactory" - {

          val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Satisfactory)

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[ConfirmationView]

          val testConfirmationDetails = ConfirmationDetails(Satisfactory.toString, testReceiptDate)

          implicit val doc: Document = Jsoup.parse(view(testConfirmationDetails).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.whatNextH2,
            Selectors.p(1) -> messagesForLanguage.whatNextP1,
            Selectors.p(2) -> messagesForLanguage.whatNextP2,
            Selectors.p(3) -> messagesForLanguage.contactHmrc,
            Selectors.button -> messagesForLanguage.returnToAtAGlance,
            Selectors.p(4) -> messagesForLanguage.feedback
          ))
        }

        "when movement was Unsatisfactory and there's a shortage and excess" - {

          val userAnswers = emptyUserAnswers
            .set(AcceptMovementPage, Unsatisfactory)
            .set(WrongWithMovementPage, Set[WrongWithMovement](Shortage, Excess))

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[ConfirmationView]

          val testConfirmationDetails = ConfirmationDetails(Unsatisfactory.toString, testReceiptDate, hasMovementShortage = true, hasMovementExcess = true)

          implicit val doc: Document = Jsoup.parse(view(testConfirmationDetails).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.whatNextH2,
            Selectors.p(1) -> messagesForLanguage.whatNextP1,
            Selectors.p(2) -> messagesForLanguage.whatNextP2,
            Selectors.h3(1) -> messagesForLanguage.shortageH3,
            Selectors.p(3) -> messagesForLanguage.shortageP1,
            Selectors.h3(2) -> messagesForLanguage.excessH3,
            Selectors.p(4) -> messagesForLanguage.excessP1,
            Selectors.bullet(1) -> messagesForLanguage.excessSameGoodsBullet1,
            Selectors.bullet(2) -> messagesForLanguage.excessSameGoodsBullet2,
            Selectors.p(5) -> messagesForLanguage.excessP2,
            Selectors.bullet(1, 2) -> messagesForLanguage.excessDifferentGoodsBullet1,
            Selectors.bullet(2, 2) -> messagesForLanguage.excessDifferentGoodsBullet2,
            Selectors.p(6) -> messagesForLanguage.contactHmrc,
            Selectors.button -> messagesForLanguage.returnToAtAGlance,
            Selectors.p(7) -> messagesForLanguage.feedback
          ))
        }

        "when movement was Refused and there's an item shortage and excess" - {

          val userAnswers = emptyUserAnswers
            .set(AcceptMovementPage, Refused)
            .set(SelectItemsPage(1), item1.itemUniqueReference)
            .set(SelectItemsPage(2), item2.itemUniqueReference)
            .set(ItemShortageOrExcessPage(1), ItemShortageOrExcessModel(Shortage, 10, None))
            .set(ItemShortageOrExcessPage(2), ItemShortageOrExcessModel(Excess, 10, None))

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[ConfirmationView]

          val testConfirmationDetails = ConfirmationDetails(Refused.toString, testReceiptDate, hasItemExcess = true, hasItemShortage = true)

          implicit val doc: Document = Jsoup.parse(view(testConfirmationDetails).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.whatNextH2,
            Selectors.p(1) -> messagesForLanguage.whatNextP1,
            Selectors.p(2) -> messagesForLanguage.whatNextP2,
            Selectors.h3(1) -> messagesForLanguage.refusedH3,
            Selectors.p(3) -> messagesForLanguage.refusedP1,
            Selectors.bullet(1) -> messagesForLanguage.refusedBullet1,
            Selectors.bullet(2) -> messagesForLanguage.refusedBullet2,
            Selectors.bullet(3) -> messagesForLanguage.refusedBullet3,
            Selectors.h3(2) -> messagesForLanguage.shortageH3,
            Selectors.p(4) -> messagesForLanguage.shortageP1,
            Selectors.h3(3) -> messagesForLanguage.excessH3,
            Selectors.p(5) -> messagesForLanguage.excessP1,
            Selectors.bullet(1, 2) -> messagesForLanguage.excessSameGoodsBullet1,
            Selectors.bullet(2, 2) -> messagesForLanguage.excessSameGoodsBullet2,
            Selectors.p(6) -> messagesForLanguage.excessP2,
            Selectors.bullet(1, 3) -> messagesForLanguage.excessDifferentGoodsBullet1,
            Selectors.bullet(2, 3) -> messagesForLanguage.excessDifferentGoodsBullet2,
            Selectors.p(7) -> messagesForLanguage.contactHmrc,
            Selectors.button -> messagesForLanguage.returnToAtAGlance,
            Selectors.p(8) -> messagesForLanguage.feedback
          ))
        }
      }
    }
  }
}
