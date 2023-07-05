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
import forms.RefusedAmountFormProvider
import models.Mode
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import navigation.Navigator
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, RefusedAmountPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{ReferenceDataService, UserAnswersService}
import views.html.RefusedAmountView

import javax.inject.Inject
import scala.concurrent.Future

class RefusedAmountController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         override val userAnswersService: UserAnswersService,
                                         override val navigator: Navigator,
                                         override val auth: AuthAction,
                                         override val userAllowList: UserAllowListAction,
                                         override val withMovement: MovementAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         formProvider: RefusedAmountFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         referenceDataService: ReferenceDataService,
                                         view: RefusedAmountView
                                       ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAddedItemAsync(idx) { item =>
        renderView(Ok, fillForm(RefusedAmountPage(idx), form(item)), item, mode)
      }
    }

  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAddedItemAsync(idx) { item =>
        form(item).bindFromRequest().fold(
          renderView(BadRequest, _, item, mode),
          value =>
            saveAndRedirect(RefusedAmountPage(idx), value, mode)
        )
      }
    }

  private def form(item: MovementItem)(implicit request: DataRequest[_]): Form[BigDecimal] = {
    val shortageAmount = request.userAnswers.get(ItemShortageOrExcessPage(item.itemUniqueReference)).flatMap(_.shortageAmount)
    formProvider(item.quantity, shortageAmount)
  }

  private def renderView(status: Status, form: Form[_], item: MovementItem, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    referenceDataService.itemWithReferenceData(item) { (item, cnCodeInformation) =>
      Future.successful(status(view(
        form = form,
        action = routes.RefusedAmountController.onSubmit(request.ern, request.arc, item.itemUniqueReference, mode),
        unitOfMeasure = cnCodeInformation.unitOfMeasureCode.toUnitOfMeasure,
        item = item,
        cnCodeInfo = cnCodeInformation,
        idx = item.itemUniqueReference
      )))
    }
}
