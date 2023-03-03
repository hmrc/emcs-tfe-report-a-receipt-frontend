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

import models.AcceptMovement.Unsatisfactory
import models.CheckMode
import models.WrongWithMovement.{BrokenSeals, Damaged, Less, More, Other}
import models.requests.DataRequest
import pages.unsatisfactory._
import pages.{AcceptMovementPage, MoreInformationPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.govuk.summarylist._

import javax.inject.Inject

class CheckAnswersHelper @Inject()(acceptMovementSummary: AcceptMovementSummary,
                                   dateOfArrivalSummary: DateOfArrivalSummary,
                                   howMuchIsWrongSummary: HowMuchIsWrongSummary,
                                   moreInformationSummary: MoreInformationSummary,
                                   otherInformationSummary: OtherInformationSummary,
                                   wrongWithMovementSummary: WrongWithMovementSummary) {

  def summaryList()(implicit request: DataRequest[_], messages: Messages): SummaryList = {

    val commonRows = Seq(
      dateOfArrivalSummary.row(),
      acceptMovementSummary.row()
    ).flatten

    val unsatisfactoryRows =
      if (request.userAnswers.get(AcceptMovementPage).contains(Unsatisfactory)) {
        Seq(
          howMuchIsWrongSummary.row(),
          wrongWithMovementSummary.row(),
          shortageInformation(),
          excessInformation(),
          damageInformation(),
          sealsInformation(),
          otherInformation()
        ).flatten
      } else {
        Seq()
      }

    SummaryListViewModel(
      rows = commonRows ++ unsatisfactoryRows :+
        moreInformationSummary.row(
          page = MoreInformationPage,
          changeAction = controllers.routes.MoreInformationController.loadMoreInformation(request.ern, request.arc, CheckMode)
        )
    ).withCssClass("govuk-!-margin-bottom-9")
  }

  private def shortageInformation()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(WrongWithMovementPage).exists(_.contains(Less))) {
      Some(moreInformationSummary.row(
        page = ShortageInformationPage,
        changeAction = controllers.routes.MoreInformationController.loadShortageInformation(request.ern, request.arc, CheckMode))
      )
    } else {
      None
    }

  private def excessInformation()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(WrongWithMovementPage).exists(_.contains(More))) {
      Some(moreInformationSummary.row(
        page = ExcessInformationPage,
        changeAction = controllers.routes.MoreInformationController.loadExcessInformation(request.ern, request.arc, CheckMode))
      )
    } else {
      None
    }

  private def damageInformation()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(WrongWithMovementPage).exists(_.contains(Damaged))) {
      Some(moreInformationSummary.row(
        page = DamageInformationPage,
        changeAction = controllers.routes.MoreInformationController.loadDamageInformation(request.ern, request.arc, CheckMode))
      )
    } else {
      None
    }

  private def sealsInformation()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(WrongWithMovementPage).exists(_.contains(BrokenSeals))) {
      Some(moreInformationSummary.row(
        page = SealsInformationPage,
        changeAction = controllers.routes.MoreInformationController.loadSealsInformation(request.ern, request.arc, CheckMode))
      )
    } else {
      None
    }

  private def otherInformation()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(WrongWithMovementPage).exists(_.contains(Other))) {
      Some(otherInformationSummary.row())
    } else {
      None
    }
}
