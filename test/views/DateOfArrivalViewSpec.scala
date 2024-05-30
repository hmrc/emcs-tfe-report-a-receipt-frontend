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
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Lang, Messages}
import play.api.test.FakeRequest
import views.html.DateOfArrivalView

import java.time.LocalDate

class DateOfArrivalViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  class Fixture(language: Lang) {

    val dateOfDispatch = LocalDate.now()

    implicit val msgs: Messages = messages(app, language)
    implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

    val form = app.injector.instanceOf[DateOfArrivalFormProvider].apply(dateOfDispatch)
    val view = app.injector.instanceOf[DateOfArrivalView]
  }

  "DateOfArrival view" - {

    Seq(DateOfArrivalMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - new Fixture(messagesForLanguage.lang) {

        implicit val doc: Document = Jsoup.parse(view(form, NormalMode).toString())

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

      s"must not render a secondary link in lang code of '${messagesForLanguage.lang.code}'" in new Fixture(messagesForLanguage.lang) {

        val doc = Jsoup.parse(view(form, NormalMode).toString())

        doc.select(Selectors.id("save-and-exit")).size() mustBe 0
      }

      s"when rendered with an error, must highlight only error fields and link to correct one with lang code of '${messagesForLanguage.lang.code}'" - {

        "when day and year are missing, highlight both and link error message to day" in new Fixture(messagesForLanguage.lang) {

          val formWithErrors = form.bind(Map(
            "value.month" -> "1"
          ))

          val doc = Jsoup.parse(view(formWithErrors, NormalMode).toString())

          doc.select("#value\\.day").hasClass("govuk-input--error") mustBe true
          doc.select("#value\\.month").hasClass("govuk-input--error") mustBe false
          doc.select("#value\\.year").hasClass("govuk-input--error") mustBe true

          doc.select(".govuk-error-summary__list a").attr("href") mustBe "#value.day"
        }

        "when month and year are missing, highlight both and link error message to month" in new Fixture(messagesForLanguage.lang) {

          val formWithErrors = form.bind(Map(
            "value.day" -> "1"
          ))

          val doc = Jsoup.parse(view(formWithErrors, NormalMode).toString())

          doc.select("#value\\.day").hasClass("govuk-input--error") mustBe false
          doc.select("#value\\.month").hasClass("govuk-input--error") mustBe true
          doc.select("#value\\.year").hasClass("govuk-input--error") mustBe true

          doc.select(".govuk-error-summary__list a").attr("href") mustBe "#value.month"
        }

        "when day and month are missing, highlight both and link error message to day" in new Fixture(messagesForLanguage.lang) {

          val formWithErrors = form.bind(Map(
            "value.year" -> "2023"
          ))

          val doc = Jsoup.parse(view(formWithErrors, NormalMode).toString())

          doc.select("#value\\.day").hasClass("govuk-input--error") mustBe true
          doc.select("#value\\.month").hasClass("govuk-input--error") mustBe true
          doc.select("#value\\.year").hasClass("govuk-input--error") mustBe false

          doc.select(".govuk-error-summary__list a").attr("href") mustBe "#value.day"
        }

        "when whole date is wrong, highlight all fields and link error message to day" in new Fixture(messagesForLanguage.lang) {

          val formWithErrors = form.bind(Map(
            "value.day" -> "30",
            "value.month" -> "2",
            "value.year" -> "2023"
          ))

          val doc = Jsoup.parse(view(formWithErrors, NormalMode).toString())

          doc.select("#value\\.day").hasClass("govuk-input--error") mustBe true
          doc.select("#value\\.month").hasClass("govuk-input--error") mustBe true
          doc.select("#value\\.year").hasClass("govuk-input--error") mustBe true

          doc.select(".govuk-error-summary__list a").attr("href") mustBe "#value.day"
        }
      }
    }
  }
}
