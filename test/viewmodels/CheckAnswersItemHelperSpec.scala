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
import fixtures.messages.UnitOfMeasureMessages.English.kilogramsLong
import mocks.viewmodels._
import models.UnitOfMeasure.Kilograms
import models.WrongWithMovement.{BrokenSeals, Damaged, Other, ShortageOrExcess}
import models.requests.DataRequest
import models.{CheckMode, NormalMode, ReviewMode, WrongWithMovement}
import pages.unsatisfactory.individualItems._
import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.{SummaryList, SummaryListRow}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions}
import viewmodels.checkAnswers.CheckAnswersItemHelper
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.{link, list}

class CheckAnswersItemHelperSpec extends SpecBase with MockShortageOrExcessItemSummary {
  private lazy val app: Application = applicationBuilder().build()
  private lazy val link: link = app.injector.instanceOf[link]
  private lazy val list: list = app.injector.instanceOf[list]

  implicit lazy val msgs: Messages = messages(app)

  trait Test {

    lazy val checkAnswersItemHelper = new CheckAnswersItemHelper(
      mockShortageOrExcessItemSummary, list, link
    )

    def wrongWithItemPageValue: Set[WrongWithMovement] = Set(ShortageOrExcess, Damaged, BrokenSeals, Other)

    def itemDamageInformationPageValue: Option[String] = Some("value")

    def itemSealsInformationPageValue: Option[String] = Some("value")

    def itemOtherInformationPageValue: String = "value"

    def refusingAnyAmountOfItemPageValue: Boolean = true

    def refusedAmountPageValue: BigDecimal = BigDecimal(1)


    implicit def request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
      FakeRequest(),
      emptyUserAnswers
        .set(RefusingAnyAmountOfItemPage(1), refusingAnyAmountOfItemPageValue)
        .set(RefusedAmountPage(1), refusedAmountPageValue)
        .set(WrongWithItemPage(1), wrongWithItemPageValue)
        .set(ItemDamageInformationPage(1), itemDamageInformationPageValue)
        .set(ItemSealsInformationPage(1), itemSealsInformationPageValue)
        .set(ItemOtherInformationPage(1), itemOtherInformationPageValue)
    )

    lazy val whatWasWrongRow = SummaryListRow(
      key = msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.label"),
      value = ValueViewModel(
        HtmlContent(list(
          Seq(
            Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$ShortageOrExcess")),
            Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$Damaged")),
            Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$BrokenSeals")),
            Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$Other"))
          )
        ))
      ),
      actions = Some(Actions(items = Seq(ActionItem(
        href = routes.WrongWithMovementController.loadWrongWithItem(testErn, testArc, 1, NormalMode).url,
        content = msgs("site.change"),
        visuallyHiddenText = Some(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.change.hidden")),
        attributes = Map("id" -> s"${WrongWithItemPage(1)}")
      ))))
    )
  }


  "CheckAnswersItemHelperHelper" - {

    "being rendered" - {

      "must return a filled-in SummaryList" - {

        s"when all values are filled in on item check your answers page" in new Test {

          MockShortageOrExcessItemSummary.rows().returns(Seq())

          lazy val refusedAmountRow = SummaryListRow(
            key = s"${RefusingAnyAmountOfItemPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(Text(s"1 $kilogramsLong")),
            actions = Some(Actions(items = Seq(ActionItem(
              href = routes.RefusedAmountController.onPageLoad(testErn, testArc, 1, CheckMode).url,
              content = msgs("site.change"),
              visuallyHiddenText = Some(msgs(s"${RefusingAnyAmountOfItemPage(1)}.checkYourAnswers.change.hidden")),
              attributes = Map("id" -> s"${RefusingAnyAmountOfItemPage(1)}")
            ))))
          )

          lazy val damagedGoodsInformationRow = SummaryListRow(
            key = s"${ItemDamageInformationPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(Text("value")),
            actions = Some(Actions(items = Seq(ActionItem(
              href = routes.ItemMoreInformationController.loadItemDamageInformation(testErn, testArc, 1, CheckMode).url,
              content = msgs("site.change"),
              visuallyHiddenText = Some(msgs(s"${ItemDamageInformationPage(1)}.checkYourAnswers.change.hidden")),
              attributes = Map("id" -> s"${ItemDamageInformationPage(1)}")
            ))))
          )

          lazy val brokenSealsInformationRow = SummaryListRow(
            key = s"${ItemSealsInformationPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(Text("value")),
            actions = Some(Actions(items = Seq(ActionItem(
              href = routes.MoreInformationController.loadItemSealsInformation(testErn, testArc, 1, CheckMode).url,
              content = msgs("site.change"),
              visuallyHiddenText = Some(msgs(s"${ItemSealsInformationPage(1)}.checkYourAnswers.change.hidden")),
              attributes = Map("id" -> s"${ItemSealsInformationPage(1)}")
            ))))
          )

          lazy val otherInformationRow = SummaryListRow(
            key = s"${ItemOtherInformationPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(Text("value")),
            actions = Some(Actions(items = Seq(ActionItem(
              href = routes.OtherInformationController.loadItemOtherInformation(testErn, testArc, 1, CheckMode).url,
              content = msgs("site.change"),
              visuallyHiddenText = Some(msgs(s"${ItemOtherInformationPage(1)}.checkYourAnswers.change.hidden")),
              attributes = Map("id" -> s"${ItemOtherInformationPage(1)}")
            ))))
          )

          checkAnswersItemHelper.summaryList(1, Kilograms) mustBe SummaryList(Seq(
            refusedAmountRow,
            whatWasWrongRow,
            damagedGoodsInformationRow,
            brokenSealsInformationRow,
            otherInformationRow
          ))
        }
      }

      s"when all values are filled in on final check your answers page" in new Test {


        MockShortageOrExcessItemSummary.rows().returns(Seq())

        lazy val damagedGoodsInformationRow = SummaryListRow(
          key = s"${ItemDamageInformationPage(1)}.checkYourAnswers.label",
          value = ValueViewModel(Text("value")),
          actions = Some(Actions(items = Seq(ActionItem(
            href = routes.ItemMoreInformationController.loadItemDamageInformation(testErn, testArc, 1, ReviewMode).url,
            content = msgs("site.change"),
            visuallyHiddenText = Some(msgs(s"${ItemDamageInformationPage(1)}.checkYourAnswers.change.hidden")),
            attributes = Map("id" -> s"${ItemDamageInformationPage(1)}-item-1")
          ))))
        )

        lazy val brokenSealsInformationRow = SummaryListRow(
          key = s"${ItemSealsInformationPage(1)}.checkYourAnswers.label",
          value = ValueViewModel(Text("value")),
          actions = Some(Actions(items = Seq(ActionItem(
            href = routes.MoreInformationController.loadItemSealsInformation(testErn, testArc, 1, ReviewMode).url,
            content = msgs("site.change"),
            visuallyHiddenText = Some(msgs(s"${ItemSealsInformationPage(1)}.checkYourAnswers.change.hidden")),
            attributes = Map("id" -> s"${ItemSealsInformationPage(1)}-item-1")
          ))))
        )

        lazy val otherInformationRow = SummaryListRow(
          key = s"${ItemOtherInformationPage(1)}.checkYourAnswers.label",
          value = ValueViewModel(Text("value")),
          actions = Some(Actions(items = Seq(ActionItem(
            href = routes.OtherInformationController.loadItemOtherInformation(testErn, testArc, 1, ReviewMode).url,
            content = msgs("site.change"),
            visuallyHiddenText = Some(msgs(s"${ItemOtherInformationPage(1)}.checkYourAnswers.change.hidden")),
            attributes = Map("id" -> s"${ItemOtherInformationPage(1)}-item-1")
          ))))
        )

        lazy val refusedAmountRow = SummaryListRow(
          key = s"${RefusingAnyAmountOfItemPage(1)}.checkYourAnswers.label",
          value = ValueViewModel(Text(s"1 $kilogramsLong")),
          actions = Some(Actions(items = Seq(ActionItem(
            href = routes.RefusedAmountController.onPageLoad(testErn, testArc, 1, ReviewMode).url,
            content = msgs("site.change"),
            visuallyHiddenText = Some(msgs(s"${RefusingAnyAmountOfItemPage(1)}.checkYourAnswers.change.hidden")),
            attributes = Map("id" -> s"${RefusingAnyAmountOfItemPage(1)}-item-1")
          ))))
        )

        lazy val whatWasWrongReviewRow = SummaryListRow(
          key = msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.label"),
          value = ValueViewModel(
            HtmlContent(list(
              Seq(
                Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$ShortageOrExcess")),
                Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$Damaged")),
                Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$BrokenSeals")),
                Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$Other"))
              )
            ))
          ),
          actions = Some(Actions(items = Seq(ActionItem(
            href = routes.WrongWithMovementController.loadWrongWithItem(testErn, testArc, 1, NormalMode).url,
            content = msgs("site.change"),
            visuallyHiddenText = Some(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.change.hidden")),
            attributes = Map("id" -> s"${WrongWithItemPage(1)}-item-1")
          ))))
        )

        checkAnswersItemHelper.summaryList(1, Kilograms, onFinalCheckAnswers = true) mustBe SummaryList(Seq(
          refusedAmountRow,
          whatWasWrongReviewRow,
          damagedGoodsInformationRow,
          brokenSealsInformationRow,
          otherInformationRow
        ))
      }

      "must return default text on optional rows" - {
        "when provided values are empty on check your items answers" in new Test {
          override implicit def request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(RefusingAnyAmountOfItemPage(1), false)
              .set(WrongWithItemPage(1), wrongWithItemPageValue)
              .set(ItemDamageInformationPage(1), itemDamageInformationPageValue)
              .set(ItemSealsInformationPage(1), itemSealsInformationPageValue)
              .set(ItemOtherInformationPage(1), itemOtherInformationPageValue)
          )

          override def itemDamageInformationPageValue: Option[String] = Some("")

          override def itemSealsInformationPageValue: Option[String] = Some("")

          override def itemOtherInformationPageValue: String = ""

          MockShortageOrExcessItemSummary.rows().returns(Seq())

          lazy val damagedGoodsInformationRow = SummaryListRow(
            key = s"${ItemDamageInformationPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.ItemMoreInformationController.loadItemDamageInformation(testErn, testArc, 1, CheckMode).url,
              messageKey = s"${ItemDamageInformationPage(1)}.checkYourAnswers.addMoreInformation"
            )))
          )

          lazy val brokenSealsInformationRow = SummaryListRow(
            key = s"${ItemSealsInformationPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.MoreInformationController.loadItemSealsInformation(testErn, testArc, 1, CheckMode).url,
              messageKey = s"${ItemSealsInformationPage(1)}.checkYourAnswers.addMoreInformation"
            )))
          )
          lazy val amountRefusedRow = SummaryListRow(
            key = s"${RefusingAnyAmountOfItemPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.RefusedAmountController.onPageLoad(testErn, testArc, 1, CheckMode).url,
              messageKey = s"${RefusingAnyAmountOfItemPage(1)}.checkYourAnswers.addMoreInformation"
            )))
          )


          checkAnswersItemHelper.summaryList(1, Kilograms) mustBe SummaryList(Seq(
            amountRefusedRow,
            whatWasWrongRow,
            damagedGoodsInformationRow,
            brokenSealsInformationRow
          ))
        }

        "when provided values are empty on check your final answers" in new Test {
          override implicit def request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(RefusingAnyAmountOfItemPage(1), false)
              .set(WrongWithItemPage(1), wrongWithItemPageValue)
              .set(ItemDamageInformationPage(1), itemDamageInformationPageValue)
              .set(ItemSealsInformationPage(1), itemSealsInformationPageValue)
              .set(ItemOtherInformationPage(1), itemOtherInformationPageValue)
          )

          override def itemDamageInformationPageValue: Option[String] = Some("")

          override def itemSealsInformationPageValue: Option[String] = Some("")

          override def itemOtherInformationPageValue: String = ""

          MockShortageOrExcessItemSummary.rows().returns(Seq())

          lazy val damagedGoodsInformationRow = SummaryListRow(
            key = s"${ItemDamageInformationPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.ItemMoreInformationController.loadItemDamageInformation(testErn, testArc, 1, ReviewMode).url,
              messageKey = s"${ItemDamageInformationPage(1)}.checkYourAnswers.addMoreInformation"
            )))
          )

          lazy val brokenSealsInformationRow = SummaryListRow(
            key = s"${ItemSealsInformationPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.MoreInformationController.loadItemSealsInformation(testErn, testArc, 1, ReviewMode).url,
              messageKey = s"${ItemSealsInformationPage(1)}.checkYourAnswers.addMoreInformation"
            )))
          )

          lazy val amountRefusedRow = SummaryListRow(
            key = s"${RefusingAnyAmountOfItemPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.RefusedAmountController.onPageLoad(testErn, testArc, 1, ReviewMode).url,
              messageKey = s"${RefusingAnyAmountOfItemPage(1)}.checkYourAnswers.addMoreInformation"
            )))
          )

          lazy val whatWasWrongReviewRow = SummaryListRow(
            key = msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.label"),
            value = ValueViewModel(
              HtmlContent(list(
                Seq(
                  Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$ShortageOrExcess")),
                  Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$Damaged")),
                  Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$BrokenSeals")),
                  Html(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.$Other"))
                )
              ))
            ),
            actions = Some(Actions(items = Seq(ActionItem(
              href = routes.WrongWithMovementController.loadWrongWithItem(testErn, testArc, 1, NormalMode).url,
              content = msgs("site.change"),
              visuallyHiddenText = Some(msgs(s"${WrongWithItemPage(1)}.checkYourAnswers.change.hidden")),
              attributes = Map("id" -> s"${WrongWithItemPage(1)}-item-1")
            ))))
          )


          checkAnswersItemHelper.summaryList(1, Kilograms, true) mustBe SummaryList(Seq(
            amountRefusedRow,
            whatWasWrongReviewRow,
            damagedGoodsInformationRow,
            brokenSealsInformationRow
          ))
        }

        "when provided values are None" in new Test {
          override implicit def request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(WrongWithItemPage(1), wrongWithItemPageValue)
              .set(ItemDamageInformationPage(1), itemDamageInformationPageValue)
              .set(ItemSealsInformationPage(1), itemSealsInformationPageValue)
              .set(ItemOtherInformationPage(1), itemOtherInformationPageValue)
          )

          override def itemDamageInformationPageValue: Option[String] = None

          override def itemSealsInformationPageValue: Option[String] = None

          override def itemOtherInformationPageValue: String = ""

          MockShortageOrExcessItemSummary.rows().returns(Seq())

          lazy val damagedGoodsInformationRow = SummaryListRow(
            key = s"${ItemDamageInformationPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.ItemMoreInformationController.loadItemDamageInformation(testErn, testArc, 1, CheckMode).url,
              messageKey = s"${ItemDamageInformationPage(1)}.checkYourAnswers.addMoreInformation"
            )))
          )

          lazy val brokenSealsInformationRow = SummaryListRow(
            key = s"${ItemSealsInformationPage(1)}.checkYourAnswers.label",
            value = ValueViewModel(HtmlContent(link(
              link = routes.MoreInformationController.loadItemSealsInformation(testErn, testArc, 1, CheckMode).url,
              messageKey = s"${ItemSealsInformationPage(1)}.checkYourAnswers.addMoreInformation"
            )))
          )

          checkAnswersItemHelper.summaryList(1, Kilograms) mustBe SummaryList(Seq(
            whatWasWrongRow,
            damagedGoodsInformationRow,
            brokenSealsInformationRow
          ))
        }
      }

      "must return no rows" - {
        "when there's no WrongWithItemPage for the given idx" in new Test {

          override implicit def request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
          )

          checkAnswersItemHelper.summaryList(1, Kilograms) mustBe SummaryList(Seq())
        }

      }

      "must return only the WhatWasWrong row" - {
        "when the WrongWithItemPage for the given idx contains no WrongWithMovement values" in new Test {
          override implicit def request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(WrongWithItemPage(1), wrongWithItemPageValue)
              .set(ItemDamageInformationPage(1), itemDamageInformationPageValue)
              .set(ItemSealsInformationPage(1), itemSealsInformationPageValue)
              .set(ItemOtherInformationPage(1), itemOtherInformationPageValue)
          )

          override def wrongWithItemPageValue: Set[WrongWithMovement] = Set()

          MockShortageOrExcessItemSummary.rows().returns(Seq())

          checkAnswersItemHelper.summaryList(1, Kilograms) mustBe
            SummaryList(Seq(whatWasWrongRow.copy(value = ValueViewModel(HtmlContent(list(Seq()))))))
        }
      }
    }
  }
}
