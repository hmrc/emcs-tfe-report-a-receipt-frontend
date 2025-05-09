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

import java.time.LocalDate
import forms.mappings.Mappings

import javax.inject.Inject
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.Messages
import utils.{DateUtils, TimeMachine}

class DateOfArrivalFormProvider @Inject()(timeMachine: TimeMachine) extends Mappings with DateUtils {

  def apply(dateOfDispatch: LocalDate)(implicit messages: Messages): Form[LocalDate] =
    Form(
      "value" -> localDate(
        notARealDateKey = "dateOfArrival.error.invalid",
        allRequiredKey = "dateOfArrival.error.required.all",
        twoRequiredKey = "dateOfArrival.error.required.two",
        oneRequiredKey = "dateOfArrival.error.required",
        oneInvalidKey = "dateOfArrival.error.invalid.one"
      )
        .verifying(notInFuture("dateOfArrival.error.notInFuture"))
        .verifying(notBeforeDateOfDispatch(dateOfDispatch, "dateOfArrival.error.notBeforeDateOfDispatch"))
    )

  def notBeforeDateOfDispatch(dateOfDispatch: LocalDate, errorKey: String)
                             (implicit messages: Messages): Constraint[LocalDate] = Constraint { date =>
    if (!date.isBefore(dateOfDispatch)) Valid else Invalid(errorKey, dateOfDispatch.formatDateForUIOutput())
  }

  def notInFuture(errorKey: String): Constraint[LocalDate] = Constraint { date =>
    if (!date.isAfter(timeMachine.now().toLocalDate)) Valid else Invalid(errorKey)
  }
}
