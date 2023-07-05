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
import models.UnitOfMeasure.reads
import models.WrongWithMovement.{BrokenSeals, Damaged, Other}
import models.requests.DataRequest
import models.{CheckMode, NormalMode, ReviewMode, UnitOfMeasure, WrongWithMovement}
import pages.unsatisfactory.individualItems._
import play.api.i18n.Messages
import play.api.libs.json.Format.GenericFormat
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import utils.JsonOptionFormatter
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.{link, list}

import javax.inject.Inject

class CheckAnswersItemHelper @Inject()(
                                        shortageOrExcessItemSummary: ShortageOrExcessItemSummary,
                                        list: list,
                                        link: link
                                      ) extends JsonOptionFormatter {

  def summaryList(idx: Int, unitOfMeasure: UnitOfMeasure, onFinalCheckAnswers: Boolean = false)
                 (implicit request: DataRequest[_], messages: Messages): SummaryList = {
    val additionalLinkIdSignifier = if (onFinalCheckAnswers) s"-item-$idx" else ""

    val rows: Seq[SummaryListRow] =
      request.userAnswers.get(WrongWithItemPage(idx)).map {
        answers =>
          Seq(
            amountRefusedRow(idx, unitOfMeasure, additionalLinkIdSignifier),
            whatWasWrongRow(answers, idx, additionalLinkIdSignifier),
            shortageOrExcessItemSummary.rows(idx, unitOfMeasure, additionalLinkIdSignifier),
            damagedItemsInformationRow(idx, additionalLinkIdSignifier),
            brokenSealsInformationRow(idx, additionalLinkIdSignifier),
            otherInformationRow(idx, additionalLinkIdSignifier)
          ).flatten
      }.getOrElse(Seq.empty)

    SummaryListViewModel(rows = rows)
  }

  private def whatWasWrongRow(answers: Set[WrongWithMovement], idx: Int, additionalLinkIdSignifier: String)
                             (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    Some(SummaryListRowViewModel(
      key = s"${WrongWithItemPage(idx)}.checkYourAnswers.label",
      value = ValueViewModel(HtmlContent(
        list(WrongWithMovement.individualItemValues.filter(answers.contains).map { answer =>
          Html(messages(s"${WrongWithItemPage(idx)}.checkYourAnswers.$answer"))
        })
      )),
      actions = Seq(
        ActionItemViewModel(
          "site.change",
          routes.WrongWithMovementController.loadWrongWithItem(request.userAnswers.ern, request.userAnswers.arc, idx, NormalMode).url,
          id = WrongWithItemPage(idx) + additionalLinkIdSignifier
        ).withVisuallyHiddenText(messages(s"${WrongWithItemPage(idx)}.checkYourAnswers.change.hidden"))
      )
    ))

  private def brokenSealsInformationRow(idx: Int, additionalLinkIdSignifier: String)
                                       (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    if (request.userAnswers.get(WrongWithItemPage(idx)).exists(_.contains(BrokenSeals))) {
      val mode = if (additionalLinkIdSignifier != "") ReviewMode else CheckMode
      request.userAnswers.get(ItemSealsInformationPage(idx)).map {
        case Some(value) if value != "" =>
          SummaryListRowViewModel(
            key = s"${ItemSealsInformationPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(Text(value)),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                routes.ItemMoreInformationController.loadItemSealsInformation(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
                id = ItemSealsInformationPage(idx) + additionalLinkIdSignifier
              ).withVisuallyHiddenText(messages(s"${ItemSealsInformationPage(idx)}.checkYourAnswers.change.hidden"))
            )
          )
        case _ =>
          SummaryListRowViewModel(
            key = s"${ItemSealsInformationPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.ItemMoreInformationController.loadItemSealsInformation(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
              messageKey = s"${ItemSealsInformationPage(idx)}.checkYourAnswers.addMoreInformation")))
          )
      }
    } else {
      None
    }
  }


  private def amountRefusedRow(idx: Int, unitOfMeasure: UnitOfMeasure, additionalLinkIdSignifier: String)
                              (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {

    if (request.userAnswers.get(RefusingAnyAmountOfItemPage(idx)).nonEmpty) {
      val mode = if (additionalLinkIdSignifier != "") ReviewMode else CheckMode
      request.userAnswers.get(RefusingAnyAmountOfItemPage(idx)).map {
        case _ if request.userAnswers.get(RefusedAmountPage(idx)).isEmpty =>
          SummaryListRowViewModel(
            key = s"${RefusingAnyAmountOfItemPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.RefusedAmountController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
              messageKey = s"${RefusingAnyAmountOfItemPage(idx)}.checkYourAnswers.addMoreInformation")))
          )
        case _ =>
          SummaryListRowViewModel(
            key = s"${RefusingAnyAmountOfItemPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(
              messages(
                s"${RefusingAnyAmountOfItemPage(idx)}.checkYourAnswers.amount.value",
                request.userAnswers.get(RefusedAmountPage(idx)).get.toString(),
                messages(s"unitOfMeasure.$unitOfMeasure.long")
              )
            ),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                routes.RefusedAmountController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
                id = RefusingAnyAmountOfItemPage(idx) + additionalLinkIdSignifier
              ).withVisuallyHiddenText(messages(s"${RefusingAnyAmountOfItemPage(idx)}.checkYourAnswers.change.hidden"))
            )
          )
      }
    } else {
      None
    }
  }

  private def damagedItemsInformationRow(idx: Int, additionalLinkIdSignifier: String)
                                        (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    if (request.userAnswers.get(WrongWithItemPage(idx)).exists(_.contains(Damaged))) {
      val mode = if (additionalLinkIdSignifier != "") ReviewMode else CheckMode
      request.userAnswers.get(ItemDamageInformationPage(idx)).map {
        case Some(value) if value != "" =>
          SummaryListRowViewModel(
            key = s"${ItemDamageInformationPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(Text(value)),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                routes.ItemMoreInformationController.loadItemDamageInformation(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
                id = ItemDamageInformationPage(idx) + additionalLinkIdSignifier
              ).withVisuallyHiddenText(messages(s"${ItemDamageInformationPage(idx)}.checkYourAnswers.change.hidden"))
            )
          )
        case _ =>
          SummaryListRowViewModel(
            key = s"${ItemDamageInformationPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.ItemMoreInformationController.loadItemDamageInformation(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
              messageKey = s"${ItemDamageInformationPage(idx)}.checkYourAnswers.addMoreInformation")))
          )
      }
    } else {
      None
    }
  }

  private def otherInformationRow(idx: Int, additionalLinkIdSignifier: String)
                                 (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(WrongWithItemPage(idx)).exists(_.contains(Other))) {
      request.userAnswers.get(ItemOtherInformationPage(idx)).flatMap {
        case value if value != "" =>
          val mode = if (additionalLinkIdSignifier != "") ReviewMode else CheckMode
          Some(SummaryListRowViewModel(
            key = s"${ItemOtherInformationPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(Text(value)),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                routes.ItemOtherInformationController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
                id = ItemOtherInformationPage(idx) + additionalLinkIdSignifier
              ).withVisuallyHiddenText(messages(s"${ItemOtherInformationPage(idx)}.checkYourAnswers.change.hidden"))
            )
          ))
        case _ =>
          None
      }
    } else {
      None
    }
}
