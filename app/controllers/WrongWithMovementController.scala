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
import forms.WrongWithMovementFormProvider
import models.{Mode, UserAnswers, WrongWithMovement}
import navigation.Navigator
import pages.unsatisfactory.individualItems.WrongWithItemPage
import pages.unsatisfactory.{HowMuchIsWrongPage, WrongWithMovementPage}
import pages.{AcceptMovementPage, DateOfArrivalPage, QuestionPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import services.UserAnswersService
import views.html.WrongWithMovementView

import javax.inject.Inject
import scala.concurrent.Future

class WrongWithMovementController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val navigator: Navigator,
                                             override val auth: AuthAction,
                                             override val userAllowList: UserAllowListAction,
                                             override val withMovement: MovementAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             formProvider: WrongWithMovementFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: WrongWithMovementView
                                           ) extends BaseNavigationController with AuthActionHelper {

  def loadWrongWithMovement(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(WrongWithMovementPage, ern, arc, routes.WrongWithMovementController.submitWrongWithMovement(ern, arc, mode))

  def submitWrongWithMovement(ern: String, arc: String, mode: Mode): Action[AnyContent] = {
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider(WrongWithMovementPage).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(
            WrongWithMovementPage,
            formWithErrors,
            routes.WrongWithMovementController.submitWrongWithMovement(ern, arc, mode)
          ))),
        value => {
          val newUserAnswers: UserAnswers = request.userAnswers.filterForPages(Seq(
            DateOfArrivalPage,
            AcceptMovementPage,
            HowMuchIsWrongPage
          ))
          saveAndRedirect(WrongWithMovementPage, value, newUserAnswers, mode)
        }
      )
    }
  }

  def loadwrongWithItem(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onPageLoad(WrongWithItemPage(idx), ern, arc, routes.WrongWithMovementController.submitwrongWithItem(ern, arc, idx, mode))

  def submitwrongWithItem(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] = {
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider(WrongWithItemPage(idx)).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(
            WrongWithItemPage(idx),
            formWithErrors,
            routes.WrongWithMovementController.submitwrongWithItem(ern, arc, idx, mode)
          ))),
        value =>
          saveAndRedirect(WrongWithItemPage(idx), value, mode)
      )
    }
  }

  private def onPageLoad(page: QuestionPage[Set[WrongWithMovement]],
                         ern: String,
                         arc: String,
                         action: Call): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Ok(view(page, fillForm(page, formProvider(page)), action))
    }
}
