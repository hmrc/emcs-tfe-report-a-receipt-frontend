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
import models.UnitOfMeasure.{Kilograms, Litres15, Litres20, Thousands}
import models.requests.DataRequest
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
      RefusedAmountMessages.English -> UnitOfMeasureMessages.English,
      RefusedAmountMessages.Welsh -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[RefusedAmountFormProvider].apply(itemQuantity = 20)
        val view = app.injector.instanceOf[RefusedAmountView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Kilograms).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title(unitOfMeasureMessagesForLanguage.kilogramsLong),
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading(unitOfMeasureMessagesForLanguage.kilogramsLong),
          Selectors.label("value") -> viewMessagesForLanguage.heading(unitOfMeasureMessagesForLanguage.kilogramsLong),
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.kilogramsShort,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> viewMessagesForLanguage.saveAndReturnToMovement
        ))

        "input label should be visually hidden as same as heading" in {
          doc.select(Selectors.label("value")).hasClass("govuk-visually-hidden") mustBe true
        }
      }
    }
  }

  "RefusedAmountView rendered in the Litres at 15 degrees variant of view" - {

    Seq(
      RefusedAmountMessages.English -> UnitOfMeasureMessages.English,
      RefusedAmountMessages.Welsh -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[RefusedAmountFormProvider].apply(itemQuantity = 20)
        val view = app.injector.instanceOf[RefusedAmountView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Litres15).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title(unitOfMeasureMessagesForLanguage.litres15Long),
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading(unitOfMeasureMessagesForLanguage.litres15Long),
          Selectors.label("value") -> viewMessagesForLanguage.heading(unitOfMeasureMessagesForLanguage.litres15Long),
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.litres15Short,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> viewMessagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }

  "RefusedAmountView rendered in the Litres at 20 degrees variant of view" - {

    Seq(
      RefusedAmountMessages.English -> UnitOfMeasureMessages.English,
      RefusedAmountMessages.Welsh -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[RefusedAmountFormProvider].apply(itemQuantity = 20)
        val view = app.injector.instanceOf[RefusedAmountView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Litres20).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title(unitOfMeasureMessagesForLanguage.litres20Long),
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading(unitOfMeasureMessagesForLanguage.litres20Long),
          Selectors.label("value") -> viewMessagesForLanguage.heading(unitOfMeasureMessagesForLanguage.litres20Long),
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.litres20Short,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> viewMessagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }

  "RefusedAmountView rendered in the Thousands variant of view" - {

    Seq(
      RefusedAmountMessages.English -> UnitOfMeasureMessages.English,
      RefusedAmountMessages.Welsh -> UnitOfMeasureMessages.English
    ).foreach { case (viewMessagesForLanguage, unitOfMeasureMessagesForLanguage) =>

      s"when being rendered in lang code of '${viewMessagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, viewMessagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[RefusedAmountFormProvider].apply(itemQuantity = 20)
        val view = app.injector.instanceOf[RefusedAmountView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Thousands).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessagesForLanguage.title(unitOfMeasureMessagesForLanguage.thousandsLong),
          Selectors.h2(1) -> viewMessagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> viewMessagesForLanguage.heading(unitOfMeasureMessagesForLanguage.thousandsLong),
          Selectors.label("value") -> viewMessagesForLanguage.heading(unitOfMeasureMessagesForLanguage.thousandsLong),
          Selectors.inputSuffix -> unitOfMeasureMessagesForLanguage.thousandsShort,
          Selectors.button -> viewMessagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> viewMessagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }
}
