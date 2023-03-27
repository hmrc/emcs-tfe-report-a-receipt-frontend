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
import forms.AddMoreInformationFormProvider
import models.NormalMode
import navigation.Navigator
import pages.unsatisfactory.individualItems.RemoveItemPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import utils.JsonOptionFormatter
import views.html.AddMoreInformationView

import javax.inject.Inject
import scala.concurrent.Future

class RemoveItemController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              override val userAnswersService: UserAnswersService,
                                              override val navigator: Navigator,
                                              override val auth: AuthAction,
                                              override val withMovement: MovementAction,
                                              override val getData: DataRetrievalAction,
                                              override val requireData: DataRequiredAction,
                                              formProvider: AddMoreInformationFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: AddMoreInformationView
                                            ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {

  def onPageLoad(ern: String,
                         arc: String,
                         idx: Int): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      withItem(idx) {
        _ => Ok(view(
          form = fillForm(RemoveItemPage(idx), formProvider(RemoveItemPage(idx))),
          page = RemoveItemPage(idx),
          action = routes.RemoveItemController.onSubmit(ern, arc, idx)
        ))
      }
    }

  def onSubmit(ern: String,
                       arc: String,
                       idx: Int): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withItemAsync(idx) {
        _ =>
        formProvider(RemoveItemPage(idx)).bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, RemoveItemPage(idx), routes.RemoveItemController.onSubmit(ern, arc, idx)))),
          {
            case true =>
              removeItemAndRedirect(idx)
            case false =>
              saveAndRedirect(RemoveItemPage(idx), false, NormalMode)
          }
        )
      }
    }

}