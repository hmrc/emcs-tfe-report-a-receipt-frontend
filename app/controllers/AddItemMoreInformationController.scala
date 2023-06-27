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
import forms.AddMoreInformationFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.QuestionPage
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, ItemDamageInformationPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.{GetCnCodeInformationService, GetPackagingTypesService, UserAnswersService}
import utils.JsonOptionFormatter
import views.html.AddItemMoreInformationView

import javax.inject.Inject
import scala.concurrent.Future

class AddItemMoreInformationController @Inject()(override val messagesApi: MessagesApi,
                                                 override val userAnswersService: UserAnswersService,
                                                 override val navigator: Navigator,
                                                 override val auth: AuthAction,
                                                 override val userAllowList: UserAllowListAction,
                                                 override val withMovement: MovementAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 formProvider: AddMoreInformationFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: AddItemMoreInformationView,
                                                 getCnCodeInformationService: GetCnCodeInformationService,
                                                 getPackagingTypesService: GetPackagingTypesService
                                                ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {

  def loadItemDamageInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, AddItemDamageInformationPage(idx), routes.AddItemMoreInformationController.submitItemDamageInformation(ern, arc, idx, mode), idx)

  def submitItemDamageInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, AddItemDamageInformationPage(idx), ItemDamageInformationPage(idx), routes.AddItemMoreInformationController.submitItemDamageInformation(ern, arc, idx, mode), idx, mode)


  private def onPageLoad(ern: String,
                         arc: String,
                         yesNoPage: QuestionPage[Boolean],
                         submitAction: Call,
                         itemReference: Int): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      renderViewWithItemDetails(Ok, fillForm(yesNoPage, formProvider(yesNoPage)), yesNoPage, submitAction, itemReference)
    }

  private def onSubmit(ern: String,
                       arc: String,
                       yesNoPage: QuestionPage[Boolean],
                       infoPage: QuestionPage[Option[String]],
                       submitAction: Call,
                       itemReference: Int,
                       mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider(yesNoPage).bindFromRequest().fold(
        formWithErrors =>
          renderViewWithItemDetails(BadRequest, formWithErrors, yesNoPage, submitAction, itemReference),
        {
          case true =>
            saveAndRedirect(yesNoPage, true, mode)
          case false =>
            saveAndRedirect(yesNoPage, false, request.userAnswers.set(infoPage, None), mode)
        }
      )
    }

  private def renderViewWithItemDetails(status: Status,
                                        form: Form[_],
                                        yesNoPage: QuestionPage[Boolean],
                                        submitAction: Call,
                                        itemReference: Int)(implicit request: DataRequest[_]): Future[Result] =
    request.movementDetails.item(itemReference) match {
      case Some(item) =>
        getPackagingTypesService.getPackagingTypes(Seq(item)).flatMap {
          getCnCodeInformationService.getCnCodeInformationWithMovementItems(_).map {
            case (item, cnCodeInformation) :: Nil =>
              status(view(form, yesNoPage, submitAction, item, cnCodeInformation))
            case _ =>
              Redirect(routes.SelectItemsController.onPageLoad(request.ern, request.arc).url)
          }
        }
      case _ =>
        Future.successful(Redirect(routes.SelectItemsController.onPageLoad(request.ern, request.arc).url))
    }
}
