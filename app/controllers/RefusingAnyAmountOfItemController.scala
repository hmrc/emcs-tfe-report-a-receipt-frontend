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
import forms.RefusingAnyAmountOfItemFormProvider
import models.Mode
import navigation.Navigator
import pages.unsatisfactory.individualItems.RefusingAnyAmountOfItemPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.RefusingAnyAmountOfItemView

import javax.inject.Inject
import scala.concurrent.Future

class RefusingAnyAmountOfItemController @Inject()(override val messagesApi: MessagesApi,
                                                  override val userAnswersService: UserAnswersService,
                                                  override val navigator: Navigator,
                                                  override val auth: AuthAction,
                                                  override val userAllowList: UserAllowListAction,
                                                  override val withMovement: MovementAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  formProvider: RefusingAnyAmountOfItemFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: RefusingAnyAmountOfItemView) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Ok(view(
        form = fillForm(RefusingAnyAmountOfItemPage(idx), formProvider()),
        action = routes.RefusingAnyAmountOfItemController.onSubmit(ern, arc, idx, mode)
      ))
    }

  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, routes.RefusingAnyAmountOfItemController.onSubmit(ern, arc, idx, mode)))),
        value =>
          saveAndRedirect(RefusingAnyAmountOfItemPage(idx), value, mode)
      )
    }
}
