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
import fixtures.messages.{ItemShortageOrExcessMessages, UnitOfMeasureMessages}
import forms.ItemShortageOrExcessFormProvider
import models.UnitOfMeasure.{Kilograms, Litres15, Litres20, Thousands}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ItemShortageOrExcessView

class ItemShortageOrExcessViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val shortageExcessLegend = "main legend"
    val amountLabel = "main label[for='amount']"
    val additionalInfoLabel = "main label[for='additionalInfo']"
    val shortageLabel = "main label[for='shortage']"
    val excessLabel = "main label[for='excess']"
  }

  "ItemShortageOrExcessView rendered in the Kilograms variant of view" - {

    Seq(
      ItemShortageOrExcessMessages.English -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[ItemShortageOrExcessFormProvider].apply()
        val view = app.injector.instanceOf[ItemShortageOrExcessView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Kilograms).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title,
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading,
          Selectors.shortageExcessLegend -> viewMessagesForLanguage.shortageOrExcessLegend,
          Selectors.shortageLabel -> viewMessagesForLanguage.shortageLabel,
          Selectors.excessLabel -> viewMessagesForLanguage.excessLabel,
          Selectors.amountLabel -> viewMessagesForLanguage.amountLabel(unitOfMeasureMessagesForLanguage.kilogramsLong),
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.kilogramsShort,
          Selectors.additionalInfoLabel -> viewMessagesForLanguage.additionalInfoLabel,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> viewMessagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }

  "ItemShortageOrExcessView rendered in the Litres 15 degrees variant of view" - {

    Seq(
      ItemShortageOrExcessMessages.English -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[ItemShortageOrExcessFormProvider].apply()
        val view = app.injector.instanceOf[ItemShortageOrExcessView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Litres15).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title,
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading,
          Selectors.shortageExcessLegend -> viewMessagesForLanguage.shortageOrExcessLegend,
          Selectors.shortageLabel -> viewMessagesForLanguage.shortageLabel,
          Selectors.excessLabel -> viewMessagesForLanguage.excessLabel,
          Selectors.amountLabel -> viewMessagesForLanguage.amountLabel(unitOfMeasureMessagesForLanguage.litres15Long),
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.litres15Short,
          Selectors.additionalInfoLabel -> viewMessagesForLanguage.additionalInfoLabel,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> viewMessagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }

  "ItemShortageOrExcessView rendered in the Litres 20 degrees variant of view" - {

    Seq(
      ItemShortageOrExcessMessages.English -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[ItemShortageOrExcessFormProvider].apply()
        val view = app.injector.instanceOf[ItemShortageOrExcessView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Litres20).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title,
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading,
          Selectors.shortageExcessLegend -> viewMessagesForLanguage.shortageOrExcessLegend,
          Selectors.shortageLabel -> viewMessagesForLanguage.shortageLabel,
          Selectors.excessLabel -> viewMessagesForLanguage.excessLabel,
          Selectors.amountLabel -> viewMessagesForLanguage.amountLabel(unitOfMeasureMessagesForLanguage.litres20Long),
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.litres20Short,
          Selectors.additionalInfoLabel -> viewMessagesForLanguage.additionalInfoLabel,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> viewMessagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }

  "ItemShortageOrExcessView rendered in the thousands variant of view" - {

    Seq(
      ItemShortageOrExcessMessages.English -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[ItemShortageOrExcessFormProvider].apply()
        val view = app.injector.instanceOf[ItemShortageOrExcessView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Thousands).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title,
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading,
          Selectors.shortageExcessLegend -> viewMessagesForLanguage.shortageOrExcessLegend,
          Selectors.shortageLabel -> viewMessagesForLanguage.shortageLabel,
          Selectors.excessLabel -> viewMessagesForLanguage.excessLabel,
          Selectors.amountLabel -> viewMessagesForLanguage.amountLabel(unitOfMeasureMessagesForLanguage.thousandsLong),
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.thousandsShort,
          Selectors.additionalInfoLabel -> viewMessagesForLanguage.additionalInfoLabel,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> viewMessagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }
}
