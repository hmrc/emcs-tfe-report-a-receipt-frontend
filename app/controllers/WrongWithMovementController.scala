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
import models.{Mode, UserAnswers, WrongWithMovement}
import navigation.Navigator
import pages._
import pages.unsatisfactory._
import pages.unsatisfactory.individualItems._
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
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
    onPageLoad(WrongWithMovementPage, ern, arc, routes.WrongWithMovementController.submitWrongWithMovement(ern, arc, mode))

  def submitWrongWithMovement(ern: String, arc: String, mode: Mode): Action[AnyContent] = {
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider(WrongWithMovementPage).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(
            WrongWithMovementPage,
            formWithErrors,
            routes.WrongWithMovementController.submitWrongWithMovement(ern, arc, mode)
          ))),
        (values: Set[WrongWithMovement]) => {

          val newUserAnswers: UserAnswers = cleanseUserAnswersIfValueHasChanged(
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

          saveAndRedirect(WrongWithMovementPage, values, newUserAnswers, mode)
        }
      )
    }
  }

  private def onPageLoad(page: QuestionPage[Set[WrongWithMovement]],
                         ern: String,
                         arc: String,
                         action: Call): Action[AnyContent] =
    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      Ok(view(page, fillForm(page, formProvider(page)), action))
    }
}
