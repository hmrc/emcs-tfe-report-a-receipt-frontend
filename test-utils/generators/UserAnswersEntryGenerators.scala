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

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, ItemDamageInformationPage, ItemShortageOrExcessPage, RefusingAnyAmountOfItemPage}
import pages.unsatisfactory.{HowMuchIsWrongPage, WrongWithMovementPage}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryRefusingAnyAmountOfItemUserAnswersEntry: Arbitrary[(RefusingAnyAmountOfItemPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RefusingAnyAmountOfItemPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryItemDamageInformationUserAnswersEntry: Arbitrary[(ItemDamageInformationPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ItemDamageInformationPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryItemShortageOrExcessUserAnswersEntry: Arbitrary[(ItemShortageOrExcessPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ItemShortageOrExcessPage]
        value <- arbitrary[ItemShortageOrExcessModel].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryChooseGiveReasonItemDamagedUserAnswersEntry: Arbitrary[(AddItemDamageInformationPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddItemDamageInformationPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowMuchIsWrongUserAnswersEntry: Arbitrary[(HowMuchIsWrongPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowMuchIsWrongPage.type]
        value <- arbitrary[HowMuchIsWrong].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWrongWithMovementUserAnswersEntry: Arbitrary[(WrongWithMovementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WrongWithMovementPage.type]
        value <- arbitrary[WrongWithMovement].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryMoreInformationUserAnswersEntry: Arbitrary[(MoreInformationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MoreInformationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddMoreInformationUserAnswersEntry: Arbitrary[(AddMoreInformationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddMoreInformationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAcceptMovementUserAnswersEntry: Arbitrary[(AcceptMovementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AcceptMovementPage.type]
        value <- arbitrary[AcceptMovement].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDateOfArrivalUserAnswersEntry: Arbitrary[(DateOfArrivalPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DateOfArrivalPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }
}
