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
import models.HowMuchIsWrong.{IndividualItem, TheWholeMovement}
import models.WrongWithMovement._
import models._
import pages._
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems.{SelectItemsPage, WrongWithItemPage}

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe routes.IndexController.onPageLoad(testErn, testArc)
      }

      "for the DateOfArrival page" - {

        "must go to AcceptMovement page" in {
          navigator.nextPage(DateOfArrivalPage, NormalMode, emptyUserAnswers) mustBe routes.AcceptMovementController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the AcceptMovementPage page" - {

        s"when the user answers is $Satisfactory" - {

          "must go to the AddMoreInformation page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Satisfactory)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is $Unsatisfactory" - {

          "must go to the How Much Is Wrong page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Unsatisfactory)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe routes.HowMuchIsWrongController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is $Refused" - {

          //TODO: Update this as part of future stories to go through the actual flow
          "must go to the CheckAnswersPage page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Refused)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }

        s"when the user answers is $PartiallyRefused" - {

          //TODO: Update this as part of future stories to go through the actual flow
          "must go to the CheckAnswersPage page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, PartiallyRefused)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }
      }

      "for the HowMuchIsWrong page" - {

        s"when the user answers is $TheWholeMovement" - {

          "must go to the WrongWithMovement page" in {

            val userAnswers = emptyUserAnswers.set(HowMuchIsWrongPage, TheWholeMovement)

            navigator.nextPage(HowMuchIsWrongPage, NormalMode, userAnswers) mustBe routes.WrongWithMovementController.loadWrongWithMovement(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is $IndividualItem" - {

          "must go to the Item Selection List page" in {

            val userAnswers = emptyUserAnswers.set(HowMuchIsWrongPage, IndividualItem)

            navigator.nextPage(HowMuchIsWrongPage, NormalMode, userAnswers) mustBe routes.SelectItemsController.onPageLoad(testErn, testArc)
          }
        }

        s"when there is no answer for the question" - {

          "must go to back to the HowMuchIsWrong page" in {
            navigator.nextPage(HowMuchIsWrongPage, NormalMode, emptyUserAnswers) mustBe routes.HowMuchIsWrongController.onPageLoad(testErn, testArc, NormalMode)
          }
        }
      }

      "for the SelectItems page" - {

        "must go to the WrongWithItem page at the specified index" in {

          navigator.nextPage(SelectItemsPage(3), NormalMode, emptyUserAnswers) mustBe
            routes.WrongWithMovementController.loadwrongWithItem(testErn, testArc, 3, NormalMode)
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
            val selectedOptions: Set[WrongWithMovement] = Set(Less, More, Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, selectedOptions)
            navigator.nextPage(WrongWithMovementPage, NormalMode, userAnswers) mustBe
              routes.AddMoreInformationController.loadShortageInformation(testErn, testArc, NormalMode)
          }
        }

        "when the next page is More" - {

          "must go to ExcessInformation Yes/No page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(More, Damaged, BrokenSeals, Other)
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
              routes.OtherInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }
      }

      "for the wrongWithItem page" - {

        "when no option has been selected (shouldn't happen)" - {

          "must go back to the wrongWithItem page" in {
            navigator.nextPage(WrongWithItemPage(1), NormalMode, emptyUserAnswers) mustBe
              routes.WrongWithMovementController.loadwrongWithItem(testErn, testArc, idx = 1, mode = NormalMode)
          }
        }

        "when the next page is MoreOrLess" - {

          "must go to ItemShortageOrExcess page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(MoreOrLess, Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)
            navigator.nextPage(WrongWithItemPage(1), NormalMode, userAnswers) mustBe
              routes.ItemShortageOrExcessController.onPageLoad(testErn, testArc, 1, NormalMode)
          }
        }

        "when the next page is Damaged" - {

          //TODO: Implement as part of future story when page is built
          "must go to ItemDamagedInformation add more info Yes/No page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Damaged, BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)
            navigator.nextPage(WrongWithItemPage(1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "when the next page is BrokenSeals" - {

          //TODO: Implement as part of future story when page is built
          "must go to ItemBrokenSealsInformation add more info Yes/No page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(BrokenSeals, Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)
            navigator.nextPage(WrongWithItemPage(1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "when the next page is Other" - {

          //TODO: Implement as part of future story when page is built
          "must go to ItemOther information page" in {
            val selectedOptions: Set[WrongWithMovement] = Set(Other)
            val userAnswers = emptyUserAnswers.set(WrongWithItemPage(1), selectedOptions)
            navigator.nextPage(WrongWithItemPage(1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }
      }

      "for the AddShortageInformation page" - {

        s"when the user answers is Yes" - {

          "must go to the ShortageMoreInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddShortageInformationPage, true)

            navigator.nextPage(AddShortageInformationPage, NormalMode, userAnswers) mustBe routes.MoreInformationController.loadShortageInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the next WhatWrongWith page to answer" in {

            val userAnswers = emptyUserAnswers
              .set(WrongWithMovementPage, Set[WrongWithMovement](Less))
              .set(AddShortageInformationPage, false)

            navigator.nextPage(AddShortageInformationPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is None (shouldn't be possible)" - {

          "must go back to itself" in {
            navigator.nextPage(AddShortageInformationPage, NormalMode, emptyUserAnswers) mustBe routes.AddMoreInformationController.loadShortageInformation(testErn, testArc, NormalMode)
          }
        }
      }

      "for the ShortageInformation page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Less))

          navigator.nextPage(ShortageInformationPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
        }
      }

      "for the AddExcessInformation page" - {

        s"when the user answers is Yes" - {

          "must go to the ExcessInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddExcessInformationPage, true)

            navigator.nextPage(AddExcessInformationPage, NormalMode, userAnswers) mustBe routes.MoreInformationController.loadExcessInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the next WhatWrongWith page to answer" in {

            val userAnswers = emptyUserAnswers
              .set(WrongWithMovementPage, Set[WrongWithMovement](Less, More))
              .set(AddExcessInformationPage, false)

            navigator.nextPage(AddExcessInformationPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is None (shouldn't be possible)" - {

          "must go back to itself" in {
            navigator.nextPage(AddExcessInformationPage, NormalMode, emptyUserAnswers) mustBe routes.AddMoreInformationController.loadExcessInformation(testErn, testArc, NormalMode)
          }
        }
      }

      "for the ExcessInformation page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Less, More))

          navigator.nextPage(ExcessInformationPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
        }
      }

      "for the AddDamageInformation page" - {

        s"when the user answers is Yes" - {

          "must go to the DamageInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddDamageInformationPage, true)

            navigator.nextPage(AddDamageInformationPage, NormalMode, userAnswers) mustBe routes.MoreInformationController.loadDamageInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the next WhatWrongWith page to answer" in {

            val userAnswers = emptyUserAnswers
              .set(WrongWithMovementPage, Set[WrongWithMovement](Damaged, BrokenSeals))
              .set(AddDamageInformationPage, false)

            navigator.nextPage(AddDamageInformationPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.loadSealsInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is None (shouldn't be possible)" - {

          "must go back to itself" in {
            navigator.nextPage(AddDamageInformationPage, NormalMode, emptyUserAnswers) mustBe routes.AddMoreInformationController.loadDamageInformation(testErn, testArc, NormalMode)
          }
        }
      }

      "for the DamageInformationPage page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Damaged, BrokenSeals))

          navigator.nextPage(DamageInformationPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.loadSealsInformation(testErn, testArc, NormalMode)
        }
      }

      "for the AddSealsInformationPage page" - {

        s"when the user answers is Yes" - {

          "must go to the DamageInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddSealsInformationPage, true)

            navigator.nextPage(AddSealsInformationPage, NormalMode, userAnswers) mustBe routes.MoreInformationController.loadSealsInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the next WhatWrongWith page to answer" in {

            val userAnswers = emptyUserAnswers
              .set(WrongWithMovementPage, Set[WrongWithMovement](Less, More, BrokenSeals))
              .set(AddSealsInformationPage, false)

            navigator.nextPage(AddSealsInformationPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is None (shouldn't be possible)" - {

          "must go back to itself" in {
            navigator.nextPage(AddSealsInformationPage, NormalMode, emptyUserAnswers) mustBe routes.AddMoreInformationController.loadSealsInformation(testErn, testArc, NormalMode)
          }
        }
      }

      "for the SealsInformationPage page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Less, More, BrokenSeals))

          navigator.nextPage(SealsInformationPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
        }
      }

      "for the OtherInformationPage page" - {

        "must go to the next WhatWrongWith page to answer" in {

          val userAnswers = emptyUserAnswers.set(WrongWithMovementPage, Set[WrongWithMovement](Less, More, Other))

          navigator.nextPage(OtherInformationPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
        }
      }

      "for the AddMoreInformation page" - {

        s"when the user answers is Yes" - {

          "must go to the MoreInformation page" in {

            val userAnswers = emptyUserAnswers.set(AddMoreInformationPage, true)

            navigator.nextPage(AddMoreInformationPage, NormalMode, userAnswers) mustBe routes.MoreInformationController.loadMoreInformation(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is No" - {

          "must go to the CheckYourAnswers page" in {

            val userAnswers = emptyUserAnswers.set(AddMoreInformationPage, false)

            navigator.nextPage(AddMoreInformationPage, NormalMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }
      }

      "for the MoreInformation page" - {

        "must go to the CheckYourAnswers page" in {

          navigator.nextPage(MoreInformationPage, NormalMode, emptyUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }
      }

      "for the CheckYourAnswers page" - {

        "must go to the Confirmation page" in {

          navigator.nextPage(CheckAnswersPage, NormalMode, emptyUserAnswers) mustBe routes.ConfirmationController.onPageLoad(testErn, testArc)
        }
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
      }
    }

    "calling the .nextWrongWithMovementOptionToAnswer method" - {

      "when all options are selected" - {

        val setOfOptions: Set[WrongWithMovement] = Set(Damaged, Other, Less, More, BrokenSeals)

        "when user hasn't answered any reasons yet" - {

          "must return the first one to answer based on the order radio items" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions) mustBe Some(Less)
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

        val setOfOptions: Set[WrongWithMovement] = Set(Other, More)

        "when user hasn't answered any reasons yet" - {

          "must return the first one to answer based on the order radio items" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions) mustBe Some(More)
          }
        }

        "when user has answered some of the reasons" - {

          "must return the next one to answer" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions, Some(More)) mustBe Some(Other)
          }
        }

        "when user has answered the last reason" - {

          "must return None" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions, Some(Other)) mustBe None
          }
        }
      }

      "when one of the options are selected" - {

        val setOfOptions: Set[WrongWithMovement] = Set(More)

        "when user hasn't answered any reasons yet" - {

          "must return the first one to answer based on the order radio items" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions) mustBe Some(More)
          }
        }

        "when user has answered the last reason" - {

          "must return None" in {
            navigator.nextWrongWithMovementOptionToAnswer(setOfOptions, Some(More)) mustBe None
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
