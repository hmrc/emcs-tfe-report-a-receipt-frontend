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
import models.UnitOfMeasure._
import models.WrongWithMovement.{Shortage, ShortageOrExcess}
import models.{CheckMode, ItemShortageOrExcessModel, UnitOfMeasure, UserAnswers, WrongWithMovement}
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, WrongWithItemPage}
import play.api.Application
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions}
import viewmodels.checkAnswers.ShortageOrExcessItemSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class ShortageOrExcessItemSummarySpec extends SpecBase {

  private lazy val app: Application = applicationBuilder().build()
  private lazy val link: link = app.injector.instanceOf[link]
  implicit lazy val msgs: Messages = messages(app)

  trait Test {
    lazy val shortageOrExcessItemSummary = new ShortageOrExcessItemSummary(link)

    def wrongWithItemPageValue: Set[WrongWithMovement] = Set(ShortageOrExcess)

    def additionalInfo: Option[String] = Some("value")

    def answers: UserAnswers = emptyUserAnswers
      .set(WrongWithItemPage(1), wrongWithItemPageValue)
      .set(ItemShortageOrExcessPage(1), ItemShortageOrExcessModel(Shortage, 1, additionalInfo))

    implicit val request = dataRequest(FakeRequest(), answers)

    def unitOfMeasure: UnitOfMeasure = Kilograms

    lazy val shortageOrExcessRow = SummaryListRow(
      key = msgs(s"${ItemShortageOrExcessPage(1)}.checkYourAnswers.shortageOrExcess.label"),
      value = ValueViewModel(msgs(s"itemShortageOrExcess.shortageOrExcess.${Shortage.toString}")),
      actions = Some(Actions(items = Seq(
        ActionItem(
          href = routes.ItemShortageOrExcessController.onPageLoad(testErn, testArc, 1, CheckMode).url,
          content = msgs("site.change"),
          visuallyHiddenText = Some(msgs(s"${ItemShortageOrExcessPage(1)}.checkYourAnswers.shortageOrExcess.change.hidden")),
          attributes = Map("id" -> s"${ItemShortageOrExcessPage(1)}-shortageOrExcess")
        )
      )))
    )

    lazy val amountOfShortageOrExcessRow = SummaryListRow(
      key = msgs(s"${ItemShortageOrExcessPage(1)}.checkYourAnswers.amount.label"),
      value = ValueViewModel(
        msgs(
          s"${ItemShortageOrExcessPage(1)}.checkYourAnswers.amount.value",
          "1",
          msgs(s"unitOfMeasure.$unitOfMeasure.long")
        )
      ),
      actions = Some(Actions(items = Seq(
        ActionItem(
          href = routes.ItemShortageOrExcessController.onPageLoad(testErn, testArc, 1, CheckMode).url,
          content = msgs("site.change"),
          visuallyHiddenText = Some(msgs(s"${ItemShortageOrExcessPage(1)}.checkYourAnswers.amount.change.hidden")),
          attributes = Map("id" -> s"${ItemShortageOrExcessPage(1)}-amount")
        )
      )))
    )
  }

  "ShortageOrExcessItemSummary" - {

    "when WrongWithItemPage contains ShortageOrExcess" - {
      Seq(
        // TODO: add more units when reference data is called in a future story
        // this should be derived from the provided cnCode
        Kilograms,
        //        Litres15,
        //        Litres20,
        //        Thousands,
      ).foreach {
        unitOfMeasure =>
          s"when the unit of measure is $unitOfMeasure" - {
            "returns a Seq containing the additionalInfo value" - {
              "when additionalInfo is filled out" in new Test {
                val shortageOrExcessInformationRow = SummaryListRow(
                  key = msgs(s"${ItemShortageOrExcessPage(1)}.checkYourAnswers.additionalInfo.label"),
                  value = ValueViewModel(Text("value")),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemShortageOrExcessController.onPageLoad(testErn, testArc, 1, CheckMode).url,
                      content = msgs("site.change"),
                      visuallyHiddenText = Some(msgs(s"${ItemShortageOrExcessPage(1)}.checkYourAnswers.additionalInfo.change.hidden")),
                      attributes = Map("id" -> s"${ItemShortageOrExcessPage(1)}-additionalInfo")
                    )
                  )))
                )

                shortageOrExcessItemSummary.rows(1, Kilograms) mustBe Seq(
                  shortageOrExcessRow,
                  amountOfShortageOrExcessRow,
                  shortageOrExcessInformationRow
                )
              }
            }

            "returns a Seq containing the 'Enter more information' text" - {
              val shortageOrExcessInformationRow = SummaryListRow(
                key = msgs(s"${ItemShortageOrExcessPage(1)}.checkYourAnswers.additionalInfo.label"),
                value = ValueViewModel(HtmlContent(link(
                  link = routes.ItemShortageOrExcessController.onPageLoad(testErn, testArc, 1, CheckMode).url,
                  messageKey = s"${ItemShortageOrExcessPage(1)}.checkYourAnswers.addMoreInformation")))
              )
              "when additionalInfo is an empty String" in new Test {
                override def additionalInfo: Option[String] = Some("")

                shortageOrExcessItemSummary.rows(1, Kilograms) mustBe Seq(
                  shortageOrExcessRow,
                  amountOfShortageOrExcessRow,
                  shortageOrExcessInformationRow
                )
              }
              "when additionalInfo is None" in new Test {
                override def additionalInfo: Option[String] = None

                shortageOrExcessItemSummary.rows(1, Kilograms) mustBe Seq(
                  shortageOrExcessRow,
                  amountOfShortageOrExcessRow,
                  shortageOrExcessInformationRow
                )
              }
            }
          }

      }
    }

    "when WrongWithItemPage doesn't contain ShortageOrExcess" - {
      "returns an empty Seq" in new Test {
        override def answers: UserAnswers = emptyUserAnswers

        shortageOrExcessItemSummary.rows(1, Kilograms) mustBe Seq()
      }
    }
  }
}
