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
import models.response.emcsTfe.MovementItem
import models.{CheckMode, NormalMode, WrongWithMovement}
import pages.unsatisfactory.individualItems.{ItemDamageInformationPage, ItemOtherInformationPage, ItemSealsInformationPage, WrongWithItemPage}
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

  def itemName(item: MovementItem): String = {
    // TODO: replace with real description once we've hooked into the reference data
    item.cnCode
  }

  def summaryList(idx: Int, item: MovementItem)(implicit request: DataRequest[_], messages: Messages): SummaryList = {

    val rows: Seq[SummaryListRow] =
      request.userAnswers.get(WrongWithItemPage(idx)).map {
        answers =>
          Seq(
            whatWasWrongRow(answers, idx),
            shortageOrExcessItemSummary.rows(idx, item),
            damagedItemsInformationRow(idx),
            brokenSealsInformationRow(idx),
            otherInformationRow(idx)
          ).flatten
      }.getOrElse(Seq.empty)

    SummaryListViewModel(rows = rows)
  }

  private def whatWasWrongRow(answers: Set[WrongWithMovement], idx: Int)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
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
          routes.WrongWithMovementController.loadwrongWithItem(request.userAnswers.ern, request.userAnswers.arc, idx, NormalMode).url,
          id = s"${WrongWithItemPage(idx)}-item-$idx"
        ).withVisuallyHiddenText(messages(s"${WrongWithItemPage(idx)}.checkYourAnswers.change.hidden"))
      )
    ))

  private def brokenSealsInformationRow(idx: Int)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    if (request.userAnswers.get(WrongWithItemPage(idx)).exists(_.contains(BrokenSeals))) {
      request.userAnswers.get(ItemSealsInformationPage(idx)).map {
        case Some(value) if value != "" =>
          SummaryListRowViewModel(
            key = s"${ItemSealsInformationPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(Text(value)),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                routes.MoreInformationController.loadItemSealsInformation(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
                id = s"${ItemSealsInformationPage(idx)}-item-$idx"
              ).withVisuallyHiddenText(messages(s"${ItemSealsInformationPage(idx)}.checkYourAnswers.change.hidden"))
            )
          )
        case _ =>
          SummaryListRowViewModel(
            key = s"${ItemSealsInformationPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.MoreInformationController.loadItemSealsInformation(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
              messageKey = s"${ItemSealsInformationPage(idx)}.checkYourAnswers.addMoreInformation")))
          )
      }
    } else {
      None
    }
  }

  private def damagedItemsInformationRow(idx: Int)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    if (request.userAnswers.get(WrongWithItemPage(idx)).exists(_.contains(Damaged))) {
      request.userAnswers.get(ItemDamageInformationPage(idx)).map {
        case Some(value) if value != "" =>
          SummaryListRowViewModel(
            key = s"${ItemDamageInformationPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(Text(value)),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                routes.MoreInformationController.loadItemDamageInformation(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
                id = s"${ItemDamageInformationPage(idx)}-item-$idx"
              ).withVisuallyHiddenText(messages(s"${ItemDamageInformationPage(idx)}.checkYourAnswers.change.hidden"))
            )
          )
        case _ =>
          SummaryListRowViewModel(
            key = s"${ItemDamageInformationPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.MoreInformationController.loadItemDamageInformation(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
              messageKey = s"${ItemDamageInformationPage(idx)}.checkYourAnswers.addMoreInformation")))
          )
      }
    } else {
      None
    }
  }

  private def otherInformationRow(idx: Int)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    if (request.userAnswers.get(WrongWithItemPage(idx)).exists(_.contains(Other))) {
      request.userAnswers.get(ItemOtherInformationPage(idx)).flatMap {
        case value if value != "" =>
          Some(SummaryListRowViewModel(
            key = s"${ItemOtherInformationPage(idx)}.checkYourAnswers.label",
            value = ValueViewModel(Text(value)),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                routes.OtherInformationController.loadItemOtherInformation(request.userAnswers.ern, request.userAnswers.arc, idx, CheckMode).url,
                id = s"${ItemOtherInformationPage(idx)}-item-$idx"
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
