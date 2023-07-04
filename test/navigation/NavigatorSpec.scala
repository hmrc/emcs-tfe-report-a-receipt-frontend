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

package navigation

import base.SpecBase
import controllers.routes
import models.AcceptMovement._
import models.HowGiveInformation.{IndividualItem, TheWholeMovement}
import models.WrongWithMovement._
import models._
import pages._
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn, testArc)
      }

      "for the DateOfArrival page" - {

        "must go to AcceptMovement page" in {
          navigator.nextPage(DateOfArrivalPage, NormalMode, emptyUserAnswers) mustBe
            routes.AcceptMovementController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the AcceptMovementPage page" - {

        s"when the user answers is $Satisfactory" - {

          "must go to the AddMoreInformation page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Satisfactory)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is $Unsatisfactory" - {

          "must go to the SelectItems page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Unsatisfactory)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe
              routes.SelectItemsController.onPageLoad(testErn, testArc)
          }
        }

        s"when the user answers is $Refused" - {

          "must go to the HowGiveInformationWithMovement page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Refused)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe
              routes.HowGiveInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is $PartiallyRefused" - {

          "must go to the SelectItems page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, PartiallyRefused)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe
              routes.SelectItemsController.onPageLoad(testErn, testArc)
          }
        }
      }

      "for the HowGiveInformation page" - {

        s"when the user answers is $TheWholeMovement" - {

          "must go to the WrongWithMovement page" in {

            val userAnswers = emptyUserAnswers.set(HowGiveInformationPage, TheWholeMovement)

            navigator.nextPage(HowGiveInformationPage, NormalMode, userAnswers) mustBe
              routes.WrongWithMovementController.loadWrongWithMovement(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is $IndividualItem" - {

          "must go to the Item Selection List page" in {

            val userAnswers = emptyUserAnswers.set(HowGiveInformationPage, IndividualItem)

            navigator.nextPage(HowGiveInformationPage, NormalMode, userAnswers) mustBe
              routes.SelectItemsController.onPageLoad(testErn, testArc)
          }
        }

        s"when there is no answer for the question" - {

          "must go to back to the HowGiveInformation page" in {
            navigator.nextPage(HowGiveInformationPage, NormalMode, emptyUserAnswers) mustBe
              routes.HowGiveInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }
      }

      "for the SelectItems page" - {

        "when the movement is PartiallyRefused" - {

          "must go to the AreYouRefusingAnyAmountOfItem page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, PartiallyRefused)

            navigator.nextPage(SelectItemsPage(3), NormalMode, userAnswers) mustBe
              routes.RefusingAnyAmountOfItemController.onPageLoad(testErn, testArc, 3, NormalMode)
          }
        }

        "when the movement is not PartiallyRefused" - {

          "must go to the WrongWithItem page at the specified index" in {

            navigator.nextPage(SelectItemsPage(3), NormalMode, emptyUserAnswers) mustBe
              routes.WrongWithItemController.loadWrongWithItem(testErn, testArc, 3, NormalMode)
          }
        }
      }

      "for the RefusingAnyAmountOfItem page" - {

        "when the answer is Yes" - {

          "must go to the Amount being refused page" in {

            val userAnswers = emptyUserAnswers.set(RefusingAnyAmountOfItemPage(1), true)

            navigator.nextPage(RefusingAnyAmountOfItemPage(1), NormalMode, userAnswers) mustBe
              routes.RefusedAmountController.onPageLoad(testErn, testArc, 1, NormalMode)
          }
        }

        "when the answer is No" - {

          "must go to the WrongWithItem page" in {

            val userAnswers = emptyUserAnswers.set(RefusingAnyAmountOfItemPage(1), false)

            navigator.nextPage(RefusingAnyAmountOfItemPage(1), NormalMode, userAnswers) mustBe
              routes.WrongWithItemController.loadWrongWithItem(testErn, testArc, 1, NormalMode)
          }
        }
      }

      "for the RefusedAmount page" - {

        "must go to the WrongWithItem page" in {

          navigator.nextPage(RefusedAmountPage(1), NormalMode, emptyUserAnswers) mustBe
            routes.WrongWithItemController.loadWrongWithItem(testErn, testArc, 1, NormalMode)
        }
      }

      "for the WrongWithMovement page" - {

        "when no option has been selected (shouldn't happen)" - {

          "must go back to the WrongWithMovement page" in {
            navigator.nextPage(WrongWithMovementPage, NormalMode, emptyUserAnswers) mustBe
              routes.WrongWithMovementController.loadWrongWithMovement(testErn, testArc, NormalMode)
          }
        }

        "when the next page is Less" - {

          "must go to ShortageInformation Yes/No page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Shortage, Excess, Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, selectedOptions)
            navigator.nextPage(WrongWithMovementPage, NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadShortageInformation(testErn, testArc, NormalMode)
          }
        }

        "when the next page is More" - {

          "must go to ExcessInformation Yes/No page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Excess, Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, selectedOptions)
            navigator.nextPage(WrongWithMovementPage, NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadExcessInformation(testErn, testArc, NormalMode)
          }
        }

        "when the next page is Damaged" - {

          "must go to DamagedInformation Yes/No page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, selectedOptions)
            navigator.nextPage(WrongWithMovementPage, NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadDamageInformation(testErn, testArc, NormalMode)
          }
        }

        "when the next page is BrokenSeals" - {

          "must go to BrokenSealsInformation Yes/No page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, selectedOptions)
            navigator.nextPage(WrongWithMovementPage, NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadSealsInformation(testErn, testArc, NormalMode)
          }
        }

        "when the next page is Other" - {

          "must go to Other information page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Other)
            val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, selectedOptions)
            navigator.nextPage(WrongWithMovementPage, NormalMode, userAnswers) mustBe
              routes.OtherInformationController.loadOtherInformation(testErn, testArc, NormalMode)
          }
        }
      }

      "for the wrongWithItem page" - {

        "when no option has been selected (shouldn't happen)" - {

          "must go back to the wrongWithItem page" in {
            navigator.nextPage(WrongWithItemPage(1), NormalMode, emptyUserAnswers) mustBe
              routes.WrongWithItemController.loadWrongWithItem(testErn, testArc, idx = 1, mode = NormalMode)
          }
        }

        "when the next page is MoreOrLess" - {

          "must go to ItemShortageOrExcess page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(ShortageOrExcess, Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)
            navigator.nextPage(WrongWithItemPage(1), NormalMode, userAnswers) mustBe
              routes.ItemShortageOrExcessController.onPageLoad(testErn, testArc, 1, NormalMode)
          }
        }

        "when the next page is Shortage" - {

          //TODO: Update as part of future story, for now routes to combined page
          "must go to ItemShortagePage" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Shortage, Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)
            navigator.nextPage(WrongWithItemPage(1), NormalMode, userAnswers) mustBe
              routes.ItemShortageOrExcessController.onPageLoad(testErn, testArc, 1, NormalMode)
          }
        }

        "when the next page is Excess" - {

          //TODO: Update as part of future story, for now routes to combined page
          "must go to ItemExcessPage" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Excess, Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)
            navigator.nextPage(WrongWithItemPage(1), NormalMode, userAnswers) mustBe
              routes.ItemShortageOrExcessController.onPageLoad(testErn, testArc, 1, NormalMode)
          }
        }

        "when the next page is Damaged" - {

          "must go to ItemDamagedInformation add more info Yes/No page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)
            navigator.nextPage(WrongWithItemPage(1), NormalMode, userAnswers) mustBe
              routes.AddItemMoreInformationController.loadItemDamageInformation(testErn, testArc, 1, NormalMode)
          }
        }

        "for the ItemDamagedInformation page" - {

          "must go to the next WhatWrongWith page to answer" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)

            navigator.nextPage(ItemDamageInformationPage(1), NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadItemSealsInformation(testErn, testArc, 1, NormalMode)
          }
        }

        "when the next page is BrokenSeals" - {

          "must go to ItemBrokenSealsInformation add more info Yes/No page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)
            navigator.nextPage(WrongWithItemPage(1), NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadItemSealsInformation(userAnswers.ern, userAnswers.arc, 1, NormalMode)
          }
        }

        "when the next page is Other" - {

          "must go to ItemOther information page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)
            navigator.nextPage(WrongWithItemPage(1), NormalMode, userAnswers) mustBe
              routes.OtherInformationController.loadItemOtherInformation(testErn, testArc, idx = 1, mode = NormalMode)
          }
        }
      }

      "for the AddShortageInformation page" - {

        s"when the user answers is Yes" - {

          "must go to the ShortageMoreInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddShortageInformationPage, true)

            navigator.nextPage(AddShortageInformationPage, NormalMode, userAnswers) mustBe
              routes.MoreInformationController.loadShortageInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the next WhatWrongWith page to answer" in {

            val userAnswers = emptyUserAnswers
              .set(WrongWithMovementPage, Set[WrongWithMovement](Shortage))
              .set(AddShortageInformationPage, false)

            navigator.nextPage(AddShortageInformationPage, NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is None (shouldn't be possible)" - {

          "must go back to itself" in {
            navigator.nextPage(AddShortageInformationPage, NormalMode, emptyUserAnswers) mustBe
              routes.AddMoreInformationController.loadShortageInformation(testErn, testArc, NormalMode)
          }
        }
      }

      "for the ShortageInformation page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Shortage))

          navigator.nextPage(ShortageInformationPage, NormalMode, userAnswers) mustBe
            routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
        }
      }

      "for the ItemShortageOrExcess page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), Set[WrongWithMovement](ShortageOrExcess, Damaged, BrokenSeals, Other))

          navigator.nextPage(ItemShortageOrExcessPage(1), NormalMode, userAnswers) mustBe
            routes.AddItemMoreInformationController.loadItemDamageInformation(testErn, testArc, 1, NormalMode)
        }
      }

      "for the AddExcessInformation page" - {

        s"when the user answers is Yes" - {

          "must go to the ExcessInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddExcessInformationPage, true)

            navigator.nextPage(AddExcessInformationPage, NormalMode, userAnswers) mustBe
              routes.MoreInformationController.loadExcessInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the next WhatWrongWith page to answer" in {

            val userAnswers = emptyUserAnswers
              .set(WrongWithMovementPage, Set[WrongWithMovement](Shortage, Excess))
              .set(AddExcessInformationPage, false)

            navigator.nextPage(AddExcessInformationPage, NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is None (shouldn't be possible)" - {

          "must go back to itself" in {
            navigator.nextPage(AddExcessInformationPage, NormalMode, emptyUserAnswers) mustBe
              routes.AddMoreInformationController.loadExcessInformation(testErn, testArc, NormalMode)
          }
        }
      }

      "for the ExcessInformation page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Shortage, Excess))

          navigator.nextPage(ExcessInformationPage, NormalMode, userAnswers) mustBe
            routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
        }
      }

      "for the AddDamageInformation page" - {

        s"when the user answers is Yes" - {

          "must go to the DamageInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddDamageInformationPage, true)

            navigator.nextPage(AddDamageInformationPage, NormalMode, userAnswers) mustBe
              routes.MoreInformationController.loadDamageInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the next WhatWrongWith page to answer" in {

            val userAnswers = emptyUserAnswers
              .set(WrongWithMovementPage, Set[WrongWithMovement](Damaged, BrokenSeals))
              .set(AddDamageInformationPage, false)

            navigator.nextPage(AddDamageInformationPage, NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadSealsInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is None (shouldn't be possible)" - {

          "must go back to itself" in {
            navigator.nextPage(AddDamageInformationPage, NormalMode, emptyUserAnswers) mustBe
              routes.AddMoreInformationController.loadDamageInformation(testErn, testArc, NormalMode)
          }
        }
      }

      "for the ChooseGiveReasonItemDamaged page" - {

        s"when the user answers is Yes" - {
          "must go to the DamageInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddItemDamageInformationPage(1), true)

            navigator.nextPage(AddItemDamageInformationPage(1), NormalMode, userAnswers) mustBe
              routes.ItemMoreInformationController.loadItemDamageInformation(testErn, testArc, 1, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the next WhatWrongWith page to answer" in {

            val userAnswers = emptyUserAnswers
              .set(WrongWithItemPage(1), Set[WrongWithMovement](Damaged, BrokenSeals))
              .set(AddItemDamageInformationPage(1), false)

            navigator.nextPage(AddItemDamageInformationPage(1), NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadItemSealsInformation(testErn, testArc, 1, NormalMode)
          }
        }

        s"when the user answers is None (shouldn't be possible)" - {

          "must go back to itself" in {
            navigator.nextPage(AddItemDamageInformationPage(1), NormalMode, emptyUserAnswers) mustBe
              routes.AddItemMoreInformationController.loadItemDamageInformation(testErn, testArc, idx = 1, NormalMode)
          }
        }
      }

      "for the DamageInformationPage page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Damaged, BrokenSeals))

          navigator.nextPage(DamageInformationPage, NormalMode, userAnswers) mustBe
            routes.AddMoreInformationController.loadSealsInformation(testErn, testArc, NormalMode)
        }
      }

      "for the AddSealsInformationPage page" - {

        s"when the user answers is Yes" - {

          "must go to the DamageInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddSealsInformationPage, true)

            navigator.nextPage(AddSealsInformationPage, NormalMode, userAnswers) mustBe
              routes.MoreInformationController.loadSealsInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the next WhatWrongWith page to answer" in {

            val userAnswers = emptyUserAnswers
              .set(WrongWithMovementPage, Set[WrongWithMovement](Shortage, Excess, BrokenSeals))
              .set(AddSealsInformationPage, false)

            navigator.nextPage(AddSealsInformationPage, NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is None (shouldn't be possible)" - {

          "must go back to itself" in {
            navigator.nextPage(AddSealsInformationPage, NormalMode, emptyUserAnswers) mustBe
              routes.AddMoreInformationController.loadSealsInformation(testErn, testArc, NormalMode)
          }
        }
      }

      "for the AddItemSealsInformationPage page" - {

        s"when the user answers is Yes" - {

          "must go to the DamageInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddItemSealsInformationPage(1), true)

            navigator.nextPage(AddItemSealsInformationPage(1), NormalMode, userAnswers) mustBe
              routes.MoreInformationController.loadItemSealsInformation(testErn, testArc, 1, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the next WhatWrongWith page to answer" in {

            val userAnswers = emptyUserAnswers
              .set(WrongWithMovementPage, Set[WrongWithMovement](Shortage, Excess, BrokenSeals))
              .set(AddItemSealsInformationPage(1), false)

            navigator.nextPage(AddItemSealsInformationPage(1), NormalMode, userAnswers) mustBe
              routes.WrongWithItemController.loadWrongWithItem(userAnswers.ern, userAnswers.arc, 1, NormalMode)
          }
        }

        s"when the user answers is None (shouldn't be possible)" - {

          "must go back to itself" in {
            navigator.nextPage(AddItemSealsInformationPage(1), NormalMode, emptyUserAnswers) mustBe
              routes.AddMoreInformationController.loadItemSealsInformation(testErn, testArc, 1, NormalMode)
          }
        }
      }

      "for the SealsInformationPage page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Shortage, Excess, BrokenSeals))

          navigator.nextPage(SealsInformationPage, NormalMode, userAnswers) mustBe
            routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
        }
      }

      "for the ItemSealsInformationPage page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), Set[WrongWithMovement](BrokenSeals, Other))

          navigator.nextPage(ItemSealsInformationPage(1), NormalMode, userAnswers) mustBe
            routes.OtherInformationController.loadItemOtherInformation(testErn, testArc, 1, NormalMode)
        }
      }

      "for the OtherInformationPage page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Shortage, Excess, Other))

          navigator.nextPage(OtherInformationPage, NormalMode, userAnswers) mustBe
            routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
        }
      }

      "for the ItemOtherInformationPage page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), Set[WrongWithMovement](Other))

          navigator.nextPage(ItemOtherInformationPage(1), NormalMode, userAnswers) mustBe
            routes.CheckYourAnswersItemController.onPageLoad(testErn, testArc, 1)
        }
      }

      "for the AddMoreInformation page" - {

        s"when the user answers is Yes" - {

          "must go to the MoreInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddMoreInformationPage, true)

            navigator.nextPage(AddMoreInformationPage, NormalMode, userAnswers) mustBe
              routes.MoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the CheckYourAnswers page" in {

            val userAnswers = emptyUserAnswers.set(AddMoreInformationPage, false)

            navigator.nextPage(AddMoreInformationPage, NormalMode, userAnswers) mustBe
              routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }
      }

      "for the MoreInformation page" - {

        "must go to the CheckYourAnswers page" in {

          navigator.nextPage(MoreInformationPage, NormalMode, emptyUserAnswers) mustBe
            routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }
      }

      "for the CheckYourAnswers page" - {

        "must go to the Confirmation page" in {

          navigator.nextPage(CheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            routes.ConfirmationController.onPageLoad(testErn, testArc)
        }
      }

      "for the CheckYourAnswers item page" - {

        "must go to the AddedItemsList page" in {

          navigator.nextPage(CheckAnswersItemPage(1), NormalMode, emptyUserAnswers) mustBe
            routes.AddedItemsController.onPageLoad(testErn, testArc)
        }
      }

      "for the AddedItemsList page" - {

        "must go to the AddMoreInformation page" in {

          navigator.nextPage(AddedItemsPage, NormalMode, emptyUserAnswers) mustBe
            routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
        }
      }
    }

    "in Check mode" - {

      "must go to the AddToList page" - {
        Seq(ItemShortageOrExcessPage(1), ItemSealsInformationPage(1), ItemDamageInformationPage(1), ItemOtherInformationPage(1)).foreach {
          page: QuestionPage[_] =>
            s"when the previous page is $page" in {
              navigator.nextPage(page, CheckMode, emptyUserAnswers) mustBe
                routes.AddedItemsController.onPageLoad(testErn, testArc)
            }
        }
      }

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
      }
    }

    "in review mode" - {
      "must go to the CheckYourAnswers page" - {
        Seq(ItemShortageOrExcessPage(1), ItemSealsInformationPage(1), ItemDamageInformationPage(1), ItemOtherInformationPage(1)).foreach {
          page: QuestionPage[_] =>
            s"when the previous page is $page" in {
              navigator.nextPage(page, ReviewMode, emptyUserAnswers) mustBe
                routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
            }
        }
      }

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
      }
    }

    "calling the .nextWrongWithMovementOptionToAnswer method" - {

      "when all options are selected" - {

        val setOfOptions: Set[WrongWithMovement] = Set(Damaged, Other, Shortage, Excess, BrokenSeals)

        "when user hasn't answered any reasons yet" - {

          "must return the first one to answer based on the order radio items" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions) mustBe Some(Shortage)
          }
        }

        "when user has answered some of the reasons" - {

          "must return the next one to answer" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions, Some(Damaged)) mustBe Some(BrokenSeals)
          }
        }

        "when user has answered the last reason" - {

          "must return None" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions, Some(Other)) mustBe None
          }
        }
      }

      "when some of the options are selected" - {

        val setOfOptions: Set[WrongWithMovement] = Set(Other, Excess)

        "when user hasn't answered any reasons yet" - {

          "must return the first one to answer based on the order radio items" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions) mustBe Some(Excess)
          }
        }

        "when user has answered some of the reasons" - {

          "must return the next one to answer" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions, Some(Excess)) mustBe Some(Other)
          }
        }

        "when user has answered the last reason" - {

          "must return None" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions, Some(Other)) mustBe None
          }
        }
      }

      "when one of the options are selected" - {

        val setOfOptions: Set[WrongWithMovement] = Set(Excess)

        "when user hasn't answered any reasons yet" - {

          "must return the first one to answer based on the order radio items" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions) mustBe Some(Excess)
          }
        }

        "when user has answered the last reason" - {

          "must return None" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions, Some(Excess)) mustBe None
          }
        }
      }

      "when none the options are selected" - {

        val setOfOptions: Set[WrongWithMovement] = Set()

        "must return None" in {
          navigator.nextWrongWithMovementOptionToAnswer(setOfOptions) mustBe None
        }
      }
    }
  }
}
