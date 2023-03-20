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
import forms.AddGiveReasonItemDamagedFormProvider

import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.QuestionPage
import pages.unsatisfactory.individualItems.{AddGiveReasonItemDamagedPage, ChooseGiveReasonItemDamagedPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import services.UserAnswersService
import views.html.AddGiveReasonItemDamagedView

import scala.concurrent.Future

class AddGiveReasonItemDamagedController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val withMovement: MovementAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: AddGiveReasonItemDamagedFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: AddGiveReasonItemDamagedView
                                     ) extends BaseNavigationController with AuthActionHelper {


  def loadAddGiveReasonDamagedItem(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onPageLoad(AddGiveReasonItemDamagedPage(idx), ern, arc, routes.AddGiveReasonItemDamagedController.submitAddGiveReasonDamagedItem(ern, arc, idx, mode))
  def submitAddGiveReasonDamagedItem(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onSubmit(AddGiveReasonItemDamagedPage(idx), ern, arc, mode, routes.AddGiveReasonItemDamagedController.submitAddGiveReasonDamagedItem(ern, arc, idx, mode))



  def onPageLoad(page: QuestionPage[String], ern: String, arc: String, action: Call): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Ok(view(fillForm(page, formProvider()), action))
    }

  def onSubmit(page: QuestionPage[String], ern: String, arc: String, mode: Mode, action: Call): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, action))),
        value =>
          saveAndRedirect(page, value, mode)
      )
    }
}
