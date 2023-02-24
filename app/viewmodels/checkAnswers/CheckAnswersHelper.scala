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

package viewmodels.checkAnswers

import models.requests.DataRequest
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.summarylist._

import javax.inject.Inject

class CheckAnswersHelper @Inject()(acceptMovementSummary: AcceptMovementSummary,
                                   dateOfArrivalSummary: DateOfArrivalSummary,
                                   howMuchIsWrongSummary: HowMuchIsWrongSummary,
                                   moreInformationSummary: MoreInformationSummary,
                                   wrongWithMovementSummary: WrongWithMovementSummary)  {

  def summaryList()(implicit request: DataRequest[_], messages: Messages): SummaryList =
    SummaryListViewModel(
      rows = Seq(
        dateOfArrivalSummary.row(),
        acceptMovementSummary.row(),
        howMuchIsWrongSummary.row(),
        wrongWithMovementSummary.row(),
        Some(moreInformationSummary.row()),
      ).flatten
    ).withCssClass("govuk-!-margin-bottom-9")
}
