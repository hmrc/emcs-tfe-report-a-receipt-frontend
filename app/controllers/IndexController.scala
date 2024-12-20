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
import models.{NormalMode, UserAnswers}
import pages.ConfirmationPage
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
                                view: ContinueDraftView) extends BaseController {

  def onPageLoadLegacy(ern: String, arc: String): Action[AnyContent] =
    Action {
      Redirect(routes.IndexController.onPageLoad(ern, arc))
    }

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    (authAction(ern, arc) andThen withMovement.fromCache(arc) andThen getData).async { implicit request =>
      request.userAnswers match {
        case Some(ans) if ans.get(ConfirmationPage).isDefined =>
          initialiseAndRedirect(UserAnswers(request.ern, request.arc))
        case Some(ans) if ans.data.fields.nonEmpty =>
          Future.successful(Ok(view(
            routes.IndexController.continueOrStartAgain(ern, arc, continueDraft = true),
            routes.IndexController.continueOrStartAgain(ern, arc, continueDraft = false),
          )))
        case _ =>
          initialiseAndRedirect(UserAnswers(request.ern, request.arc))
      }
    }

  def continueOrStartAgain(ern: String, arc: String, continueDraft: Boolean): Action[AnyContent] =
    (authAction(ern, arc) andThen withMovement.fromCache(arc) andThen getData).async { implicit request =>
      val userAnswers = request.userAnswers match {
        case Some(answers) if continueDraft => answers
        case _ => UserAnswers(request.ern, request.arc)
      }
      initialiseAndRedirect(userAnswers)
    }

  private def initialiseAndRedirect(answers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result] =
    userAnswersService.set(answers).map { _ =>
      Redirect(routes.DateOfArrivalController.onPageLoad(answers.ern, answers.arc, NormalMode))
    }
}
