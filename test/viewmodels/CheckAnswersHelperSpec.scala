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
import mocks.viewmodels._
import models.AcceptMovement.{Satisfactory, Unsatisfactory}
import models.WrongWithMovement.{BrokenSeals, Damaged, Less, More, Other}
import models.{CheckMode, WrongWithMovement}
import pages.unsatisfactory._
import pages.{AcceptMovementPage, MoreInformationPage}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{SummaryList, SummaryListRow}
import viewmodels.checkAnswers.CheckAnswersHelper
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class CheckAnswersHelperSpec extends SpecBase
  with MockDateOfArrivalSummary
  with MockAcceptMovementSummary
  with MockHowMuchIsWrongSummary
  with MockMoreInformationSummary
  with MockWrongWithMovementSummary
{

  lazy val checkAnswersHelper = new CheckAnswersHelper(
    mockAcceptMovementSummary,
    mockDateOfArrivalSummary,
    mockHowMuchIsWrongSummary,
    mockMoreInformationSummary,
    mockWrongWithMovementSummary
  )

  lazy val app = applicationBuilder().build()
  implicit lazy val msgs = messages(app)

  "CheckAnswersHelperHelper" - {

    "being rendered for the Satisfactory flow" - {

      implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(AcceptMovementPage, Satisfactory))

      s"must return the expected SummaryList" in {

        val dateOfArrivalAnswer = SummaryListRow(
          "DateOfArrival",
          ValueViewModel("today")
        )
        val acceptMovementAnswer = SummaryListRow(
          "AcceptMovement",
          ValueViewModel("Yes")
        )
        val moreInformationAnswer = SummaryListRow(
          "MoreInfo",
          ValueViewModel("Info")
        )

        MockDateOfArrivalSummary.row().returns(Some(dateOfArrivalAnswer))
        MockAcceptMovementSummary.row().returns(Some(acceptMovementAnswer))
        MockMoreInformationSummary.row(
          MoreInformationPage,
          controllers.routes.MoreInformationController.loadMoreInformation(testErn, testArc, CheckMode)
        ).returns(moreInformationAnswer)

        checkAnswersHelper.summaryList() mustBe SummaryList(Seq(
          dateOfArrivalAnswer,
          acceptMovementAnswer,
          moreInformationAnswer
        )).withCssClass("govuk-!-margin-bottom-9")
      }
    }

    "being rendered for the Unsatisfactory flow" - {

      implicit lazy val request = dataRequest(
        FakeRequest(),
        emptyUserAnswers
          .set(AcceptMovementPage, Unsatisfactory)
          .set(WrongWithMovementPage, Set[WrongWithMovement](Less, More, Damaged, BrokenSeals, Other))
      )

      s"must return the expected SummaryList" in {

        val dateOfArrivalAnswer = SummaryListRow("DateOfArrival", ValueViewModel("today"))
        val acceptMovementAnswer = SummaryListRow("AcceptMovement", ValueViewModel("Yes"))
        val howMuchIsWrongAnswer = SummaryListRow("HowMuchIsWrong", ValueViewModel("Whole Movement"))
        val wrongWithMovementAnswer = SummaryListRow("WrongWithMovement", ValueViewModel("Less"))
        val shortageInformationAnswer = SummaryListRow("ShortageInfo", ValueViewModel("Info"))
        val excessInformationAnswer = SummaryListRow("ExcessInfo", ValueViewModel("Info"))
        val damagedInformationAnswer = SummaryListRow("DamageInfo", ValueViewModel("Info"))
        val sealsInformationAnswer = SummaryListRow("SealsInfo", ValueViewModel("Info"))
        val otherInformationAnswer = SummaryListRow("OtherInfo", ValueViewModel("Info"))
        val moreInformationAnswer = SummaryListRow("MoreInfo", ValueViewModel("Info"))

        MockDateOfArrivalSummary.row().returns(Some(dateOfArrivalAnswer))
        MockAcceptMovementSummary.row().returns(Some(acceptMovementAnswer))
        MockHowMuchIsWrongSummary.row().returns(Some(howMuchIsWrongAnswer))
        MockWrongWithMovementSummary.row().returns(Some(wrongWithMovementAnswer))
        MockMoreInformationSummary.row(
          ShortageInformationPage,
          controllers.routes.MoreInformationController.loadShortageInformation(testErn, testArc, CheckMode)
        ).returns(shortageInformationAnswer)
        MockMoreInformationSummary.row(
          ExcessInformationPage,
          controllers.routes.MoreInformationController.loadExcessInformation(testErn, testArc, CheckMode)
        ).returns(excessInformationAnswer)
        MockMoreInformationSummary.row(
          DamageInformationPage,
          controllers.routes.MoreInformationController.loadDamageInformation(testErn, testArc, CheckMode)
        ).returns(damagedInformationAnswer)
        MockMoreInformationSummary.row(
          SealsInformationPage,
          controllers.routes.MoreInformationController.loadSealsInformation(testErn, testArc, CheckMode)
        ).returns(sealsInformationAnswer)
        MockMoreInformationSummary.row(
          OtherInformationPage,
          controllers.routes.MoreInformationController.loadOtherInformation(testErn, testArc, CheckMode)
        ).returns(otherInformationAnswer)
        MockMoreInformationSummary.row(
          MoreInformationPage,
          controllers.routes.MoreInformationController.loadMoreInformation(testErn, testArc, CheckMode)
        ).returns(moreInformationAnswer)

        checkAnswersHelper.summaryList() mustBe SummaryList(Seq(
          dateOfArrivalAnswer,
          acceptMovementAnswer,
          howMuchIsWrongAnswer,
          wrongWithMovementAnswer,
          shortageInformationAnswer,
          excessInformationAnswer,
          damagedInformationAnswer,
          sealsInformationAnswer,
          otherInformationAnswer,
          moreInformationAnswer
        )).withCssClass("govuk-!-margin-bottom-9")
      }
    }
  }
}
