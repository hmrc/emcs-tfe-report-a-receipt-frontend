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

package generators

import models.WrongWithMovement.{Shortage, Excess}
import models._
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryHowMuchIsWrong: Arbitrary[HowMuchIsWrong] =
    Arbitrary {
      Gen.oneOf(HowMuchIsWrong.values.toSeq)
    }

  implicit lazy val arbitraryWrongWithMovement: Arbitrary[WrongWithMovement] =
    Arbitrary {
      Gen.oneOf(WrongWithMovement.values)
    }

  implicit lazy val arbitraryAcceptMovement: Arbitrary[AcceptMovement] =
    Arbitrary {
      Gen.oneOf(AcceptMovement.values)
    }

  implicit lazy val arbitraryAboutShortageExcess: Arbitrary[ItemShortageOrExcessModel] =
    Arbitrary {
      Gen.oneOf(Seq(
        ItemShortageOrExcessModel(
          Excess,
          BigDecimal(12),
          None
        ),
        ItemShortageOrExcessModel(
          Shortage,
          BigDecimal(12.123),
          Some("info")
        )
      ))
    }
}
