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

import pages.QuestionPage
import pages.unsatisfactory.WrongWithMovementPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import viewmodels.govuk.checkbox._

sealed trait WrongWithMovement

object WrongWithMovement extends Enumerable.Implicits {

  case object Less extends WithName("less") with WrongWithMovement
  case object More extends WithName("more") with WrongWithMovement
  case object MoreOrLess extends WithName("moreOrLess") with WrongWithMovement
  case object Damaged extends WithName("damaged") with WrongWithMovement
  case object BrokenSeals extends WithName("brokenSeals") with WrongWithMovement
  case object Other extends WithName("other") with WrongWithMovement

  val values: Seq[WrongWithMovement] = Seq(
    Less,
    More,
    Damaged,
    BrokenSeals,
    Other
  )

  val individualItemValues: Seq[WrongWithMovement] = Seq(
    MoreOrLess,
    Damaged,
    BrokenSeals,
    Other
  )

  def checkboxItems(page: QuestionPage[Set[WrongWithMovement]])(implicit messages: Messages): Seq[CheckboxItem] = {
    val checkboxes = page match {
      case WrongWithMovementPage => values
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

  def itemShortageOrExcessOptions(implicit messages: Messages) = {
    Seq(
      RadioItem(
        content = Text(messages(s"itemShortageOrExcess.shortageOrExcess.$Less")),
        value = Some(Less.toString),
        id = Some(Less.toString)
      ),
      RadioItem(
        content = Text(messages(s"itemShortageOrExcess.shortageOrExcess.$More")),
        value = Some(More.toString),
        id = Some(More.toString)
      )
    )
  }

  implicit val enumerable: Enumerable[WrongWithMovement] =
    Enumerable((values ++ individualItemValues).distinct.map(v => v.toString -> v): _*)
}
