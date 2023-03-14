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
import models.{ItemShortageOrExcessModel, Mode}
import models.UnitOfMeasure.Kilograms
import models.WrongWithMovement.Less
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import navigation.Navigator
import pages.unsatisfactory.individualItems.{ItemShortageOrExcessPage, SelectItemsPage}
import play.api.data.{Form, FormError}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import play.twirl.api.HtmlFormat
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
      Ok(renderView(fillForm(ItemShortageOrExcessPage(idx), formProvider()), ern, arc, idx, mode))
    }

  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      request.getItemDetails(idx) match {
        case Some(item) =>
          formProvider().bindFromRequest().fold(
            formWithErrors =>
              Future.successful(BadRequest(renderView(formWithErrors, ern, arc, idx, mode)))
            , {
              case value if value.wrongWithItem == Less && value.amount > item.quantity =>
                val formWithError =
                  formProvider()
                    .fill(value)
                    .withError(FormError("amount", "itemShortageOrExcess.amount.error.tooLarge", Seq(item.quantity)))
                Future.successful(BadRequest(renderView(formWithError, ern, arc, idx, mode)))
              case value =>
                saveAndRedirect(ItemShortageOrExcessPage(idx), value, mode)
            }
          )
        case None =>
          //Shouldn't be possible, but if this happens, redirect to the list view to recover journey
          Future.successful(Redirect(routes.SelectItemsController.onPageLoad(ern, arc)))
      }
    }

  private def renderView(form: Form[ItemShortageOrExcessModel], ern: String, arc: String, idx: Int, mode: Mode)
                        (implicit request: DataRequest[_]): HtmlFormat.Appendable =
  //TODO: Hardcoded to kg, should be determined from the reference data based on the CN Code in future story
    view(form, routes.ItemShortageOrExcessController.onSubmit(ern, arc, idx, mode), Kilograms)

}
