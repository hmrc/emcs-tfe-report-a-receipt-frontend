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
import models.NormalMode
import navigation.Navigator
import pages.unsatisfactory.individualItems.CheckAnswersItemPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import viewmodels.checkAnswers.CheckAnswersItemHelper
import views.html.CheckYourAnswersItemView

import javax.inject.Inject

class CheckYourAnswersItemController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            override val auth: AuthAction,
                                            override val withMovement: MovementAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            val navigator: Navigator,
                                            view: CheckYourAnswersItemView,
                                            checkAnswersItemHelper: CheckAnswersItemHelper
                                          ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      withItem(idx) {
        item =>
          Ok(view(
            routes.CheckYourAnswersItemController.onSubmit(ern, arc, idx),
            checkAnswersItemHelper.itemName(item),
            checkAnswersItemHelper.summaryList(idx, item)
          ))
      }
    }

  def onSubmit(ern: String, arc: String, idx: Int): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      withItem(idx) {
        _ =>
          Redirect(navigator.nextPage(CheckAnswersItemPage(idx), NormalMode, request.userAnswers))
      }
    }
}
