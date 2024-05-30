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
import fixtures.messages.{AcceptMovementMessages, BaseMessages}
import models.AcceptMovement.{PartiallyRefused, Refused, Satisfactory, Unsatisfactory}
import models.NormalMode
import pages.AcceptMovementPage
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.AcceptMovementSummary
import viewmodels.govuk.summarylist._

class AcceptMovementSummarySpec extends SpecBase {

  "AcceptMovementSummary" - {

    Seq(AcceptMovementMessages.English).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val acceptMovementSummary = new AcceptMovementSummary

        "when an answer is satisfactory" - {

          "must render the expected SummaryListRow" in {

            val answers = emptyUserAnswers.set(AcceptMovementPage, Satisfactory)
            implicit val request = dataRequest(FakeRequest(), answers)

            acceptMovementSummary.row() mustBe expectedSummaryList(langMessages.cyaSatisfactory, langMessages)
          }
        }

        "when an answer is unsatisfactory" - {

          "must render the expected SummaryListRow" in {

            val answers = emptyUserAnswers.set(AcceptMovementPage, Unsatisfactory)
            implicit val request = dataRequest(FakeRequest(), answers)

            acceptMovementSummary.row() mustBe expectedSummaryList(langMessages.cyaUnsatisfactory, langMessages)
          }
        }

        "when an answer is refused" - {

          "must render the expected SummaryListRow" in {

            val answers = emptyUserAnswers.set(AcceptMovementPage, Refused)
            implicit val request = dataRequest(FakeRequest(), answers)

            acceptMovementSummary.row() mustBe expectedSummaryList(langMessages.cyaRefused, langMessages)
          }
        }

        "when an answer is partially refused" - {

          "must render the expected SummaryListRow" in {

            val answers = emptyUserAnswers.set(AcceptMovementPage, PartiallyRefused)
            implicit val request = dataRequest(FakeRequest(), answers)

            acceptMovementSummary.row() mustBe expectedSummaryList(langMessages.cyaPartiallyRefused, langMessages)
          }
        }

        "when no answer is set" - {

          "must render None" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
            acceptMovementSummary.row() mustBe None
          }
        }
      }
    }
  }

  def expectedSummaryList(valueMessage: String, langMessages: AcceptMovementMessages.ViewMessages with BaseMessages): Option[SummaryListRow] = {
    Some(SummaryListRowViewModel(
      key = Key(Text(langMessages.checkYourAnswersLabel)),
      value = ValueViewModel(Text(valueMessage)),
      actions = Seq(
        ActionItemViewModel(
          Text(langMessages.change),
          routes.AcceptMovementController.onPageLoad(testErn, testArc, NormalMode).url,
          id = AcceptMovementPage
        ).withVisuallyHiddenText(langMessages.hiddenChangeLinkText)
      )
    ))
  }
}
