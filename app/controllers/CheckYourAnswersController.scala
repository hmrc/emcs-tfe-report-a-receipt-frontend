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
import pages.CheckAnswersPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.{CheckAnswersHelper, CheckAnswersItemHelper}
import views.html.CheckYourAnswersView

import javax.inject.Inject

class CheckYourAnswersController @Inject()(override val messagesApi: MessagesApi,
                                           override val auth: AuthAction,
                                           override val withMovement: MovementAction,
                                           override val getData: DataRetrievalAction,
                                           override val requireData: DataRequiredAction,
                                           val controllerComponents: MessagesControllerComponents,
                                           val navigator: Navigator,
                                           view: CheckYourAnswersView,
                                           checkAnswersHelper: CheckAnswersHelper,
                                           checkAnswersItemHelper: CheckAnswersItemHelper,
                                          ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      withAllItems() {
        items =>
          val uniqueReferenceList = items.map(_.itemUniqueReference)
          val formattedAnswers: Seq[(String, SummaryList)] =
            items.zip(uniqueReferenceList) map {
              case (item, idx) =>
                (
                  checkAnswersItemHelper.itemName(item),
                  checkAnswersItemHelper.summaryList(idx + 1, item, true)
                )
            }

          val moreItemsToAdd: Boolean =
            if (request.movementDetails.items.size == items.size || items.isEmpty) false else true

          Ok(view(routes.CheckYourAnswersController.onSubmit(ern, arc),
            routes.SelectItemsController.onPageLoad(ern, arc).url,
            checkAnswersHelper.summaryList(),
            formattedAnswers,
            moreItemsToAdd,
          ))
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Redirect(navigator.nextPage(CheckAnswersPage, NormalMode, request.userAnswers))
    }
}
