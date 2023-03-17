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
import models.NormalMode
import models.requests.DataRequest
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import viewmodels.AddedItemsSummary
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
                                      addedItemsSummary: AddedItemsSummary
                                    ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withAddedItems(ern, arc) { items =>
        val form = if (items.size < request.movementDetails.items.size) Some(formProvider()) else None
        Future.successful(Ok(view(form, items, routes.AddedItemsController.onSubmit(ern, arc))))
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withAddedItems(ern, arc) {
        case items if items.size < request.movementDetails.items.size =>
          formProvider().bindFromRequest().fold(
            formWithErrors =>
              Future.successful(BadRequest(view(Some(formWithErrors), items, routes.AddedItemsController.onSubmit(ern, arc))))
            ,{
              case true => addAnotherItemRedirect(ern, arc)
              case _ => onwardRedirect(ern, arc)
            }
          )
        case _ =>
          onwardRedirect(ern, arc)
      }
    }

  private def withAddedItems(ern: String, arc: String)(f: Seq[ListItem] => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    addedItemsSummary.itemList() match {
      case items if items.isEmpty => Future.successful(Redirect(routes.SelectItemsController.onPageLoad(ern, arc)))
      case items => f(items)
    }

  private def addAnotherItemRedirect(ern: String, arc: String): Future[Result] =
    Future.successful(Redirect(routes.SelectItemsController.onPageLoad(ern, arc)))

  private def onwardRedirect(ern: String, arc: String): Future[Result] =
    Future.successful(Redirect(routes.AddMoreInformationController.loadMoreInformation(ern, arc, NormalMode)))
}
