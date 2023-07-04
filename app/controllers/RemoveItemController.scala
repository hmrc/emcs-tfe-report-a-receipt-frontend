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
import forms.AddMoreInformationFormProvider
import models.{Mode, NormalMode, ReviewMode}
import navigation.Navigator
import pages.unsatisfactory.individualItems.RemoveItemPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import utils.JsonOptionFormatter
import views.html.RemoveItemView

import javax.inject.Inject
import scala.concurrent.Future

class RemoveItemController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      override val userAnswersService: UserAnswersService,
                                      override val navigator: Navigator,
                                      override val auth: AuthAction,
                                      override val userAllowList: UserAllowListAction,
                                      override val withMovement: MovementAction,
                                      override val getData: DataRetrievalAction,
                                      override val requireData: DataRequiredAction,
                                      formProvider: AddMoreInformationFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: RemoveItemView
                                    ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {

  def onPageLoad(ern: String,
                 arc: String,
                 idx: Int,
                 mode: Mode = NormalMode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAddedItemAsync(idx) {
        _ => Future.successful(Ok(view(
          form = formProvider(RemoveItemPage(idx)),
          page = RemoveItemPage(idx),
          action = routes.RemoveItemController.onSubmit(ern, arc, idx, mode)
        )))
      }
    }

  def onSubmit(ern: String,
               arc: String,
               idx: Int,
               mode: Mode = NormalMode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAddedItemAsync(idx) {
        _ =>
          formProvider(RemoveItemPage(idx)).bindFromRequest().fold(
            formWithErrors =>
              Future.successful(BadRequest(view(formWithErrors, RemoveItemPage(idx), routes.RemoveItemController.onSubmit(ern, arc, idx, mode)))),
            {
              case true =>
                val updatedAnswers = request.userAnswers.removeItem(idx)
                userAnswersService.set(updatedAnswers).map { _ =>
                  Redirect(routes.AddedItemsController.onPageLoad(request.ern, request.arc))
                }
              case false if mode == ReviewMode =>
                Future.successful(Redirect(routes.CheckYourAnswersController.onPageLoad(ern, arc)))
              case false =>
                Future.successful(Redirect(routes.AddedItemsController.onPageLoad(ern, arc)))
            }
          )
      }
    }

}
