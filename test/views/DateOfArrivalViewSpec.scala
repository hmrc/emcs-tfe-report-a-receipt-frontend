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
import fixtures.messages.DateOfArrivalMessages
import forms.DateOfArrivalFormProvider
import models.NormalMode
import org.jsoup.Jsoup
import play.api.test.FakeRequest
import views.html.DateOfArrivalView

import java.time.LocalDate

class DateOfArrivalViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "DateOfArrival view" - {

    Seq(DateOfArrivalMessages.English, DateOfArrivalMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        val dateOfDispatch = LocalDate.now()

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[DateOfArrivalFormProvider].apply(dateOfDispatch)
        val view = app.injector.instanceOf[DateOfArrivalView]

        implicit val doc = Jsoup.parse(view(form, NormalMode).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.dateDay -> messagesForLanguage.day,
          Selectors.dateMonth -> messagesForLanguage.month,
          Selectors.dateYear -> messagesForLanguage.year,
          Selectors.button -> messagesForLanguage.saveAndContinue
        ))
      }
    }

    "must not render a secondary button" in {
      val dateOfDispatch = LocalDate.now()

      implicit val msgs = messages(app, DateOfArrivalMessages.English.lang)
      implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)

      val form = app.injector.instanceOf[DateOfArrivalFormProvider].apply(dateOfDispatch)
      val view = app.injector.instanceOf[DateOfArrivalView]

      implicit val doc = Jsoup.parse(view(form, NormalMode).toString())

      doc.select(Selectors.secondaryButton).size() mustBe 0
    }
  }
}
