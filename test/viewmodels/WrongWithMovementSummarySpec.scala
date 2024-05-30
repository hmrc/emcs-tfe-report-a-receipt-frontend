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

package viewmodels

import base.SpecBase
import controllers.routes
import fixtures.messages.WrongWithMovementMessages
import models.WrongWithMovement.{BrokenSeals, Damaged, Shortage, Excess, Other}
import models.{NormalMode, WrongWithMovement}
import pages.unsatisfactory.WrongWithMovementPage
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import utils.DateUtils
import viewmodels.checkAnswers.WrongWithMovementSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.list

class WrongWithMovementSummarySpec extends SpecBase with DateUtils {


  "WrongWithMovementSummary" - {

    Seq(WrongWithMovementMessages.English).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val list = app.injector.instanceOf[list]
        lazy val wrongWithMovementSummary = new WrongWithMovementSummary(list)

        "when an answer is set" - {

          "must render the expected SummaryListRow" in {

            val answers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Shortage, Damaged, Other, BrokenSeals, Excess))
            implicit val request = dataRequest(FakeRequest(), answers)

            wrongWithMovementSummary.row() mustBe
              Some(SummaryListRowViewModel(
                key = langMessages.checkYourAnswersLabel,
                value = ValueViewModel(
                  HtmlContent(list(Seq(
                    Html(langMessages.checkYourAnswersLess),
                    Html(langMessages.checkYourAnswersMore),
                    Html(langMessages.checkYourAnswersDamaged),
                    Html(langMessages.checkYourAnswersBrokenSeals),
                    Html(langMessages.checkYourAnswersOther)
                  )))
                ),
                actions = Seq(
                  ActionItemViewModel(
                    langMessages.change,
                    routes.WrongWithMovementController.loadWrongWithMovement(request.userAnswers.ern, request.userAnswers.arc, NormalMode).url,
                    id = WrongWithMovementPage
                  ).withVisuallyHiddenText(langMessages.hiddenChangeLinkText)
                )
              ))
          }
        }

        "when no answer is set" - {

          "must render None" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
            wrongWithMovementSummary.row() mustBe None
          }
        }
      }
    }
  }
}
