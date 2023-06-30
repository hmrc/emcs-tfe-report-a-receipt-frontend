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
import models.ItemModel
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import navigation.Navigator
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{ReferenceDataService, UserAnswersService}
import views.html.SelectItemsView

import javax.inject.Inject
import scala.concurrent.Future

class SelectItemsController @Inject()(override val messagesApi: MessagesApi,
                                      override val userAnswersService: UserAnswersService,
                                      override val navigator: Navigator,
                                      override val auth: AuthAction,
                                      override val userAllowList: UserAllowListAction,
                                      override val withMovement: MovementAction,
                                      override val getData: DataRetrievalAction,
                                      override val requireData: DataRequiredAction,
                                      val controllerComponents: MessagesControllerComponents,
                                      referenceDataService: ReferenceDataService,
                                      view: SelectItemsView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withFilteredItems { filteredItems =>
        referenceDataService.getMovementItemsWithReferenceData(filteredItems).map { serviceResult =>
          Ok(view(serviceResult))
        }
      }
    }

  private[controllers] def withFilteredItems(f: Seq[MovementItem] => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    if (request.userAnswers.itemReferences.isEmpty) {
      f(request.movementDetails.items)
    } else {
      request.movementDetails.items.filterNot { movementDetailsItem =>
        request.userAnswers.completedItems.exists(_.itemUniqueReference == movementDetailsItem.itemUniqueReference)
      } match {
        case filteredItems if filteredItems.nonEmpty =>
          f(filteredItems)
        case _ =>
          Future.successful(Redirect(routes.AddedItemsController.onPageLoad(request.ern, request.arc)))
      }
    }

}
