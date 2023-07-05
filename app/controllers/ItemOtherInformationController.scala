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
import forms.OtherInformationFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.unsatisfactory.individualItems.ItemOtherInformationPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.{ReferenceDataService, UserAnswersService}
import utils.Logging
import views.html.ItemOtherInformationView

import javax.inject.Inject
import scala.concurrent.Future

class ItemOtherInformationController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                override val userAnswersService: UserAnswersService,
                                                override val navigator: Navigator,
                                                override val auth: AuthAction,
                                                override val userAllowList: UserAllowListAction,
                                                override val withMovement: MovementAction,
                                                override val getData: DataRetrievalAction,
                                                override val requireData: DataRequiredAction,
                                                formProvider: OtherInformationFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: ItemOtherInformationView,
                                                referenceDataService: ReferenceDataService
                                              ) extends BaseNavigationController with AuthActionHelper with Logging {

  private def page(idx: Int): ItemOtherInformationPage = ItemOtherInformationPage(idx)

  def onPageLoad(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      renderViewWithItemDetails(
        status = Ok,
        form = fillForm(page(idx), formProvider(Some(page(idx)))),
        submitAction = routes.ItemOtherInformationController.onSubmit(ern, arc, idx, mode),
        itemReference = idx)
    }

  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      submitAndTrimWhitespaceFromTextarea[String](Some(page(idx)), formProvider)(
        formWithErrors => renderViewWithItemDetails(
          status = BadRequest,
          form = formWithErrors,
          submitAction = routes.ItemOtherInformationController.onSubmit(ern, arc, idx, mode),
          itemReference = idx)
      )(
        value => saveAndRedirect(page(idx), value, mode)
      )
    }

  private def renderViewWithItemDetails(status: Status,
                                        form: Form[_],
                                        submitAction: Call,
                                        itemReference: Int)(implicit request: DataRequest[_]): Future[Result] = {
    withAddedItemAsync(itemReference) {
      referenceDataService.itemWithReferenceData(_) { (item, cnCodeInformation) =>
        Future.successful(status(view(ItemOtherInformationPage(itemReference), form, submitAction, item, cnCodeInformation)))
      }
    }
  }
}
