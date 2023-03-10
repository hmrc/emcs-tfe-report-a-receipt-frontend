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
import models.AcceptMovement.{Satisfactory, Unsatisfactory}
import models.HowMuchIsWrong.TheWholeMovement
import models.WrongWithMovement.{BrokenSeals, Damaged, Less, More, MoreOrLess, Other}
import models._
import pages._
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems.{SelectItemsPage, WrongWithItemPage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject()() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case DateOfArrivalPage =>
      (userAnswers: UserAnswers) => routes.AcceptMovementController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
    case AcceptMovementPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AcceptMovementPage) match {
          case Some(Satisfactory) => routes.AddMoreInformationController.loadMoreInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(Unsatisfactory) => routes.HowMuchIsWrongController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
          case _ => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
        }
    case HowMuchIsWrongPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(HowMuchIsWrongPage) match {
          case Some(TheWholeMovement) => routes.WrongWithMovementController.loadWrongWithMovement(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(_) => routes.SelectItemsController.onPageLoad(userAnswers.ern, userAnswers.arc)
          case None => routes.HowMuchIsWrongController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case selectItemsPage: SelectItemsPage =>
      (userAnswers: UserAnswers) => routes.WrongWithMovementController.loadwrongWithItem(userAnswers.ern, userAnswers.arc, selectItemsPage.idx, NormalMode)
    case WrongWithMovementPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage()(userAnswers)
    case wrongWithItemPage: WrongWithItemPage =>
      (userAnswers: UserAnswers) => redirectToNextItemWrongMovementPage(wrongWithItemPage)(userAnswers)
    case AddShortageInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddShortageInformationPage) match {
          case Some(true) => routes.MoreInformationController.loadShortageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(false) => redirectToNextWrongMovementPage(Some(Less))(userAnswers)
          case _ => routes.AddMoreInformationController.loadShortageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case ShortageInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage(Some(Less))(userAnswers)
    case AddExcessInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddExcessInformationPage) match {
          case Some(true) => routes.MoreInformationController.loadExcessInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(false) => redirectToNextWrongMovementPage(Some(More))(userAnswers)
          case _ => routes.AddMoreInformationController.loadExcessInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case ExcessInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage(Some(More))(userAnswers)
    case AddDamageInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddDamageInformationPage) match {
          case Some(true) => routes.MoreInformationController.loadDamageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(false) => redirectToNextWrongMovementPage(Some(Damaged))(userAnswers)
          case _ => routes.AddMoreInformationController.loadDamageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case DamageInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage(Some(Damaged))(userAnswers)
    case AddSealsInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddSealsInformationPage) match {
          case Some(true) => routes.MoreInformationController.loadSealsInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(false) => redirectToNextWrongMovementPage(Some(BrokenSeals))(userAnswers)
          case _ => routes.AddMoreInformationController.loadSealsInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case SealsInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage(Some(BrokenSeals))(userAnswers)
    case OtherInformationPage =>
      (userAnswers: UserAnswers) => redirectToNextWrongMovementPage(Some(Other))(userAnswers)
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
    case _ =>
      (userAnswers: UserAnswers) => routes.IndexController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

  private[navigation] def nextWrongWithMovementOptionToAnswer(selectedOptions: Set[WrongWithMovement],
                                                              lastOption: Option[WrongWithMovement] = None,
                                                              checkboxOptions: Seq[WrongWithMovement] = WrongWithMovement.values): Option[WrongWithMovement] = {
    val orderedSetOfOptions = checkboxOptions.filter(selectedOptions.contains)
    lastOption match {
      case Some(value) if orderedSetOfOptions.lastOption.contains(value) =>
        None
      case Some(value) =>
        Some(orderedSetOfOptions(orderedSetOfOptions.indexOf(value) + 1))
      case None =>
        orderedSetOfOptions.headOption
    }
  }

  private[navigation] def redirectToNextWrongMovementPage(lastOption: Option[WrongWithMovement] = None)(implicit userAnswers: UserAnswers): Call =
    userAnswers.get(WrongWithMovementPage) match {
      case Some(selectedOptions) =>
        nextWrongWithMovementOptionToAnswer(selectedOptions, lastOption) match {
          case Some(Less) =>
            routes.AddMoreInformationController.loadShortageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(More) =>
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
                                                              lastOption: Option[WrongWithMovement] = None)(implicit userAnswers: UserAnswers): Call =
    userAnswers.get(page) match {
      case Some(selectedOptions) =>
        nextWrongWithMovementOptionToAnswer(selectedOptions, lastOption, WrongWithMovement.individualItemValues) match {
          case Some(MoreOrLess) =>
            //TODO: Route to the ItemMoreLessPage (future story)
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          case Some(Damaged) =>
            //TODO: Route to the ItemDamagedPage (future story)
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          case Some(BrokenSeals) =>
            //TODO: Route to the ItemDamagedPage (future story)
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          case Some(Other) =>
            //TODO: Route to the ItemDamagedPage (future story)
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          case _ =>
            routes.AddMoreInformationController.loadMoreInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        }
      case _ =>
        routes.WrongWithMovementController.loadwrongWithItem(userAnswers.ern, userAnswers.arc, page.idx, NormalMode)
    }
}
