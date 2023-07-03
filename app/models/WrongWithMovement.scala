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

package models

import config.AppConfig
import featureswitch.core.config.{FeatureSwitching, NewShortageExcessFlow}
import pages.QuestionPage
import pages.unsatisfactory.WrongWithMovementPage
import pages.unsatisfactory.individualItems.WrongWithItemPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import viewmodels.govuk.checkbox._

sealed trait WrongWithMovement

object WrongWithMovement extends Enumerable.Implicits with FeatureSwitching {

  case object Shortage extends WithName("shortage") with WrongWithMovement
  case object Excess extends WithName("excess") with WrongWithMovement
  case object ShortageOrExcess extends WithName("shortageOrExcess") with WrongWithMovement
  case object Damaged extends WithName("damaged") with WrongWithMovement
  case object BrokenSeals extends WithName("brokenSeals") with WrongWithMovement
  case object Other extends WithName("other") with WrongWithMovement

  val values: Seq[WrongWithMovement] = Seq(
    Shortage,
    Excess,
    Damaged,
    BrokenSeals,
    Other
  )

  val individualItemValues: Seq[WrongWithMovement] = Seq(
    ShortageOrExcess,
    Damaged,
    BrokenSeals,
    Other
  )

  def checkboxItems(page: QuestionPage[Set[WrongWithMovement]])(implicit messages: Messages, config: AppConfig): Seq[CheckboxItem] = {
    val checkboxes = page match {
      case WrongWithMovementPage => values
      case _: WrongWithItemPage if isEnabled(NewShortageExcessFlow) => values
      case _ => individualItemValues
    }

    checkboxes.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(s"$page.${value.toString}")),
          fieldId = "value",
          index   = index,
          value   = value.toString
        ).withId(value.toString)
    }
  }

  def itemShortageOrExcessOptions(implicit messages: Messages): Seq[RadioItem] =
    Seq(
      RadioItem(
        content = Text(messages(s"itemShortageOrExcess.shortageOrExcess.$Shortage")),
        value = Some(Shortage.toString),
        id = Some(Shortage.toString)
      ),
      RadioItem(
        content = Text(messages(s"itemShortageOrExcess.shortageOrExcess.$Excess")),
        value = Some(Excess.toString),
        id = Some(Excess.toString)
      )
    )

  implicit val enumerable: Enumerable[WrongWithMovement] =
    Enumerable((values ++ individualItemValues).distinct.map(v => v.toString -> v): _*)
}
