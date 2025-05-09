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
import models.WrongWithMovement.ShortageOrExcess
import models.requests.DataRequest
import models.{CheckMode, ReviewMode, UnitOfMeasure}
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, WrongWithItemPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

import javax.inject.Inject

class ShortageOrExcessItemSummary @Inject()(link: link) {

  def rows(idx: Int, unitOfMeasure: UnitOfMeasure, isOnFinalCheckAnswers: Boolean = false)
          (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {

    Seq(
      shortageOrExcessRow(idx, isOnFinalCheckAnswers),
      amountOfShortageOrExcessRow(idx, unitOfMeasure, isOnFinalCheckAnswers),
      shortageOrExcessInformationRow(idx, isOnFinalCheckAnswers)
    ).flatten
  }

  private def shortageOrExcessRow(idx: Int, isOnFinalCheckAnswers: Boolean)
                                 (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    if (isShortageOrExcess(idx)) {
      request.userAnswers.get(ItemShortageOrExcessPage(idx)).map(
        shortageOrExcess =>
          SummaryListRowViewModel(
            key = s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.shortageOrExcess.label",
            value = ValueViewModel(messages(s"itemShortageOrExcess.shortageOrExcess.${shortageOrExcess.wrongWithItem.toString}")),
            actions = Seq(
              shortageOrExcessChangeAction(idx, s"shortageOrExcess", isOnFinalCheckAnswers)
            )
          )
      )
    } else {
      None
    }
  }

  private def amountOfShortageOrExcessRow(idx: Int, unitOfMeasure: UnitOfMeasure, isOnFinalCheckAnswers: Boolean)
                                         (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    if (isShortageOrExcess(idx)) {
      request.userAnswers.get(ItemShortageOrExcessPage(idx)).map(
        shortageOrExcess =>
          SummaryListRowViewModel(
            key = s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.amount.label",
            value = ValueViewModel(
              messages(
                s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.amount.value",
                shortageOrExcess.amount.toString,
                messages(s"unitOfMeasure.$unitOfMeasure.long")
              )
            ),
            actions = Seq(
              shortageOrExcessChangeAction(idx, s"amount", isOnFinalCheckAnswers)
            )
          )
      )
    } else {
      None
    }
  }

  private def shortageOrExcessInformationRow(idx: Int, isOnFinalCheckAnswers: Boolean)
                                            (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    if (isShortageOrExcess(idx)) {
      val mode = if(isOnFinalCheckAnswers) ReviewMode else CheckMode
      request.userAnswers.get(ItemShortageOrExcessPage(idx)).map(
        shortageOrExcess =>
          shortageOrExcess.additionalInfo match {
            case Some(value) if value != "" =>
              SummaryListRowViewModel(
                key = s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.additionalInfo.label",
                value = ValueViewModel(Text(value)),
                actions = Seq(
                  shortageOrExcessChangeAction(idx, s"additionalInfo", isOnFinalCheckAnswers)
                )
              )
            case _ =>
              SummaryListRowViewModel(
                key = s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.additionalInfo.label",
                value = ValueViewModel(HtmlContent(link(
                  link = routes.ItemShortageOrExcessController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
                  messageKey = s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.addMoreInformation",
                  hiddenContent = Some(messages("addedItems.checkYourAnswers.forItem", idx))
                )))
              )
          })
    } else {
      None
    }
  }

  private def isShortageOrExcess(idx: Int)(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(WrongWithItemPage(idx)).exists(_.contains(ShortageOrExcess))

  private def shortageOrExcessChangeAction(idx: Int,
                                           changeLabelPage: String,
                                           isOnFinalCheckAnswers: Boolean)(implicit request: DataRequest[_], messages: Messages): ActionItem = {
    val mode = if(isOnFinalCheckAnswers) ReviewMode else CheckMode
    ActionItemViewModel(
      "site.change",
      routes.ItemShortageOrExcessController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
      id = s"${ItemShortageOrExcessPage(idx)}-$changeLabelPage-item-$idx"
    ).withVisuallyHiddenText(
      s"${messages(s"${ItemShortageOrExcessPage(idx)}.checkYourAnswers.$changeLabelPage.change.hidden")} ${messages("addedItems.checkYourAnswers.forItem", idx)}"
    )
  }

}
