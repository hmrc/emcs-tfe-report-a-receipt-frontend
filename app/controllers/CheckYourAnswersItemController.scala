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

package controllers

import controllers.actions._
import models.NormalMode
import navigation.Navigator
import pages.unsatisfactory.individualItems.CheckAnswersItemPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{GetCnCodeInformationService, UserAnswersService}
import viewmodels.checkAnswers.CheckAnswersItemHelper
import views.html.CheckYourAnswersItemView

import javax.inject.Inject

class CheckYourAnswersItemController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            override val auth: AuthAction,
                                            override val userAllowList: UserAllowListAction,
                                            override val withMovement: MovementAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            val navigator: Navigator,
                                            view: CheckYourAnswersItemView,
                                            checkAnswersItemHelper: CheckAnswersItemHelper,
                                            getCnCodeInformationService: GetCnCodeInformationService,
                                            val userAnswersService: UserAnswersService
                                          ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withItemAsync(idx) {
        item =>
          getCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item)).map {
            serviceResult =>
              val unitOfMeasure = serviceResult.head._2.unitOfMeasureCode.toUnitOfMeasure

              Ok(view(
                submitAction = routes.CheckYourAnswersItemController.onSubmit(ern, arc, idx),
                itemName = serviceResult.head._2.cnCodeDescription,
                list = checkAnswersItemHelper.summaryList(idx, unitOfMeasure)
              ))
          }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Int): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withItemAsync(idx) {
        _ =>
          saveAndRedirect(CheckAnswersItemPage(idx), true, request.userAnswers, NormalMode)
      }
    }
}
