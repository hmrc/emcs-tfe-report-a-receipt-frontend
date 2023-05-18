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
import forms.AcceptMovementFormProvider
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.{AcceptMovementPage, DateOfArrivalPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.AcceptMovementView

import javax.inject.Inject
import scala.concurrent.Future

class AcceptMovementController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          override val userAnswersService: UserAnswersService,
                                          override val navigator: Navigator,
                                          override val auth: AuthAction,
                                          override val userAllowList: UserAllowListAction,
                                          override val withMovement: MovementAction,
                                          override val getData: DataRetrievalAction,
                                          override val requireData: DataRequiredAction,
                                          formProvider: AcceptMovementFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: AcceptMovementView
                                        ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Ok(view(fillForm(AcceptMovementPage, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value => {
          val newUserAnswers: UserAnswers = cleanseUserAnswersIfValueHasChanged(
            page = AcceptMovementPage,
            newAnswer = value,
            cleansingFunction = request.userAnswers.filterForPages(Seq(DateOfArrivalPage))
          )
          saveAndRedirect(AcceptMovementPage, value, newUserAnswers, mode)
        }
      )
    }
}
