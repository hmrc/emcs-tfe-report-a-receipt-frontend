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

import org.scalacheck.Arbitrary
import pages._
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, ItemDamageInformationPage, ItemShortageOrExcessPage, RefusedAmountPage, RefusingAnyAmountOfItemPage}
import pages.unsatisfactory.{HowMuchIsWrongPage, WrongWithMovementPage}

trait PageGenerators {

  implicit lazy val arbitraryContinueDraftPage: Arbitrary[ContinueDraftPage.type] =
    Arbitrary(ContinueDraftPage)

  implicit lazy val arbitraryRefusedAmountPage: Arbitrary[RefusedAmountPage] =
    Arbitrary(RefusedAmountPage(1))

  implicit lazy val arbitraryRefusingAnyAmountOfItemPage: Arbitrary[RefusingAnyAmountOfItemPage] =
    Arbitrary(RefusingAnyAmountOfItemPage(1))

  implicit lazy val arbitraryItemDamageInformationPage: Arbitrary[ItemDamageInformationPage] =
    Arbitrary(ItemDamageInformationPage(1))

  implicit lazy val arbitraryItemShortageOrExcessPage: Arbitrary[ItemShortageOrExcessPage] =
    Arbitrary(ItemShortageOrExcessPage(1))

  implicit lazy val arbitraryChooseGiveReasonItemDamagedPage: Arbitrary[AddItemDamageInformationPage] =
    Arbitrary(AddItemDamageInformationPage(1))

  implicit lazy val arbitraryHowMuchIsWrongPage: Arbitrary[HowMuchIsWrongPage.type] =
    Arbitrary(HowMuchIsWrongPage)

  implicit lazy val arbitraryWrongWithMovementPage: Arbitrary[WrongWithMovementPage.type] =
    Arbitrary(WrongWithMovementPage)

  implicit lazy val arbitraryMoreInformationPage: Arbitrary[MoreInformationPage.type] =
    Arbitrary(MoreInformationPage)

  implicit lazy val arbitraryAddMoreInformationPage: Arbitrary[AddMoreInformationPage.type] =
    Arbitrary(AddMoreInformationPage)

  implicit lazy val arbitraryAcceptMovementPage: Arbitrary[AcceptMovementPage.type] =
    Arbitrary(AcceptMovementPage)

  implicit lazy val arbitraryDateOfArrivalPage: Arbitrary[DateOfArrivalPage.type] =
    Arbitrary(DateOfArrivalPage)
}
