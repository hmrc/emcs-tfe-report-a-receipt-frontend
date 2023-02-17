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
import forms.MoreInformationFormProvider
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.{AddMoreInformationPage, MoreInformationPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import utils.JsonOptionFormatter
import views.html.MoreInformationView

import javax.inject.Inject
import scala.concurrent.Future

class MoreInformationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val sessionRepository: SessionRepository,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val withMovement: MovementAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: MoreInformationFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: MoreInformationView
                                     ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Ok(view(fillForm(MoreInformationPage, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          for {
            updatedYesNo <- save(AddMoreInformationPage, value.exists(_.nonEmpty))
            saveAndRedirect <- saveAndRedirect(MoreInformationPage, value, updatedYesNo, mode)
          } yield saveAndRedirect
      )
    }
}
