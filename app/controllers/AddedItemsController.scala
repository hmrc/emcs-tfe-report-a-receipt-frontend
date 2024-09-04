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

import config.AppConfig
import controllers.actions._
import forms.AddAnotherItemFormProvider
import models.AcceptMovement.PartiallyRefused
import models.NormalMode
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import navigation.Navigator
import pages.AcceptMovementPage
import pages.unsatisfactory.individualItems.{AddedItemsPage, RefusedAmountPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCnCodeInformationService, UserAnswersService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.CheckAnswersItemHelper
import views.html.AddedItemsView

import javax.inject.Inject
import scala.concurrent.Future

class AddedItemsController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      override val auth: AuthAction,
                                      override val withMovement: MovementAction,
                                      override val getData: DataRetrievalAction,
                                      override val requireData: DataRequiredAction,
                                      override val controllerComponents: MessagesControllerComponents,
                                      view: AddedItemsView,
                                      formProvider: AddAnotherItemFormProvider,
                                      getCnCodeInformationService: GetCnCodeInformationService,
                                      checkAnswersItemHelper: CheckAnswersItemHelper,
                                      override val userAnswersService: UserAnswersService,
                                      override val navigator: Navigator
                                    )(implicit config: AppConfig) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withCompletedItems { items =>
        formattedItems(items).map { formattedItems =>
          val allItemsAdded = formattedItems.size == request.movementDetails.items.size
          Ok(view(Some(formProvider()), formattedItems, allItemsAdded, routes.AddedItemsController.onSubmit(ern, arc)))
        }
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withCompletedItems { items =>
        val isPartiallyRefused = request.userAnswers.get(AcceptMovementPage).contains(PartiallyRefused)
        formattedItems(items).map { formattedItems =>
          val allItemsAdded = formattedItems.size == request.movementDetails.items.size
          if (allItemsAdded) {
            onwardRedirect(formattedItems, allItemsAdded, isPartiallyRefused)
          } else {
            formProvider().bindFromRequest().fold(
              formWithErrors =>
                BadRequest(view(Some(formWithErrors), formattedItems, allItemsAdded, routes.AddedItemsController.onSubmit(ern, arc)))
              ,
              {
                case true => addAnotherItemRedirect()
                case _ => onwardRedirect(formattedItems, allItemsAdded, isPartiallyRefused)
              }
            )
          }
        }
      }
    }

  private def formattedItems(items: Seq[MovementItem])(implicit request: DataRequest[_]): Future[Seq[(Int, SummaryList)]] =
    getCnCodeInformationService.getCnCodeInformationWithMovementItems(items).map { serviceResult =>
      serviceResult.map {
        case (item, cnCodeInformation) =>
          item.itemUniqueReference -> checkAnswersItemHelper.summaryList(
            idx = item.itemUniqueReference,
            unitOfMeasure = cnCodeInformation.unitOfMeasureCode.toUnitOfMeasure
          )
      }
    }

  private def withCompletedItems(f: Seq[MovementItem] => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.getAllCompletedItemDetails match {
      case items if items.isEmpty => Future.successful(addAnotherItemRedirect())
      case items => f(items)
    }

  private def addAnotherItemRedirect()(implicit request: DataRequest[_]): Result =
    Redirect(routes.SelectItemsController.onPageLoad(request.ern, request.arc))

  private def onwardRedirect(serviceResult: Seq[(Int, SummaryList)],
                             allItemsAdded: Boolean,
                             isPartiallyRefused: Boolean)
                            (implicit request: DataRequest[_]): Result = {

    def hasAtLeastSomeRefusedAmountOfOneItem()(implicit request: DataRequest[_]): Boolean = {
      request.userAnswers.itemReferences.map {
        uniqueReference =>
          request.userAnswers.get(RefusedAmountPage(uniqueReference)).getOrElse[BigDecimal](0)
      }.sum > 0
    }

    if (!isPartiallyRefused || hasAtLeastSomeRefusedAmountOfOneItem()) {
      Redirect(navigator.nextPage(AddedItemsPage, NormalMode, request.userAnswers))
    } else {
      val formWithError = formProvider().withGlobalError("addedItems.error.atLeastOneItem").fill(false)
      BadRequest(
        view(Some(formWithError), serviceResult, allItemsAdded, routes.AddedItemsController.onSubmit(request.ern, request.arc))
      )
    }
  }

}
