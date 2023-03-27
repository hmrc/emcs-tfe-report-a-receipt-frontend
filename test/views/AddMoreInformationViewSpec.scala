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
import org.jsoup.Jsoup
import pages.AddMoreInformationPage
import pages.unsatisfactory.individualItems.RemoveItemPage
import pages.unsatisfactory.{AddDamageInformationPage, AddExcessInformationPage, AddSealsInformationPage, AddShortageInformationPage}
import play.api.test.FakeRequest
import views.html.AddMoreInformationView

class AddMoreInformationViewSpec extends ViewSpecBase with ViewBehaviours {

  lazy val view = app.injector.instanceOf[AddMoreInformationView]

  object Selectors extends BaseSelectors

  "For the AddMoreInformationPage view" - {

    lazy val form = app.injector.instanceOf[AddMoreInformationFormProvider].apply(AddMoreInformationPage)

    Seq(AddMoreInformationMessages.English, AddMoreInformationMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
        implicit val doc = Jsoup.parse(view(form, AddMoreInformationPage, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }

  "For the AddShortageInformationPage view" - {

    lazy val form = app.injector.instanceOf[AddMoreInformationFormProvider].apply(AddShortageInformationPage)

    Seq(AddShortageInformationMessages.English, AddShortageInformationMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
        implicit val doc = Jsoup.parse(view(form, AddShortageInformationPage, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }

  "For the AddExcessInformationPage view" - {

    lazy val form = app.injector.instanceOf[AddMoreInformationFormProvider].apply(AddExcessInformationPage)

    Seq(AddExcessInformationMessages.English, AddExcessInformationMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
        implicit val doc = Jsoup.parse(view(form, AddExcessInformationPage, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }

  "For the AddDamageInformationPage view" - {

    lazy val form = app.injector.instanceOf[AddMoreInformationFormProvider].apply(AddDamageInformationPage)

    Seq(AddDamageInformationMessages.English, AddDamageInformationMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
        implicit val doc = Jsoup.parse(view(form, AddDamageInformationPage, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }

  "For the AddSealsInformationPage view" - {

    lazy val form = app.injector.instanceOf[AddMoreInformationFormProvider].apply(AddSealsInformationPage)

    Seq(AddSealsInformationMessages.English, AddSealsInformationMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
        implicit val doc = Jsoup.parse(view(form, AddSealsInformationPage, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }

  "For the RemoveItemPage view" - {

    lazy val form = app.injector.instanceOf[AddMoreInformationFormProvider].apply(RemoveItemPage(1))

    Seq(RemoveItemMessages.English, RemoveItemMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
        implicit val doc = Jsoup.parse(view(form, RemoveItemPage(1), testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.secondaryButton -> messagesForLanguage.saveAndReturnToMovement
        ))
      }
    }
  }
}
