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

import controllers.actions.{AuthAction, DataRequiredAction, DataRetrievalAction, MovementAction}
import models._
import models.requests.DataRequest
import navigation.BaseNavigator
import pages.QuestionPage
import play.api.libs.json.Format
import play.api.mvc.{Action, AnyContent, Result}
import repositories.SessionRepository

import scala.concurrent.Future

trait BaseNavigationController extends BaseController {

  val sessionRepository: SessionRepository
  val navigator: BaseNavigator

  def saveAndRedirect[A](page: QuestionPage[A], answer: A, mode: Mode)
                        (implicit request: DataRequest[_], format: Format[A]): Future[Result] =
    save(page, answer).map { updatedAnswers =>
      Redirect(navigator.nextPage(page, mode, updatedAnswers))
    }

  def save[A](page: QuestionPage[A], answer: A)
             (implicit request: DataRequest[_], format: Format[A]): Future[UserAnswers] = {

    val previousAnswer = request.userAnswers.get[A](page)

    if (previousAnswer.contains(answer)) {
      Future.successful(request.userAnswers)
    } else {
      for {
        updatedAnswers <- Future.successful(request.userAnswers.set(page, answer))
        _ <- sessionRepository.set(updatedAnswers)
      } yield updatedAnswers
    }
  }
}

