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
import models.AcceptMovement.PartiallyRefused
import models.UnitOfMeasure.Kilograms
import models.WrongWithMovement.Shortage
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import models.{ItemShortageOrExcessModel, Mode, UnitOfMeasure}
import navigation.Navigator
import pages.AcceptMovementPage
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, RefusedAmountPage, RefusingAnyAmountOfItemPage}
import play.api.data.{Form, FormError}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import services.{GetCnCodeInformationService, UserAnswersService}
import views.html.ItemShortageOrExcessView

import javax.inject.Inject
import scala.concurrent.Future

class ItemShortageOrExcessController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                override val userAnswersService: UserAnswersService,
                                                override val navigator: Navigator,
                                                override val auth: AuthAction,
                                                override val userAllowList: UserAllowListAction,
                                                override val withMovement: MovementAction,
                                                override val getData: DataRetrievalAction,
                                                override val requireData: DataRequiredAction,
                                                formProvider: ItemShortageOrExcessFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: ItemShortageOrExcessView,
                                                getCnCodeInformationService: GetCnCodeInformationService
                                              ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withItemAsync(idx) { item =>
        getCnCodeInformationService.get(Seq(item)).map {
          serviceResult =>
            val unitOfMeasure = serviceResult.head._2.unitOfMeasureCode.toUnitOfMeasure
            Ok(renderView(fillForm(ItemShortageOrExcessPage(idx), formProvider()), ern, arc, idx, mode, unitOfMeasure))
        }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withItemAsync(idx) { item =>
        getCnCodeInformationService.get(Seq(item)).flatMap {
          serviceResult =>
            val unitOfMeasure = serviceResult.head._2.unitOfMeasureCode.toUnitOfMeasure
            submitAndTrimWhitespaceFromTextarea(Some(ItemShortageOrExcessPage(idx)), formProvider)(
              formWithErrors =>
                Future.successful(BadRequest(renderView(formWithErrors, ern, arc, idx, mode, unitOfMeasure)))
            ) {
              case value if (value.wrongWithItem == Shortage) && (value.amount > maxAmount(idx, item)) =>
                val formWithError =
                  formProvider()
                    .fill(value)
                    .withError(FormError("amount", "itemShortageOrExcess.amount.error.tooLarge", Seq(maxAmount(idx, item))))
                Future.successful(BadRequest(renderView(formWithError, ern, arc, idx, mode, unitOfMeasure)))
              case value =>
                saveAndRedirect(ItemShortageOrExcessPage(idx), value, mode)
            }
        }
      }
    }

  private def maxAmount(idx: Int, item: MovementItem)(implicit request: DataRequest[_]) = {

    val isPartiallyRefusingAnAmountOfItem =
      request.userAnswers.get(RefusingAnyAmountOfItemPage(idx)).contains(true) && request.userAnswers.get(AcceptMovementPage).contains(PartiallyRefused)

    if (isPartiallyRefusingAnAmountOfItem) {
      item.quantity - request.userAnswers.get(RefusedAmountPage(idx)).getOrElse[BigDecimal](0)
    } else {
      item.quantity
    }
  }

  private def renderView(form: Form[ItemShortageOrExcessModel], ern: String, arc: String, idx: Int, mode: Mode, unitOfMeasure: UnitOfMeasure)
                        (implicit request: DataRequest[_]): HtmlFormat.Appendable =
    view(form, routes.ItemShortageOrExcessController.onSubmit(ern, arc, idx, mode), unitOfMeasure)

}
