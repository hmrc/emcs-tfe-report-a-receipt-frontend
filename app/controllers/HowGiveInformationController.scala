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
import forms.HowGiveInformationFormProvider
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.unsatisfactory.HowGiveInformationPage
import pages.{AcceptMovementPage, DateOfArrivalPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.HowGiveInformationView

import javax.inject.Inject
import scala.concurrent.Future

class HowGiveInformationController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              override val userAnswersService: UserAnswersService,
                                              override val navigator: Navigator,
                                              override val auth: AuthAction,
                                                   override val withMovement: MovementAction,
                                              override val getData: DataRetrievalAction,
                                              override val requireData: DataRequiredAction,
                                              formProvider: HowGiveInformationFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: HowGiveInformationView
                                        ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      Ok(view(fillForm(HowGiveInformationPage, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value => {
          val newUserAnswers: UserAnswers = cleanseUserAnswersIfValueHasChanged(
            page = HowGiveInformationPage,
            newAnswer = value,
            cleansingFunction = request.userAnswers.filterForPages(Seq(DateOfArrivalPage, AcceptMovementPage))
          )
          saveAndRedirect(HowGiveInformationPage, value, newUserAnswers, mode)
        }
      )
    }
}
