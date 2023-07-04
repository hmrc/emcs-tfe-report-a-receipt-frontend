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
import forms.WrongWithMovementFormProvider
import models.requests.DataRequest
import models.{Mode, UserAnswers, WrongWithMovement}
import navigation.Navigator
import pages.unsatisfactory._
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.WrongWithMovementView

import javax.inject.Inject
import scala.concurrent.Future

class WrongWithMovementController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val navigator: Navigator,
                                             override val auth: AuthAction,
                                             override val userAllowList: UserAllowListAction,
                                             override val withMovement: MovementAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             formProvider: WrongWithMovementFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: WrongWithMovementView
                                           )(implicit config: AppConfig) extends BaseNavigationController with AuthActionHelper {

  def loadWrongWithMovement(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      renderView(Ok, fillForm(WrongWithMovementPage, formProvider()), mode)
    }

  def submitWrongWithMovement(ern: String, arc: String, mode: Mode): Action[AnyContent] = {
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(renderView(BadRequest, formWithErrors, mode)),
        (values: Set[WrongWithMovement]) =>
          saveAndRedirect(WrongWithMovementPage, values, cleanseAnswers(values), mode)
      )
    }
  }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Result =
    status(view(
      WrongWithMovementPage, form, routes.WrongWithMovementController.submitWrongWithMovement(request.ern, request.arc, mode)
    ))

  private def cleanseAnswers(values: Set[WrongWithMovement])(implicit request: DataRequest[_]): UserAnswers =
    cleanseUserAnswersIfValueHasChanged(
      page = WrongWithMovementPage,
      newAnswer = values,
      cleansingFunction = {
        val allOptionsNotChecked: Seq[WrongWithMovement] = WrongWithMovement.values.filterNot(values.contains)

        allOptionsNotChecked.foldLeft(request.userAnswers) {
          case (answers, WrongWithMovement.Shortage) =>
            answers
              .remove(AddShortageInformationPage)
              .remove(ShortageInformationPage)
          case (answers, WrongWithMovement.Excess) =>
            answers
              .remove(AddExcessInformationPage)
              .remove(ExcessInformationPage)
          case (answers, WrongWithMovement.Damaged) =>
            answers
              .remove(AddDamageInformationPage)
              .remove(DamageInformationPage)
          case (answers, WrongWithMovement.BrokenSeals) =>
            answers
              .remove(AddSealsInformationPage)
              .remove(SealsInformationPage)
          case (answers, WrongWithMovement.Other) =>
            answers
              .remove(OtherInformationPage)
          case (answers, _) => answers
        }
      }
    )
}
