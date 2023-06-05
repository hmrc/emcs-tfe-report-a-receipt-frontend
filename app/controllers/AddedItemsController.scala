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
import forms.AddAnotherItemFormProvider
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import models.{ListItemWithProductCode, NormalMode}
import navigation.Navigator
import pages.unsatisfactory.individualItems.{AddedItemsPage, RefusedAmountPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCnCodeInformationService, UserAnswersService}
import viewmodels.AddedItemsSummary
import views.html.AddedItemsView

import javax.inject.Inject
import scala.concurrent.Future

class AddedItemsController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      override val auth: AuthAction,
                                      override val userAllowList: UserAllowListAction,
                                      override val withMovement: MovementAction,
                                      override val getData: DataRetrievalAction,
                                      override val requireData: DataRequiredAction,
                                      override val controllerComponents: MessagesControllerComponents,
                                      view: AddedItemsView,
                                      formProvider: AddAnotherItemFormProvider,
                                      addedItemsSummary: AddedItemsSummary,
                                      getCnCodeInformationService: GetCnCodeInformationService,
                                      override val userAnswersService: UserAnswersService,
                                      override val navigator: Navigator,
                                    ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withAddedItems(ern, arc) {
        getCnCodeInformationService.getCnCodeInformationWithListItems(_).flatMap {
          serviceResult =>
            val allItemsAdded = serviceResult.size == request.movementDetails.items.size
            Future.successful(Ok(view(Some(formProvider()), serviceResult, allItemsAdded, routes.AddedItemsController.onSubmit(ern, arc))))
        }
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withAddedItems(ern, arc) { items =>
        getCnCodeInformationService.getCnCodeInformationWithListItems(items).flatMap { implicit serviceResult =>
          val allItemsAdded = serviceResult.size == request.movementDetails.items.size
          if (allItemsAdded) {
            onwardRedirectIfAtLeastSomeOfOneItem(ern, arc, serviceResult, allItemsAdded)
          } else {
            formProvider().bindFromRequest().fold(
              formWithErrors => {
                Future.successful(
                  BadRequest(view(Some(formWithErrors), serviceResult, allItemsAdded, routes.AddedItemsController.onSubmit(ern, arc)))
                )
              },
              {
                case true => addAnotherItemRedirect(ern, arc)
                case _ => onwardRedirectIfAtLeastSomeOfOneItem(ern, arc, serviceResult, allItemsAdded)
              }
            )
          }

        }
      }
    }

  private def withAddedItems(ern: String, arc: String)(f: Seq[ListItemWithProductCode] => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    addedItemsSummary.itemList() match {
      case items if items.isEmpty => Future.successful(Redirect(routes.SelectItemsController.onPageLoad(ern, arc)))
      case items => f(items)
    }

  private def addAnotherItemRedirect(ern: String, arc: String): Future[Result] =
    Future.successful(Redirect(routes.SelectItemsController.onPageLoad(ern, arc)))

  private def onwardRedirectIfAtLeastSomeOfOneItem(ern: String,
                                                   arc: String,
                                                   serviceResult: Seq[(ListItemWithProductCode, CnCodeInformation)],
                                                   allItemsAdded: Boolean)
                                                  (implicit request: DataRequest[_]): Future[Result] = {

    def hasAtLeastSomeRefusedAmountOfOneItem()(implicit request: DataRequest[_]): Boolean = {
      request.userAnswers.itemReferences.map {
        uniqueReference =>
          request.userAnswers.get(RefusedAmountPage(uniqueReference)).getOrElse[BigDecimal](0)
      }.sum > 0
    }

    if (hasAtLeastSomeRefusedAmountOfOneItem()) {
      Future.successful(Redirect(navigator.nextPage(AddedItemsPage, NormalMode, request.userAnswers)))
    } else {
      val formWithError = formProvider().withGlobalError("addedItems.error.atLeastOneItem").fill(false)

      Future.successful(
        BadRequest(
          view(Some(formWithError), serviceResult, allItemsAdded, routes.AddedItemsController.onSubmit(ern, arc))
        )
      )
    }
  }

}
