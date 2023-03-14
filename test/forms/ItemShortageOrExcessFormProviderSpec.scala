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

import forms.behaviours.BooleanFieldBehaviours
import models.ItemShortageOrExcessModel
import models.WrongWithMovement.{BrokenSeals, Less, More}
import play.api.data.FormError

import scala.collection.immutable.Map

class ItemShortageOrExcessFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "itemShortageOrExcess.error.required"
  val invalidKey = "error.boolean"

  val form = new ItemShortageOrExcessFormProvider()()

  "ItemShortageOrExcessFormProvider" - {

    "should bind successful values" - {

      "when all fields supplied (Less, 3dp for amount & Extra info)" in {

        val data = Map(
          s"shortageOrExcess" -> Less.toString,
          s"amount" -> "123456789012.123",
          s"additionalInfo" -> "info"
        )

        val result = form.bind(data)

        result.errors mustBe Seq()
        result.value mustBe Some(ItemShortageOrExcessModel(
          wrongWithItem = Less,
          amount = BigDecimal("123456789012.123"),
          additionalInfo = Some("info")
        ))
      }

      "when all fields supplied (More, 2dp for amount & No extra info)" in {

        val data = Map(
          s"shortageOrExcess" -> More.toString,
          s"amount" -> "1234567890123.12"
        )

        val result = form.bind(data)

        result.errors mustBe Seq()
        result.value mustBe Some(ItemShortageOrExcessModel(
          wrongWithItem = More,
          amount = BigDecimal("1234567890123.12"),
          additionalInfo = None
        ))
      }

      "when all fields supplied (1dp for amount, text area contains carriage returns)" in {

        val data = Map(
          s"shortageOrExcess" -> Less.toString,
          s"amount" -> "12345678901234.1"
        )

        val result = form.bind(data)

        result.errors mustBe Seq()
        result.value mustBe Some(ItemShortageOrExcessModel(
          wrongWithItem = Less,
          amount = BigDecimal("12345678901234.1"),
          additionalInfo = None
        ))
      }

      "when all fields supplied (0dp for amount, maximum text area input)" in {

        val data = Map(
          s"shortageOrExcess" -> Less.toString,
          s"amount" -> "123456789012345",
          s"additionalInfo" -> "\n\n\na\n\nb\nc\nd\n\n\n"
        )

        val result = form.bind(data)

        result.errors mustBe Seq()
        result.value mustBe Some(ItemShortageOrExcessModel(
          wrongWithItem = Less,
          amount = BigDecimal("123456789012345"),
          additionalInfo = Some("a b c d")
        ))
      }
    }

    "should error when binding unsuccessful values for amount" - {

      "when no amount is supplied" in {

        val data = Map(
          s"shortageOrExcess" -> Less.toString,
          s"additionalInfo" -> "info"
        )

        val result = form.bind(data)

        result.errors mustBe Seq(
          FormError("amount", "itemShortageOrExcess.amount.error.required", Seq())
        )
      }

      "when amount exceeds 3dp" in {

        val data = Map(
          s"shortageOrExcess" -> Less.toString,
          s"amount" -> "12345678901.1234",
          s"additionalInfo" -> "info"
        )

        val result = form.bind(data)

        result.errors mustBe Seq(
          FormError("amount", "itemShortageOrExcess.amount.error.regex", Seq(NUMERIC_15_3DP_REGEX))
        )
      }

      "when amount exceeds 15 numerics" in {

        val data = Map(
          s"shortageOrExcess" -> Less.toString,
          s"amount" -> "1234567890123456",
          s"additionalInfo" -> "info"
        )

        val result = form.bind(data)

        result.errors mustBe Seq(
          FormError("amount", "itemShortageOrExcess.amount.error.maxLength", Seq(MAX_LENGTH_15))
        )
      }

      "when amount is not a number" in {

        val data = Map(
          s"shortageOrExcess" -> Less.toString,
          s"amount" -> "abcd",
          s"additionalInfo" -> "info"
        )

        val result = form.bind(data)

        result.errors mustBe Seq(
          FormError("amount", "itemShortageOrExcess.amount.error.regex", Seq(NUMERIC_15_3DP_REGEX))
        )
      }
    }

    "should error when binding unsuccessful values for shortageOrExcess" - {

      "when no radio is selected" in {

        val data = Map(
          s"amount" -> "123456789012.123",
          s"additionalInfo" -> "info"
        )

        val result = form.bind(data)

        result.errors mustBe Seq(
          FormError("shortageOrExcess", "itemShortageOrExcess.shortageOrExcess.error.required", Seq())
        )
      }

      "when an invalid radio is selected" in {

        val data = Map(
          s"shortageOrExcess" -> BrokenSeals.toString,
          s"amount" -> "123456789012.123",
          s"additionalInfo" -> "info"
        )

        val result = form.bind(data)

        result.errors mustBe Seq(
          FormError("shortageOrExcess", "itemShortageOrExcess.shortageOrExcess.error.required", Seq())
        )
      }
    }

    "should error when binding unsuccessful values for additionalInfo" - {

      "exceeds 350 chars" in {

        val data = Map(
          s"shortageOrExcess" -> More.toString,
          s"amount" -> "123456789012.123",
          s"additionalInfo" -> "a" * (TEXTAREA_MAX_LENGTH + 1)
        )

        val result = form.bind(data)

        result.errors mustBe Seq(
          FormError("additionalInfo", "itemShortageOrExcess.additionalInfo.error.maxLength", Seq(TEXTAREA_MAX_LENGTH))
        )
      }

      "return an error if alpha numeric data isn't used" in {

        val data = Map(
          s"shortageOrExcess" -> More.toString,
          s"amount" -> "123456789012.123",
          s"additionalInfo" -> ".."
        )

        val result = form.bind(data)

        result.errors mustBe Seq(
          FormError("additionalInfo", "itemShortageOrExcess.additionalInfo.error.character",  Seq(ALPHANUMERIC_REGEX))
        )
      }

      "return errors if invalid characters are used" in {
        val data = Map(
          s"shortageOrExcess" -> More.toString,
          s"amount" -> "123456789012.123",
          s"additionalInfo" -> "<>"
        )
        val result = form.bind(data)

        result.errors must contain only(
          FormError("additionalInfo", "itemShortageOrExcess.additionalInfo.error.character",  Seq(ALPHANUMERIC_REGEX)),
          FormError("additionalInfo", "itemShortageOrExcess.additionalInfo.error.invalidCharacter",  Seq(XSS_REGEX))
        )
      }
    }
  }
}
