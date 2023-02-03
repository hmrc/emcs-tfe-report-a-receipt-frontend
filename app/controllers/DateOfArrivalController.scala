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
import forms.DateOfArrivalFormProvider
import models.Mode
import navigation.Navigator
import pages.DateOfArrivalPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.DateOfArrivalView

import javax.inject.Inject
import scala.concurrent.Future

class DateOfArrivalController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         override val sessionRepository: SessionRepository,
                                         override val navigator: Navigator,
                                         auth: AuthAction,
                                         withMovement: MovementAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: DateOfArrivalFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: DateOfArrivalView
                                       ) extends BaseNavigationController {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    (auth(ern) andThen withMovement(arc) andThen getData andThen requireData) { implicit request =>
      Ok(view(fillForm(DateOfArrivalPage, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    (auth(ern) andThen withMovement(arc) andThen getData andThen requireData).async { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value => {
          saveAndRedirect(DateOfArrivalPage, value, mode)
        }
      )
    }
}
