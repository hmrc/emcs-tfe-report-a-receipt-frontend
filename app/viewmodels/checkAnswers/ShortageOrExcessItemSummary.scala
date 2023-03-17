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

import controllers.routes
import models.CheckMode
import models.UnitOfMeasure.Kilograms
import models.WrongWithMovement.ShortageOrExcess
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, WrongWithItemPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

import javax.inject.Inject

class ShortageOrExcessItemSummary @Inject()(link: link) {

  def rows(idx: Int, item: MovementItem)(implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {
    Seq(
      shortageOrExcessRow(idx),
      amountOfShortageOrExcessRow(idx, item),
      shortageOrExcessInformationRow(idx)
    ).flatten
  }

  private def shortageOrExcessRow(idx: Int)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    if (isShortageOrExcess(idx)) {
      request.userAnswers.get(ItemShortageOrExcessPage(idx)).map(
        shortageOrExcess =>
          SummaryListRowViewModel(
            key = s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.shortageOrExcess.label",
            value = ValueViewModel(Text(shortageOrExcess.wrongWithItem.toString)),
            actions = Seq(
              shortageOrExcessChangeAction(idx, "shortageOrExcess")
            )
          )
      )
    } else {
      None
    }
  }

  private def amountOfShortageOrExcessRow(idx: Int, item: MovementItem)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    if (isShortageOrExcess(idx)) {
      request.userAnswers.get(ItemShortageOrExcessPage(idx)).map(
        shortageOrExcess =>
          SummaryListRowViewModel(
            key = s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.amount.label",
            //TODO: Hardcoded to kg, should be determined from the reference data based on the CN Code in future story
            value = ValueViewModel(
              messages(
                s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.amount.value",
                shortageOrExcess.amount.toString,
                messages(s"unitOfMeasure.$Kilograms.long")
              )
            ),
            actions = Seq(
              shortageOrExcessChangeAction(idx, "amount")
            )
          )
      )
    } else {
      None
    }
  }

  private def shortageOrExcessInformationRow(idx: Int)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    if (isShortageOrExcess(idx)) {
      request.userAnswers.get(ItemShortageOrExcessPage(idx)).map(
        shortageOrExcess =>
          shortageOrExcess.additionalInfo match {
            case Some(value) if value != "" =>
              SummaryListRowViewModel(
                key = s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.additionalInfo.label",
                value = ValueViewModel(Text(value)),
                actions = Seq(
                  shortageOrExcessChangeAction(idx, "additionalInfo")
                )
              )
            case _ =>
              SummaryListRowViewModel(
                key = s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.additionalInfo.label",
                value = ValueViewModel(HtmlContent(link(
                  link = routes.ItemShortageOrExcessController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
                  messageKey = s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.addMoreInformation")))
              )
          })
    } else {
      None
    }
  }

  private def isShortageOrExcess(idx: Int)(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(WrongWithItemPage(idx)).exists(_.contains(ShortageOrExcess))

  private def shortageOrExcessChangeAction(idx: Int, changeLabelPage: String)(implicit request: DataRequest[_], messages: Messages): ActionItem =
    ActionItemViewModel(
      "site.change",
      routes.ItemShortageOrExcessController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
      id = s"${ItemShortageOrExcessPage(idx)}-$changeLabelPage"
    ).withVisuallyHiddenText(messages(s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.$changeLabelPage.change.hidden"))

}
