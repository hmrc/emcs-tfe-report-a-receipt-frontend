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
import forms.RefusingAnyAmountOfItemFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.unsatisfactory.individualItems.RefusingAnyAmountOfItemPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{UserAnswersService, ReferenceDataService}
import views.html.RefusingAnyAmountOfItemView

import javax.inject.Inject
import scala.concurrent.Future

class RefusingAnyAmountOfItemController @Inject()(override val messagesApi: MessagesApi,
                                                  override val userAnswersService: UserAnswersService,
                                                  override val navigator: Navigator,
                                                  override val auth: AuthAction,
                                                  override val userAllowList: UserAllowListAction,
                                                  override val withMovement: MovementAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  formProvider: RefusingAnyAmountOfItemFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  referenceDataService: ReferenceDataService,
                                                  view: RefusingAnyAmountOfItemView) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] = {
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(RefusingAnyAmountOfItemPage(idx), formProvider()), idx, mode)
    }
  }

  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) {
      implicit request =>
        formProvider().bindFromRequest().fold(
          formWithErrors => renderView(BadRequest, formWithErrors, idx, mode),
          value => {
            val newUserAnswers = cleanseUserAnswersIfValueHasChanged(
              page = RefusingAnyAmountOfItemPage(idx),
              newAnswer = value,
              cleansingFunction = request.userAnswers.resetItem(idx)
            )
            saveAndRedirect(RefusingAnyAmountOfItemPage(idx), value, newUserAnswers, mode)
          }
        )
    }

  private def renderView(status: Status, form: Form[_], idx: Int, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    request.movementDetails.item(idx) match {
      case Some(item) =>
        referenceDataService.getMovementItemsWithReferenceData(Seq(item)).map {
          case (item, cnCodeInformation) :: Nil =>
            status(view(
              form = form,
              action = routes.RefusingAnyAmountOfItemController.onSubmit(request.ern, request.arc, idx, mode),
              item = item,
              cnCodeInfo = cnCodeInformation
            ))
          case _ =>
            logger.warn(s"[renderView] Problem retrieving reference data for item idx: $idx against ERN: ${request.ern} and ARC: ${request.arc}")
            Redirect(routes.SelectItemsController.onPageLoad(request.ern, request.arc).url)
        }
      case None =>
        logger.warn(s"[renderView] Unable to find item with idx: $idx against ERN: ${request.ern} and ARC: ${request.arc}")
        Future.successful(Redirect(routes.SelectItemsController.onPageLoad(request.ern, request.arc).url))
    }
}
