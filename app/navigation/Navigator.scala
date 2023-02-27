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
import models.WrongWithMovement.{BrokenSeals, Damaged, Less, More, Other}
import models._
import pages._
import pages.unsatisfactory.{AddShortageInformationPage, HowMuchIsWrongPage, WrongWithMovementPage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject()() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case DateOfArrivalPage =>
      (userAnswers: UserAnswers) => routes.AcceptMovementController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
    case AcceptMovementPage =>
      (userAnswers: UserAnswers) => userAnswers.get(AcceptMovementPage) match {
        case Some(Satisfactory) => routes.AddMoreInformationController.loadMoreInformation(userAnswers.ern, userAnswers.arc, NormalMode)
        case Some(Unsatisfactory) => routes.HowMuchIsWrongController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }
    case HowMuchIsWrongPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(HowMuchIsWrongPage) match {
          case Some(TheWholeMovement) => routes.WrongWithMovementController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
          case Some(_) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
          case None => routes.HowMuchIsWrongController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }
    case WrongWithMovementPage =>
      (userAnswers: UserAnswers) => userAnswers.get(WrongWithMovementPage) match {
        case Some(checkboxSelections) =>
          redirectToNextWrongMovementPage(checkboxSelections)(userAnswers)
        case _ =>
          routes.WrongWithMovementController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
      }
    case AddMoreInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddMoreInformationPage) match {
          case Some(true) => routes.MoreInformationController.loadMoreInformation(userAnswers.ern, userAnswers.arc, NormalMode)
          case _ => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
        }
    case AddShortageInformationPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(AddShortageInformationPage) match {
          case Some(true) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
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
                                                              lastOption: Option[WrongWithMovement] = None): Option[WrongWithMovement] = {
    val orderedSetOfOptions = WrongWithMovement.values.filter { option => selectedOptions.contains(option) }
    lastOption match {
      case Some(value) if orderedSetOfOptions.lastOption.contains(value) =>
        None
      case Some(value) =>
        Some(orderedSetOfOptions(orderedSetOfOptions.indexOf(value) + 1))
      case None =>
        orderedSetOfOptions.headOption
    }
  }

  private[navigation] def redirectToNextWrongMovementPage(selectedOptions: Set[WrongWithMovement],
                                                          lastOption: Option[WrongWithMovement] = None)(implicit userAnswers: UserAnswers): Call =
    nextWrongWithMovementOptionToAnswer(selectedOptions, lastOption) match {
      case Some(Less) =>
        routes.AddMoreInformationController.loadShortageInformation(userAnswers.ern, userAnswers.arc, NormalMode)
      case Some(More) =>
        //TODO: Will redirect to the More Items More Information Yes/No
        routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      case Some(Damaged) =>
        //TODO: Will redirect to the Damaged Items More Information Yes/No
        routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      case Some(BrokenSeals) =>
        //TODO: Will redirect to the Broken Seals More Information Yes/No
        routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      case Some(Other) =>
        //TODO: Will redirect to the Other Information page
        routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      case None =>
        routes.AddMoreInformationController.loadMoreInformation(userAnswers.ern, userAnswers.arc, NormalMode)
    }
}
