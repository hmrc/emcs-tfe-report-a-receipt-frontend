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
import fixtures.messages.{BaseMessages, DestinationOfficeMessages}
import models.CheckMode
import models.DestinationOffice.{GreatBritain, NorthernIreland}
import pages.DestinationOfficePage
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.DestinationOfficeSummary
import viewmodels.govuk.summarylist._

class DestinationOfficeSummarySpec extends SpecBase {

  "DestinationOfficeSummary" - {

    Seq(DestinationOfficeMessages.English, DestinationOfficeMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val destinationOfficeSummary = new DestinationOfficeSummary

        "when an answer is Great Britain" - {

          "must render the expected SummaryListRow" in {

            val answers = emptyUserAnswers.set(DestinationOfficePage, GreatBritain)
            implicit val request = dataRequest(FakeRequest(), answers)

            destinationOfficeSummary.row() mustBe expectedSummaryList(langMessages.cyaGb, langMessages)
          }
        }

        "when an answer is Northern Ireland" - {

          "must render the expected SummaryListRow" in {

            val answers = emptyUserAnswers.set(DestinationOfficePage, NorthernIreland)
            implicit val request = dataRequest(FakeRequest(), answers)

            destinationOfficeSummary.row() mustBe expectedSummaryList(langMessages.cyaNi, langMessages)
          }
        }

        "when no answer is set" - {

          "must render None" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
            destinationOfficeSummary.row() mustBe None
          }
        }
      }
    }
  }

  def expectedSummaryList(valueMessage: String, langMessages: DestinationOfficeMessages.ViewMessages with BaseMessages): Option[SummaryListRow] = {
    Some(SummaryListRowViewModel(
      key = Key(Text(langMessages.checkYourAnswersLabel)),
      value = ValueViewModel(Text(valueMessage)),
      actions = Seq(
        ActionItemViewModel(
          Text(langMessages.change),
          routes.DestinationOfficeController.onPageLoad(testErn, testArc, CheckMode).url,
          id = DestinationOfficePage
        ).withVisuallyHiddenText(langMessages.hiddenChangeLinkText)
      )
    ))
  }
}
