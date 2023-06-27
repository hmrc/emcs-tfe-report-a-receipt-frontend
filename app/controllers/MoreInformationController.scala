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
import navigation.Navigator
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems.{ItemDamageInformationPage, AddItemSealsInformationPage, AddItemDamageInformationPage, ItemSealsInformationPage}
import pages.{AddMoreInformationPage, MoreInformationPage, QuestionPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import services.UserAnswersService
import utils.JsonOptionFormatter
import views.html.MoreInformationView

import javax.inject.Inject
import scala.concurrent.Future

class MoreInformationController @Inject()(
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
                                           view: MoreInformationView
                                     ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {

  def loadMoreInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, MoreInformationPage, routes.MoreInformationController.submitMoreInformation(ern, arc, mode))

  def submitMoreInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, MoreInformationPage, AddMoreInformationPage, routes.MoreInformationController.submitMoreInformation(ern, arc, mode), mode)

  def loadShortageInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, ShortageInformationPage, routes.MoreInformationController.submitShortageInformation(ern, arc, mode))

  def submitShortageInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, ShortageInformationPage, AddShortageInformationPage, routes.MoreInformationController.submitShortageInformation(ern, arc, mode), mode)

  def loadExcessInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, ExcessInformationPage, routes.MoreInformationController.submitExcessInformation(ern, arc, mode))

  def submitExcessInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, ExcessInformationPage, AddExcessInformationPage, routes.MoreInformationController.submitExcessInformation(ern, arc, mode), mode)

  def loadDamageInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, DamageInformationPage, routes.MoreInformationController.submitDamageInformation(ern, arc, mode))

  def submitDamageInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, DamageInformationPage, AddDamageInformationPage, routes.MoreInformationController.submitDamageInformation(ern, arc, mode), mode)

  def loadSealsInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, SealsInformationPage, routes.MoreInformationController.submitSealsInformation(ern, arc, mode))

  def submitSealsInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, SealsInformationPage, AddSealsInformationPage, routes.MoreInformationController.submitSealsInformation(ern, arc, mode), mode)

  def loadItemSealsInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, ItemSealsInformationPage(idx), routes.MoreInformationController.submitItemSealsInformation(ern, arc, idx, mode))

  def submitItemSealsInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, ItemSealsInformationPage(idx), AddItemSealsInformationPage(idx), routes.MoreInformationController.submitItemSealsInformation(ern, arc, idx, mode), mode)

  private def onPageLoad(ern: String, arc: String, page: QuestionPage[Option[String]], action: Call): Action[AnyContent] =
    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      Ok(view(fillForm(page, formProvider(page)), page, action))
    }

  private def onSubmit(ern: String,
                       arc: String,
                       page: QuestionPage[Option[String]],
                       yesNoPage: QuestionPage[Boolean],
                       action: Call,
                       mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider(page).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, page, action))),
        value => {
          val updatedYesNo = request.userAnswers.set(yesNoPage, value.exists(_.nonEmpty))
          saveAndRedirect(page, value, updatedYesNo, mode)
        }
      )
    }
}
