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
import forms.MoreInformationFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.QuestionPage
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, ItemDamageInformationPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.{ReferenceDataService, UserAnswersService}
import utils.JsonOptionFormatter
import views.html.ItemMoreInformationView

import javax.inject.Inject
import scala.concurrent.Future

class ItemMoreInformationController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               override val userAnswersService: UserAnswersService,
                                               override val navigator: Navigator,
                                               override val auth: AuthAction,
                                               override val userAllowList: UserAllowListAction,
                                               override val withMovement: MovementAction,
                                               override val getData: DataRetrievalAction,
                                               override val requireData: DataRequiredAction,
                                               formProvider: MoreInformationFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: ItemMoreInformationView,
                                               referenceDataService: ReferenceDataService
                                             ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {

  def loadItemDamageInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, ItemDamageInformationPage(idx), idx, routes.ItemMoreInformationController.submitItemDamageInformation(ern, arc, idx, mode))

  def submitItemDamageInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, ItemDamageInformationPage(idx), AddItemDamageInformationPage(idx), idx,
      routes.ItemMoreInformationController.submitItemDamageInformation(ern, arc, idx, mode), mode)

  private def onPageLoad(ern: String, arc: String, page: QuestionPage[Option[String]], item: Int, action: Call): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      renderViewWithItemDetails(Ok, fillForm(page, formProvider(page)), page, action, item)
    }

  private def onSubmit(ern: String,
                       arc: String,
                       page: QuestionPage[Option[String]],
                       yesNoPage: QuestionPage[Boolean],
                       item: Int,
                       action: Call,
                       mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider(page).bindFromRequest().fold(
        formWithErrors =>
          renderViewWithItemDetails(BadRequest, formWithErrors, page, action, item),
        value => {
          val updatedYesNo = request.userAnswers.set(yesNoPage, value.exists(_.nonEmpty))
          saveAndRedirect(page, value, updatedYesNo, mode)
        }
      )
    }

  private def renderViewWithItemDetails(status: Status,
                                        form: Form[_],
                                        page: QuestionPage[Option[String]],
                                        submitAction: Call,
                                        itemReference: Int)(implicit request: DataRequest[_]): Future[Result] = {
    withAddedItemAsync(itemReference) {
      referenceDataService.itemWithReferenceData(_) { (item, cnCodeInformation) =>
        Future.successful(status(view(form, page, submitAction, item, cnCodeInformation)))
      }
    }
  }
}
