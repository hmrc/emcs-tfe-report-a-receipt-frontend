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
import navigation.Navigator
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{GetCnCodeInformationService, GetPackagingTypesService, GetWineOperationsService, UserAnswersService}
import views.html.ItemDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class ItemDetailsController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val userAllowList: UserAllowListAction,
                                       override val withMovement: MovementAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       getCnCodeInformationService: GetCnCodeInformationService,
                                       getPackagingTypesService: GetPackagingTypesService,
                                       getWineOperationsService: GetWineOperationsService,
                                       view: ItemDetailsView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int): Action[AnyContent] = {
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      request.movementDetails.item(idx) match {
        case Some(item) =>
          getPackagingTypesService.getPackagingTypes(Seq(item)).flatMap {
            getWineOperationsService.getWineOperations(_).flatMap {
              getCnCodeInformationService.getCnCodeInformationWithMovementItems(_).map {
                case (item, cnCodeInformation) :: Nil => Ok(view(item, cnCodeInformation))
                case _ =>
                  logger.warn(s"[onPageLoad] Problem retrieving reference data for item idx: $idx against ERN: $ern and ARC: $arc")
                  Redirect(routes.SelectItemsController.onPageLoad(request.ern, request.arc).url)
              }
            }
          }
        case None =>
          logger.warn(s"[onPageLoad] Unable to find item with idx: $idx against ERN: $ern and ARC: $arc")
          Future.successful(Redirect(routes.SelectItemsController.onPageLoad(request.ern, request.arc).url))
      }
    }
  }

}
