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
import pages.QuestionPage
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems.ItemOtherInformationPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import services.UserAnswersService
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
                                          ) extends BaseNavigationController with AuthActionHelper {


  def loadOtherInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(OtherInformationPage, ern, arc, routes.OtherInformationController.submitOtherInformation(ern, arc, mode))

  def submitOtherInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(OtherInformationPage, ern, arc, routes.OtherInformationController.submitOtherInformation(ern, arc, mode), mode)

  def loadItemOtherInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onPageLoad(ItemOtherInformationPage(idx), ern, arc, routes.OtherInformationController.submitItemOtherInformation(ern, arc, idx, mode))

  def submitItemOtherInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onSubmit(ItemOtherInformationPage(idx), ern, arc, routes.OtherInformationController.submitItemOtherInformation(ern, arc, idx, mode), mode)

  private def onPageLoad(page: QuestionPage[String], ern: String, arc: String, action: Call): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Ok(view(page, fillForm(page, formProvider(page)), action))
    }

  private def onSubmit(page: QuestionPage[String], ern: String, arc: String, action: Call, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider(page).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(page, formWithErrors, action))),
        value =>
          saveAndRedirect(page, value, mode)
      )
    }
}
