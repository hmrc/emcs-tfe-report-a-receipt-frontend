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
import forms.WrongWithItemFormProvider
import models.requests.DataRequest
import models.{Mode, UserAnswers, WrongWithMovement}
import navigation.Navigator
import pages.unsatisfactory.individualItems._
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{ReferenceDataService, UserAnswersService}
import views.html.WrongWithItemView

import javax.inject.Inject
import scala.concurrent.Future

class WrongWithItemController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         override val userAnswersService: UserAnswersService,
                                         override val navigator: Navigator,
                                         override val auth: AuthAction,
                                         override val userAllowList: UserAllowListAction,
                                         override val withMovement: MovementAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         formProvider: WrongWithItemFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: WrongWithItemView,
                                         referenceDataService: ReferenceDataService
                                       )(implicit config: AppConfig) extends BaseNavigationController with AuthActionHelper {

  def loadWrongWithItem(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      val page = WrongWithItemPage(idx)
      renderView(Ok, idx, fillForm(page, formProvider()), mode)
    }

  def submitWrongWithItem(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] = {
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, idx, _, mode),
        (values: Set[WrongWithMovement]) =>
          saveAndRedirect(WrongWithItemPage(idx), values, cleanseUserAnswers(idx, values), mode)
      )
    }
  }

  private def cleanseUserAnswers(idx: Int, values: Set[WrongWithMovement])(implicit request: DataRequest[_]) = {
    val newUserAnswers: UserAnswers = cleanseUserAnswersIfValueHasChanged(
      page = WrongWithItemPage(idx),
      newAnswer = values,
      cleansingFunction = {
        val allOptionsNotChecked: Seq[WrongWithMovement] = WrongWithMovement.individualItemValues().filterNot(values.contains)

        allOptionsNotChecked.foldLeft(request.userAnswers) {
          case (answers, WrongWithMovement.ShortageOrExcess) =>
            //TODO: Future stories will need to tidy down the ItemShortagePage and ItemExcessPage when they are built
            answers
              .remove(ItemShortageOrExcessPage(idx))
          case (answers, WrongWithMovement.Damaged) =>
            answers
              .remove(AddItemDamageInformationPage(idx))
              .remove(ItemDamageInformationPage(idx))
          case (answers, WrongWithMovement.BrokenSeals) =>
            answers
              .remove(AddItemSealsInformationPage(idx))
              .remove(ItemSealsInformationPage(idx))
          case (answers, WrongWithMovement.Other) =>
            answers
              .remove(ItemOtherInformationPage(idx))
          case (answers, _) => answers
        }
      }
    )
    newUserAnswers
  }

  private def renderView(status: Status, idx:Int, form: Form[_], mode: Mode)(implicit request: DataRequest[_]) = {
    withMovementItemAsync(idx) {
      referenceDataService.itemWithReferenceData(_) { (item, cnCodeInfo) =>
        Future.successful(status(view(
          page = WrongWithItemPage(idx),
          form = form,
          action = routes.WrongWithItemController.submitWrongWithItem(request.ern, request.arc, idx, mode),
          item = item,
          cnCodeInfo = cnCodeInfo
        )))
      }
    }
  }
}
