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
import forms.ItemShortageOrExcessFormProvider

import javax.inject.Inject
import models.Mode
import models.UnitOfMeasure.Kilograms
import navigation.Navigator
import pages.unsatisfactory.individualItems.ItemShortageOrExcessPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.ItemShortageOrExcessView

import scala.concurrent.Future

class ItemShortageOrExcessController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val withMovement: MovementAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: ItemShortageOrExcessFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ItemShortageOrExcessView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Ok(view(
        fillForm(ItemShortageOrExcessPage(idx), formProvider()),
        routes.ItemShortageOrExcessController.onSubmit(ern, arc, idx, mode),
        Kilograms //TODO: Should be determined from the reference data based on the CN Code
      ))
    }

  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => {
          println("*************")
          println(formWithErrors.data)
          println("*************")
          Future.successful(BadRequest(view(
            formWithErrors,
            routes.ItemShortageOrExcessController.onSubmit(ern, arc, idx, mode),
            Kilograms //TODO: Should be determined from the reference data based on the CN Code
          )))},
        value =>
          saveAndRedirect(ItemShortageOrExcessPage(idx), value, mode)
      )
    }
}
