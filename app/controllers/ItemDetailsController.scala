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
import services.{UserAnswersService, ReferenceDataService}
import views.html.ItemDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class ItemDetailsController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val withMovement: MovementAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       referenceDataService: ReferenceDataService,
                                       view: ItemDetailsView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withMovementItemAsync(idx) {
        referenceDataService.itemWithReferenceData(_) { (item, cnCodeInformation) =>

          val refererUrl = request.headers.get("Referer").getOrElse(controllers.routes.IndexController.onPageLoad(ern, arc).url)

          Future.successful(Ok(view(item, cnCodeInformation, refererUrl)))
        }
      }
    }

}
