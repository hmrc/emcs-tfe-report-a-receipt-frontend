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
import fixtures.messages.{RefusedAmountMessages, UnitOfMeasureMessages}
import forms.RefusedAmountFormProvider
import models.ReferenceDataUnitOfMeasure.`1`
import models.UnitOfMeasure.{Kilograms, Litres15, Litres20, Thousands}
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.RefusedAmountView

class RefusedAmountViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "RefusedAmountView rendered in the Kilograms variant of view" - {

    Seq(
      RefusedAmountMessages.English -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[RefusedAmountFormProvider].apply(itemQuantity = 20, None)
        val view = app.injector.instanceOf[RefusedAmountView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Kilograms, item1, CnCodeInformation("", "", `1`), 1).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title,
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading,
          Selectors.label("value") -> viewMessagesForLanguage.label(unitOfMeasureMessagesForLanguage.kilogramsLong),
          Selectors.hint -> viewMessagesForLanguage.hint,
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.kilogramsShort,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> viewMessagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }

  "RefusedAmountView rendered in the Litres at 15 degrees variant of view" - {

    Seq(
      RefusedAmountMessages.English -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[RefusedAmountFormProvider].apply(itemQuantity = 20, None)
        val view = app.injector.instanceOf[RefusedAmountView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Litres15, item1, CnCodeInformation("", "", `1`), 1).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title,
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading,
          Selectors.label("value") -> viewMessagesForLanguage.label(unitOfMeasureMessagesForLanguage.litres15Long),
          Selectors.hint -> viewMessagesForLanguage.hint,
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.litres15Short,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> viewMessagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }

  "RefusedAmountView rendered in the Litres at 20 degrees variant of view" - {

    Seq(
      RefusedAmountMessages.English -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[RefusedAmountFormProvider].apply(itemQuantity = 20, None)
        val view = app.injector.instanceOf[RefusedAmountView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Litres20, item1, CnCodeInformation("", "", `1`), 1).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title,
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading,
          Selectors.label("value") -> viewMessagesForLanguage.label(unitOfMeasureMessagesForLanguage.litres20Long),
          Selectors.hint -> viewMessagesForLanguage.hint,
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.litres20Short,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> viewMessagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }

  "RefusedAmountView rendered in the Thousands variant of view" - {

    Seq(
      RefusedAmountMessages.English -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[RefusedAmountFormProvider].apply(itemQuantity = 20, None)
        val view = app.injector.instanceOf[RefusedAmountView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Thousands, item1, CnCodeInformation("", "", `1`), 1).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title,
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading,
          Selectors.label("value") -> viewMessagesForLanguage.label(unitOfMeasureMessagesForLanguage.thousandsLong),
          Selectors.hint -> viewMessagesForLanguage.hint,
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.thousandsShort,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> viewMessagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }
}
