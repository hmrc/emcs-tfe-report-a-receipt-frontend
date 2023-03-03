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
import forms.OtherInformationFormProvider
import models.Mode
import navigation.Navigator
import pages.unsatisfactory._
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import utils.JsonOptionFormatter
import views.html.OtherInformationView

import javax.inject.Inject
import scala.concurrent.Future

class OtherInformationController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            override val sessionRepository: SessionRepository,
                                            override val navigator: Navigator,
                                            override val auth: AuthAction,
                                            override val withMovement: MovementAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            formProvider: OtherInformationFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: OtherInformationView
                                          ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {


  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Ok(view(
        fillForm(OtherInformationPage, formProvider()),
        routes.OtherInformationController.onSubmit(ern, arc, mode)
      ))
    }

  def onSubmit(ern: String,
               arc: String,
               mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(
            formWithErrors,
            routes.OtherInformationController.onSubmit(ern, arc, mode)
          ))),
        value => saveAndRedirect(OtherInformationPage, value, mode)
      )
    }
}
