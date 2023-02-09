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

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import controllers.routes
import models.AcceptMovement.{PartiallyRefused, Satisfactory}
import pages._
import models._

@Singleton
class Navigator @Inject()() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case DateOfArrivalPage =>
      (userAnswers: UserAnswers) => routes.AcceptMovementController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
    case AcceptMovementPage =>
      (userAnswers: UserAnswers) => userAnswers.get(AcceptMovementPage) match {
        case Some(Satisfactory) => routes.AddMoreInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }
    case AddMoreInformationPage =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
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
}
