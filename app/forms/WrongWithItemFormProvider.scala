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

package forms

import forms.mappings.Mappings
import models.WrongWithMovement
import models.WrongWithMovement.{Excess, Shortage}
import pages.QuestionPage
import play.api.data.Form
import play.api.data.Forms.set
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject

class WrongWithItemFormProvider @Inject() extends Mappings {

  def apply(): Form[Set[WrongWithMovement]] =
    Form(
      "value" -> set(enumerable[WrongWithMovement](s"wrongWithItem.error.required"))
        .verifying(nonEmptySet(s"wrongWithItem.error.required"))
        .verifying(onlyShortageOrExcess())
    )

  private def onlyShortageOrExcess(): Constraint[Set[WrongWithMovement]] =
    Constraint {
      case set if set.contains(Shortage) && set.contains(Excess) =>
        Invalid("wrongWithItem.error.shortageOrExcessOnly")
      case _ =>
        Valid
    }
}
