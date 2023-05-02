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
import navigation.Navigator
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems.{AddItemDamageInformationPage, AddItemSealsInformationPage, ItemDamageInformationPage, ItemSealsInformationPage}
import pages.{AddMoreInformationPage, MoreInformationPage, QuestionPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import services.UserAnswersService
import utils.JsonOptionFormatter
import views.html.AddMoreInformationView

import javax.inject.Inject
import scala.concurrent.Future

class AddMoreInformationController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              override val userAnswersService: UserAnswersService,
                                              override val navigator: Navigator,
                                              override val auth: AuthAction,
                                              override val userAllowList: UserAllowListAction,
                                              override val withMovement: MovementAction,
                                              override val getData: DataRetrievalAction,
                                              override val requireData: DataRequiredAction,
                                              formProvider: AddMoreInformationFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: AddMoreInformationView
                                            ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {

  def loadMoreInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, AddMoreInformationPage, routes.AddMoreInformationController.submitMoreInformation(ern, arc, mode))

  def submitMoreInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, AddMoreInformationPage, MoreInformationPage, routes.AddMoreInformationController.submitMoreInformation(ern, arc, mode), mode)

  def loadShortageInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, AddShortageInformationPage, routes.AddMoreInformationController.submitShortageInformation(ern, arc, mode))

  def submitShortageInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, AddShortageInformationPage, ShortageInformationPage, routes.AddMoreInformationController.submitShortageInformation(ern, arc, mode), mode)

  def loadExcessInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, AddExcessInformationPage, routes.AddMoreInformationController.submitExcessInformation(ern, arc, mode))

  def submitExcessInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, AddExcessInformationPage, ExcessInformationPage, routes.AddMoreInformationController.submitExcessInformation(ern, arc, mode), mode)

  def loadDamageInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, AddDamageInformationPage, routes.AddMoreInformationController.submitDamageInformation(ern, arc, mode))

  def submitDamageInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, AddDamageInformationPage, DamageInformationPage, routes.AddMoreInformationController.submitDamageInformation(ern, arc, mode), mode)

  def loadItemDamageInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, AddItemDamageInformationPage(idx), routes.AddMoreInformationController.submitItemDamageInformation(ern, arc, idx, mode))

  def submitItemDamageInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, AddItemDamageInformationPage(idx), ItemDamageInformationPage(idx), routes.AddMoreInformationController.submitItemDamageInformation(ern, arc, idx, mode), mode)

  def loadSealsInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, AddSealsInformationPage, routes.AddMoreInformationController.submitSealsInformation(ern, arc, mode))

  def submitSealsInformation(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, AddSealsInformationPage, SealsInformationPage, routes.AddMoreInformationController.submitSealsInformation(ern, arc, mode), mode)

  def loadItemSealsInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onPageLoad(ern, arc, AddItemSealsInformationPage(idx), routes.AddMoreInformationController.submitItemSealsInformation(ern, arc, idx, mode))

  def submitItemSealsInformation(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    onSubmit(ern, arc, AddItemSealsInformationPage(idx), ItemSealsInformationPage(idx), routes.AddMoreInformationController.submitItemSealsInformation(ern, arc, idx, mode), mode)


  private def onPageLoad(ern: String,
                         arc: String,
                         yesNoPage: QuestionPage[Boolean],
                         submitAction: Call): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Ok(view(fillForm(yesNoPage, formProvider(yesNoPage)), yesNoPage, submitAction))
    }

  private def onSubmit(ern: String,
                       arc: String,
                       yesNoPage: QuestionPage[Boolean],
                       infoPage: QuestionPage[Option[String]],
                       submitAction: Call,
                       mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider(yesNoPage).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, yesNoPage, submitAction))),
        {
          case true =>
            saveAndRedirect(yesNoPage, true, mode)
          case false =>
            val removedMoreInfo = request.userAnswers.set(infoPage, None)
            saveAndRedirect(yesNoPage, false, removedMoreInfo, mode)
        }
      )
    }

}
