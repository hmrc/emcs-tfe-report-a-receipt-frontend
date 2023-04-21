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
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import models.{ItemModel, NormalMode}
import navigation.Navigator
import pages.unsatisfactory.individualItems.SelectItemsPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.SelectItemsView

import javax.inject.Inject

class SelectItemsController @Inject()(override val messagesApi: MessagesApi,
                                      override val userAnswersService: UserAnswersService,
                                      override val navigator: Navigator,
                                      override val auth: AuthAction,
                                      override val withMovement: MovementAction,
                                      override val getData: DataRetrievalAction,
                                      override val requireData: DataRequiredAction,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: SelectItemsView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      val filteredItems = getFilteredItems(request)
      if (filteredItems.isEmpty) {
        Redirect(routes.AddedItemsController.onPageLoad(ern, arc))
      } else {
        Ok(view(filteredItems))
      }
    }

  def addItemToList(ern: String, arc: String, itemUniqueReference: Int): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      saveAndRedirect(SelectItemsPage(itemUniqueReference), itemUniqueReference, NormalMode)
    }

  private[controllers] def getFilteredItems(implicit request: DataRequest[_]): Seq[MovementItem] = {
    val userAnswersUniqueReferences = request.userAnswers.itemReferences
    if (userAnswersUniqueReferences.isEmpty) {
      request.movementDetails.items
    } else {
      val userAnswerItems: Seq[ItemModel] =
        request.userAnswers.items

      request.movementDetails.items.filterNot {
        movementDetailsItem =>
          userAnswerItems
            .find(_.itemUniqueReference == movementDetailsItem.itemUniqueReference)
            .exists(_.checkAnswersItem.contains(true))
      }
    }
  }

}
