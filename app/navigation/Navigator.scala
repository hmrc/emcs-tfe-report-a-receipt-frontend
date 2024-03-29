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

import controllers.routes
import models.AcceptMovement.{PartiallyRefused, Satisfactory, Unsatisfactory}
import models.HowGiveInformation.TheWholeMovement
import models.WrongWithMovement.{BrokenSeals, Damaged, Excess, Other, Shortage, ShortageOrExcess}
import models._
import pages._
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject()() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case DateOfArrivalPage =>
      (userAnswers: UserAnswers) =>
          routes.AcceptMovementController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
    case AcceptMovementPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AcceptMovementPage) match {
          case Some(Satisfactory) => routes.AddMoreInformationController.loadMoreInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(Unsatisfactory) => routes.SelectItemsController.onPageLoad(userAnswers.ern, userAnswers.arc)
          case Some(PartiallyRefused) => routes.SelectItemsController.onPageLoad(userAnswers.ern, userAnswers.arc)
          case _ => routes.HowGiveInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case HowGiveInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(HowGiveInformationPage) match {
          case Some(TheWholeMovement) => routes.WrongWithMovementController.loadWrongWithMovement(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(_) => routes.SelectItemsController.onPageLoad(userAnswers.ern, userAnswers.arc)
          case None => routes.HowGiveInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case SelectItemsPage(idx) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AcceptMovementPage) match {
          case Some(PartiallyRefused) => routes.RefusingAnyAmountOfItemController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)
          case _ => routes.WrongWithItemController.loadWrongWithItem(userAnswers.ern, userAnswers.arc, idx, NormalMode)
        }
    case RefusingAnyAmountOfItemPage(idx) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(RefusingAnyAmountOfItemPage(idx)) match {
          case Some(true) =>
            routes.RefusedAmountController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)
          case _ =>
            routes.WrongWithItemController.loadWrongWithItem(userAnswers.ern, userAnswers.arc, idx, NormalMode)
        }
    case RefusedAmountPage(idx) =>
      (userAnswers: UserAnswers) => routes.WrongWithItemController.loadWrongWithItem(userAnswers.ern, userAnswers.arc, idx, NormalMode)
    case WrongWithMovementPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage()(userAnswers)
    case wrongWithItemPage: WrongWithItemPage =>
      (userAnswers: UserAnswers) => redirectToNextItemWrongMovementPage(wrongWithItemPage)(userAnswers)
    case AddShortageInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddShortageInformationPage) match {
          case Some(true) => routes.MoreInformationController.loadShortageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(false) => redirectToNextWrongMovementPage(Some(Shortage))(userAnswers)
          case _ => routes.AddMoreInformationController.loadShortageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case ShortageInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage(Some(Shortage))(userAnswers)
    case AddExcessInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddExcessInformationPage) match {
          case Some(true) => routes.MoreInformationController.loadExcessInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(false) => redirectToNextWrongMovementPage(Some(Excess))(userAnswers)
          case _ => routes.AddMoreInformationController.loadExcessInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case ExcessInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage(Some(Excess))(userAnswers)
    case ItemShortageOrExcessPage(idx) =>
      (userAnswers: UserAnswers) => redirectToNextItemWrongMovementPage(WrongWithItemPage(idx), Some(ShortageOrExcess))(userAnswers)
    case AddDamageInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddDamageInformationPage) match {
          case Some(true) => routes.MoreInformationController.loadDamageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(false) => redirectToNextWrongMovementPage(Some(Damaged))(userAnswers)
          case _ => routes.AddMoreInformationController.loadDamageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case AddItemDamageInformationPage(idx) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddItemDamageInformationPage(idx)) match {
          case Some(true) => routes.ItemMoreInformationController.loadItemDamageInformation(userAnswers.ern, userAnswers.arc, idx, NormalMode)
          case Some(false) => redirectToNextItemWrongMovementPage(WrongWithItemPage(idx), Some(Damaged))(userAnswers)
          case _ => routes.AddItemMoreInformationController.loadItemDamageInformation(userAnswers.ern, userAnswers.arc, idx, NormalMode)
        }
    case ItemDamageInformationPage(idx) =>
      (userAnswers: UserAnswers) => redirectToNextItemWrongMovementPage(WrongWithItemPage(idx), Some(Damaged))(userAnswers)
    case DamageInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage(Some(Damaged))(userAnswers)
    case AddSealsInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddSealsInformationPage) match {
          case Some(true) => routes.MoreInformationController.loadSealsInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(false) => redirectToNextWrongMovementPage(Some(BrokenSeals))(userAnswers)
          case _ => routes.AddMoreInformationController.loadSealsInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case AddItemSealsInformationPage(idx) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddItemSealsInformationPage(idx)) match {
          case Some(true) => routes.ItemMoreInformationController.loadItemSealsInformation(userAnswers.ern, userAnswers.arc, idx, NormalMode)
          case Some(false) => redirectToNextItemWrongMovementPage(WrongWithItemPage(idx), Some(BrokenSeals))(userAnswers)
          case _ => routes.AddItemMoreInformationController.loadItemSealsInformation(userAnswers.ern, userAnswers.arc, idx, NormalMode)
        }
    case SealsInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage(Some(BrokenSeals))(userAnswers)
    case itemSealsInformationPage: ItemSealsInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextItemWrongMovementPage(WrongWithItemPage(itemSealsInformationPage.idx), Some(BrokenSeals))(userAnswers)
    case OtherInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage(Some(Other))(userAnswers)
    case ItemOtherInformationPage(idx) =>
      (userAnswers: UserAnswers) => redirectToNextItemWrongMovementPage(WrongWithItemPage(idx), Some(Other))(userAnswers)
    case AddMoreInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddMoreInformationPage) match {
          case Some(true) => routes.MoreInformationController.loadMoreInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case _ => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
        }
    case MoreInformationPage =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case CheckAnswersPage =>
      (userAnswers: UserAnswers) => routes.ConfirmationController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case CheckAnswersItemPage(_) =>
      (userAnswers: UserAnswers) => routes.AddedItemsController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case AddedItemsPage =>
      (userAnswers: UserAnswers) => routes.AddMoreInformationController.loadMoreInformation(userAnswers.ern, userAnswers.arc, NormalMode)
    case _ =>
      (userAnswers: UserAnswers) => routes.IndexController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case ItemShortageOrExcessPage(_) =>
      (userAnswers: UserAnswers) => routes.AddedItemsController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case RefusedAmountPage(_) =>
      (userAnswers: UserAnswers) => routes.AddedItemsController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case ItemSealsInformationPage(_) =>
      (userAnswers: UserAnswers) => routes.AddedItemsController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case ItemDamageInformationPage(_) =>
      (userAnswers: UserAnswers) => routes.AddedItemsController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case ItemOtherInformationPage(_) =>
      (userAnswers: UserAnswers) => routes.AddedItemsController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)

  }

  private[navigation] def redirectToNextWrongMovementPage(lastOptionAnswered: Option[WrongWithMovement] = None)(implicit userAnswers: UserAnswers): Call =
    userAnswers.get(WrongWithMovementPage) match {
      case Some(selectedOptions) =>
        nextWrongWithMovementOptionToAnswer(selectedOptions, lastOptionAnswered) match {
          case Some(Shortage) =>
            routes.AddMoreInformationController.loadShortageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(Excess) =>
            routes.AddMoreInformationController.loadExcessInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(Damaged) =>
            routes.AddMoreInformationController.loadDamageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(BrokenSeals) =>
            routes.AddMoreInformationController.loadSealsInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(Other) =>
            routes.OtherInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
          case _ =>
            routes.AddMoreInformationController.loadMoreInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        }
      case _ =>
        routes.WrongWithMovementController.loadWrongWithMovement(userAnswers.ern, userAnswers.arc, NormalMode)
    }

  private[navigation] def redirectToNextItemWrongMovementPage(page: WrongWithItemPage,
                                                              lastOptionAnswered: Option[WrongWithMovement] = None)(implicit userAnswers: UserAnswers): Call =
    userAnswers.get(page) match {
      case Some(selectedOptions) =>
        nextWrongWithMovementOptionToAnswer(selectedOptions, lastOptionAnswered) match {
          case Some(ShortageOrExcess) | Some(Shortage) | Some(Excess) =>
            routes.ItemShortageOrExcessController.onPageLoad(userAnswers.ern, userAnswers.arc, page.idx, NormalMode)
          case Some(Damaged) =>
            routes.AddItemMoreInformationController.loadItemDamageInformation(userAnswers.ern, userAnswers.arc, page.idx, NormalMode)
          case Some(BrokenSeals) =>
            routes.AddItemMoreInformationController.loadItemSealsInformation(userAnswers.ern, userAnswers.arc, page.idx, NormalMode)
          case Some(Other) =>
            routes.ItemOtherInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, page.idx, NormalMode)
          case _ =>
            routes.CheckYourAnswersItemController.onPageLoad(userAnswers.ern, userAnswers.arc, page.idx)
        }
      case _ =>
        routes.WrongWithItemController.loadWrongWithItem(userAnswers.ern, userAnswers.arc, page.idx, NormalMode)
    }

  private[navigation] def nextWrongWithMovementOptionToAnswer(selectedOptions: Set[WrongWithMovement],
                                                              lastOption: Option[WrongWithMovement] = None): Option[WrongWithMovement] = {
    val orderedSetOfOptions = WrongWithMovement.allValues.filter(selectedOptions.contains)
    lastOption match {
      case Some(value) if orderedSetOfOptions.lastOption.contains(value) =>
        None
      case Some(value) =>
        Some(orderedSetOfOptions(orderedSetOfOptions.indexOf(value) + 1))
      case None =>
        orderedSetOfOptions.headOption
    }
  }
}
