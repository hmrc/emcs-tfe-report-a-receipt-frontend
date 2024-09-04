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
import models.requests.DataRequest
import navigation.Navigator
import pages.unsatisfactory._
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.UserAnswersService
import utils.Logging
import views.html.OtherInformationView

import javax.inject.Inject
import scala.concurrent.Future

class OtherInformationController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            override val userAnswersService: UserAnswersService,
                                            override val navigator: Navigator,
                                            override val auth: AuthAction,
                                            override val withMovement: MovementAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            formProvider: OtherInformationFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: OtherInformationView
                                          ) extends BaseNavigationController with AuthActionHelper with Logging {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      Ok(view(
        OtherInformationPage,
        fillForm(OtherInformationPage, formProvider(Some(OtherInformationPage))),
        routes.OtherInformationController.onSubmit(ern, arc, mode)
      ))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request: DataRequest[_] =>
      submitAndTrimWhitespaceFromTextarea[String](Some(OtherInformationPage), formProvider)(
        formWithErrors => Future.successful(BadRequest(view(
          OtherInformationPage,
          formWithErrors,
          routes.OtherInformationController.onSubmit(ern, arc, mode)
        )))
      )(
        value => saveAndRedirect(OtherInformationPage, value, mode)
      )
    }
}
