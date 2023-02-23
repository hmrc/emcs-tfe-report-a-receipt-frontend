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
import mocks.viewmodels.{MockAcceptMovementSummary, MockDateOfArrivalSummary, MockHowMuchIsWrongSummary, MockMoreInformationSummary}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{SummaryList, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.checkAnswers.CheckAnswersHelper

class CheckAnswersHelperSpec extends SpecBase
  with MockDateOfArrivalSummary
  with MockAcceptMovementSummary
  with MockHowMuchIsWrongSummary
  with MockMoreInformationSummary
{

  lazy val checkAnswersHelper = new CheckAnswersHelper(
    mockAcceptMovementSummary,
    mockDateOfArrivalSummary,
    mockHowMuchIsWrongSummary,
    mockMoreInformationSummary
  )

  lazy val app = applicationBuilder().build()
  implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)
  implicit lazy val msgs = messages(app)

  "CheckAnswersHelperHelper" - {

    s"must return the expected SummaryList" in {

      val dateOfArrivalAnswer = SummaryListRow(
        "DateOfArrival",
        ValueViewModel("today")
      )
      val acceptMovementAnswer = SummaryListRow(
        "AcceptMovement",
        ValueViewModel("Yes")
      )
      val howMuchIsWrongAnswer = SummaryListRow(
        "HowMuchIsWrong",
        ValueViewModel("Whole Movement")
      )
      val moreInformationAnswer = SummaryListRow(
        "MoreInfo",
        ValueViewModel("Info")
      )

      MockDateOfArrivalSummary.row().returns(Some(dateOfArrivalAnswer))
      MockAcceptMovementSummary.row().returns(Some(acceptMovementAnswer))
      MockHowMuchIsWrongSummary.row().returns(Some(howMuchIsWrongAnswer))
      MockMoreInformationSummary.row().returns(moreInformationAnswer)

      checkAnswersHelper.summaryList() mustBe SummaryList(Seq(
        dateOfArrivalAnswer,
        acceptMovementAnswer,
        howMuchIsWrongAnswer,
        moreInformationAnswer
      )).withCssClass("govuk-!-margin-bottom-9")
    }
  }
}
