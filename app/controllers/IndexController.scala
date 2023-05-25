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

import controllers.actions.{AuthAction, DataRetrievalAction, MovementAction}
import forms.ContinueDraftFormProvider
import models.{NormalMode, UserAnswers}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import uk.gov.hmrc.http.HeaderCarrier
import views.html.ContinueDraftView

import javax.inject.Inject
import scala.concurrent.Future

class IndexController @Inject()(override val messagesApi: MessagesApi,
                                val userAnswersService: UserAnswersService,
                                val controllerComponents: MessagesControllerComponents,
                                authAction: AuthAction,
                                withMovement: MovementAction,
                                getData: DataRetrievalAction,
                                formProvider: ContinueDraftFormProvider,
                                view: ContinueDraftView) extends BaseController {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    (authAction(ern) andThen withMovement(arc) andThen getData).async { implicit request =>
      request.userAnswers match {
        case Some(ans) if ans.data.fields.nonEmpty =>
          Future.successful(Ok(view(formProvider(), routes.IndexController.onSubmit(ern, arc))))
        case _ =>
          initialiseAndRedirect(UserAnswers(request.internalId, request.ern, request.arc))
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    (authAction(ern) andThen withMovement(arc) andThen getData).async { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, routes.IndexController.onSubmit(ern, arc)))),
        continueDraft => {
          val userAnswers = request.userAnswers match {
            case Some(answers) if continueDraft => answers
            case _ => UserAnswers(request.internalId, request.ern, request.arc)
          }
          initialiseAndRedirect(userAnswers)
        }
      )
    }

  private def initialiseAndRedirect(answers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result] =
    userAnswersService.set(answers).map { _ =>
      Redirect(routes.DateOfArrivalController.onPageLoad(answers.ern, answers.arc, NormalMode))
    }
}
