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
import forms.DetailsSelectItemFormProvider
import models.NormalMode
import models.requests.DataRequest
import navigation.Navigator
import pages.unsatisfactory.individualItems.SelectItemsPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.Format.GenericFormat
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{ReferenceDataService, UserAnswersService}
import views.html.DetailsSelectItemView

import javax.inject.Inject
import scala.concurrent.Future

class DetailsSelectItemController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val navigator: Navigator,
                                             override val auth: AuthAction,
                                             override val withMovement: MovementAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                                 formProvider: DetailsSelectItemFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             referenceDataService: ReferenceDataService,
                                             view: DetailsSelectItemView
                                           ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, formProvider(), idx)
    }

  def onSubmit(ern: String, arc: String, idx: Int): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, _, idx),
        {
          case true => addItemToListAndRedirect(idx)
          case false => Future.successful(Redirect(routes.SelectItemsController.onPageLoad(request.ern, request.arc).url))
        }
      )
    }

  private def renderView(status: Status, form: Form[_], idx: Int)(implicit request: DataRequest[_]): Future[Result] =
    withMovementItemAsync(idx) {
      referenceDataService.itemWithReferenceData(_) { (item, cnCodeInformation) =>
        Future.successful(status(view(form, item, cnCodeInformation)))
      }
    }

  private def addItemToListAndRedirect(idx: Int)(implicit request: DataRequest[_]): Future[Result] = {
    // remove any previously entered data before adding an item to the list
    val newUserAnswers = request.userAnswers.removeItem(idx)
    saveAndRedirect(SelectItemsPage(idx), idx, newUserAnswers, NormalMode)
  }
}
