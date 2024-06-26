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

import fixtures.messages.RefusedAmountMessages
import forms.behaviours.IntFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class RefusedAmountFormProviderSpec extends IntFieldBehaviours with GuiceOneAppPerSuite {

  val itemQuantity: BigDecimal = 999999999999.998

  val fieldName = "value"
  val form = new RefusedAmountFormProvider()(itemQuantity, None)

  ".value" - {

    "must return required error when not supplied" in {

      val boundForm = form.bind(Map(fieldName -> ""))

      boundForm.errors mustBe Seq(
        FormError(fieldName, "refusedAmount.error.required")
      )
    }

    "must return the too large error when the amount exceeds the item quantity (and there's no shortage amount)" in {

      val boundForm = form.bind(Map(fieldName -> (itemQuantity + 0.001).toString))

      boundForm.errors mustBe Seq(
        FormError(fieldName, "refusedAmount.error.valueTooLarge", Seq(itemQuantity))
      )
    }

    "must return the too large error when the amount exceeds the item quantity (and there's a shortage amount taken into consideration)" in {

      val shortageAmount = 0.01
      val form = new RefusedAmountFormProvider()(itemQuantity, Some(shortageAmount))

      val boundForm = form.bind(Map(fieldName -> itemQuantity.toString))

      boundForm.errors mustBe Seq(
        FormError(fieldName, "refusedAmount.error.valueTooLarge", Seq(itemQuantity - shortageAmount))
      )
    }

    "must return the max length error when the amount exceeds maximum characters" in {

      val boundForm = form.bind(Map(fieldName -> "1234567890123456789"))

      boundForm.errors mustBe Seq(
        FormError(fieldName, "refusedAmount.error.maxLength", Seq(MAX_LENGTH_15))
      )
    }

    "must return non-Numeric error when the amount is not a number" in {

      val boundForm = form.bind(Map(fieldName -> "a"))

      boundForm.errors mustBe Seq(
        FormError(fieldName, "refusedAmount.error.isNotNumeric", Seq(NUMERIC_REGEX))
      )
    }

    "must return three decimal places error when the amount has more than three decimal places" in {

      val boundForm = form.bind(Map(fieldName -> "123456789.1234"))

      boundForm.errors mustBe Seq(
        FormError(fieldName, "refusedAmount.error.threeDecimalPlaces", Seq(NUMERIC_15_3DP_REGEX))
      )
    }

    "must return non zero value error when the amount is zero" in {

      val boundForm = form.bind(Map(fieldName -> "0"))

      boundForm.errors mustBe Seq(
        FormError(fieldName, "refusedAmount.error.notGreaterThanZero", Seq(MIN_VALUE_0.toString))
      )
    }

    "must accept a value equal to the quantity (no shortage)" in {

      val boundForm = form.bind(Map(fieldName -> itemQuantity.toString))

      boundForm.value mustBe Some(itemQuantity)
    }

    "must accept a value equal to the quantity minus shortage" in {

      val shortageAmount = 0.01
      val form = new RefusedAmountFormProvider()(itemQuantity, Some(shortageAmount))

      val boundForm = form.bind(Map(fieldName -> (itemQuantity - shortageAmount).toString))

      boundForm.value mustBe Some(itemQuantity - shortageAmount)
    }

    "must accept a value less than the item quantity" in {

      val boundForm = form.bind(Map(fieldName -> (itemQuantity - 0.001).toString))

      boundForm.value mustBe Some(itemQuantity - 0.001)
    }

    "must accept a value that is 0dp" in {

      val boundForm = form.bind(Map(fieldName -> "100"))

      boundForm.value mustBe Some(BigDecimal(100))
    }

    "must accept a value that is 1dp" in {

      val boundForm = form.bind(Map(fieldName -> "100.1"))

      boundForm.value mustBe Some(BigDecimal(100.1))
    }

    "must accept a value that is 2dp" in {

      val boundForm = form.bind(Map(fieldName -> "100.99"))

      boundForm.value mustBe Some(BigDecimal(100.99))
    }

    "must accept a value that is 3dp" in {

      val boundForm = form.bind(Map(fieldName -> "100.998"))

      boundForm.value mustBe Some(BigDecimal(100.998))
    }
  }

  "Error Messages" - {

    Seq(RefusedAmountMessages.English) foreach { messagesForLanguage =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message when no option is selected" in {
          messages(s"refusedAmount.error.required") mustBe messagesForLanguage.requiredError
        }

        "have the correct error message when value exceeds quantity" in {
          messages(s"refusedAmount.error.valueTooLarge", 123.123) mustBe messagesForLanguage.tooLarge(123.123)
        }

        "have the correct error message when value is not a numeric" in {
          messages(s"refusedAmount.error.isNotNumeric") mustBe messagesForLanguage.nonNumeric
        }

        "have the correct error message when value is zero" in {
          messages(s"refusedAmount.error.notGreaterThanZero", 0) mustBe messagesForLanguage.notGreaterThanZero
        }

        "have the correct error message when value has more than three decimal places" in {
          messages(s"refusedAmount.error.threeDecimalPlaces") mustBe messagesForLanguage.threeDecimalPlaces
        }

        "have the correct error message when value exceeds the max length" in {
          messages(s"refusedAmount.error.maxLength", MAX_LENGTH_15) mustBe messagesForLanguage.maxLength(MAX_LENGTH_15)
        }
      }
    }
  }
}
