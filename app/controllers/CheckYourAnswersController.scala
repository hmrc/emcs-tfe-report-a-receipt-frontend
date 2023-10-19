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
import handlers.ErrorHandler
import models.AcceptMovement.{Refused, Satisfactory}
import models.HowGiveInformation.TheWholeMovement
import models.WrongWithMovement.{Excess, Shortage}
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import models.response.emcsTfe.{MovementItem, SubmitReportOfReceiptResponse}
import models.{CheckMode, ConfirmationDetails, NormalMode, UserAnswers}
import navigation.Navigator
import pages.unsatisfactory.{HowGiveInformationPage, WrongWithMovementPage}
import pages.{AcceptMovementPage, CheckAnswersPage, ConfirmationPage, DestinationOfficePage}
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCnCodeInformationService, SubmitReportOfReceiptService, UserAnswersService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.checkAnswers.{CheckAnswersHelper, CheckAnswersItemHelper}
import views.html.CheckYourAnswersView

import javax.inject.Inject
import scala.concurrent.Future

class CheckYourAnswersController @Inject()(override val messagesApi: MessagesApi,
                                           override val userAnswersService: UserAnswersService,
                                           override val auth: AuthAction,
                                           override val userAllowList: UserAllowListAction,
                                           override val withMovement: MovementAction,
                                           override val getData: DataRetrievalAction,
                                           override val requireData: DataRequiredAction,
                                           val controllerComponents: MessagesControllerComponents,
                                           val navigator: Navigator,
                                           view: CheckYourAnswersView,
                                           checkAnswersHelper: CheckAnswersHelper,
                                           checkAnswersItemHelper: CheckAnswersItemHelper,
                                           submitReportOfReceiptService: SubmitReportOfReceiptService,
                                           getCnCodeInformationService: GetCnCodeInformationService,
                                           errorHandler: ErrorHandler
                                          )(implicit config: AppConfig) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withAllCompletedItemsAsync() { items =>
        guardPageAccess(items) {
          val formattedAnswersFuture: Future[Seq[(Int, SummaryList)]] = {
            if (items.nonEmpty) {
              getCnCodeInformationService.getCnCodeInformationWithMovementItems(items).map {
                serviceResult =>
                  serviceResult.map {
                    case (item, cnCodeInformation) =>
                      (
                        item.itemUniqueReference,
                        checkAnswersItemHelper.summaryList(
                          idx = item.itemUniqueReference,
                          unitOfMeasure = cnCodeInformation.unitOfMeasureCode.toUnitOfMeasure,
                          onFinalCheckAnswers = true
                        )
                      )
                  }
              }
            } else {
              Future.successful(Seq())
            }
          }

          formattedAnswersFuture.map {
            formattedAnswers =>
              val moreItemsToAdd: Boolean =
                (request.movementDetails.items.size != items.size) && items.nonEmpty

              Ok(view(routes.CheckYourAnswersController.onSubmit(ern, arc),
                routes.SelectItemsController.onPageLoad(ern, arc).url,
                checkAnswersHelper.summaryList(),
                formattedAnswers,
                moreItemsToAdd
              ))
          }
        }
      }
    }

  private[controllers] def guardPageAccess(items: Seq[MovementItem])(block: => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    lazy val guardAfterNorthernIrelandCheck: Future[Result] =
      (request.userAnswers.get(AcceptMovementPage), request.userAnswers.get(HowGiveInformationPage)) match {
        case (Some(Satisfactory), _) | (Some(Refused), Some(TheWholeMovement)) => block
        case _ if items.nonEmpty => block
        case _ => Future.successful(Redirect(routes.SelectItemsController.onPageLoad(request.ern, request.arc)))
      }

    if(request.userAnswers.isNorthernIrelandTrader) {
      request.userAnswers.get(DestinationOfficePage) match {
        case Some(_) => guardAfterNorthernIrelandCheck
        case None => Future.successful(Redirect(routes.DestinationOfficeController.onPageLoad(request.ern, request.arc, CheckMode)))
      }
    } else {
      guardAfterNorthernIrelandCheck
    }
  }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      submitReportOfReceiptService.submit(ern, arc).flatMap { response =>

        deleteDraftAndSetConfirmationFlow(request.internalId, request.ern, request.arc, response).map { _ =>
          Redirect(navigator.nextPage(CheckAnswersPage, NormalMode, request.userAnswers))
        }

      } recover {
        case _: MissingMandatoryPage =>
          BadRequest(errorHandler.badRequestTemplate)
        case _ =>
          InternalServerError(errorHandler.internalServerErrorTemplate)
      }
    }


  private def deleteDraftAndSetConfirmationFlow(internalId: String,
                                                ern: String,
                                                arc: String,
                                                response: SubmitReportOfReceiptResponse)
                                               (implicit hc: HeaderCarrier, request: DataRequest[_]): Future[UserAnswers] = {
    userAnswersService.set(
      UserAnswers(
        ern,
        arc,
        data = Json.obj(ConfirmationPage.toString ->
          ConfirmationDetails(
            receipt = response.receipt,
            receiptDate = response.receiptDate,
            receiptStatus = request.userAnswers.get(AcceptMovementPage).getOrElse().toString,
            hasMovementShortage = request.userAnswers.get(WrongWithMovementPage).exists(_.contains(Shortage)),
            hasItemShortage = request.userAnswers.completedItems.exists(_.itemShortageOrExcess.exists(_.wrongWithItem == Shortage)),
            hasMovementExcess = request.userAnswers.get(WrongWithMovementPage).exists(_.contains(Excess)),
            hasItemExcess = request.userAnswers.completedItems.exists(_.itemShortageOrExcess.exists(_.wrongWithItem == Excess))
          ))
      ))
  }

}
