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

package services

import config.AppConfig
import connectors.emcsTfe.SubmitReportOfReceiptConnector
import featureswitch.core.config.{EnableNRS, FeatureSwitching}
import models.audit.{SubmitReportOfReceiptAuditModel, SubmitReportOfReceiptResponseAuditModel}
import models.requests.DataRequest
import models.response.SubmitReportOfReceiptException
import models.response.emcsTfe.SubmitReportOfReceiptResponse
import models.submitReportOfReceipt.SubmitReportOfReceiptModel
import services.nrs.NRSBrokerService
import uk.gov.hmrc.http.HeaderCarrier

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import play.api.libs.json.Json
import utils.Logging

@Singleton
class SubmitReportOfReceiptService @Inject()(submitReportOfReceiptConnector: SubmitReportOfReceiptConnector,
                                             nrsBrokerService: NRSBrokerService,
                                             auditingService: AuditingService,
                                             appConfig: AppConfig)
                                            (implicit ec: ExecutionContext) extends FeatureSwitching with Logging {

  def submit(ern: String, arc: String)(implicit hc: HeaderCarrier, dataRequest: DataRequest[_]): Future[SubmitReportOfReceiptResponse] = {

    val submission = SubmitReportOfReceiptModel(dataRequest.movementDetails)(dataRequest.userAnswers, appConfig)

    // Log the final payload and the Delivery Place Trader Excise Number for Destination Office Prefix
      val deliveryPlaceTraderExciseNumberForDestinationOfficePrefix = dataRequest.movementDetails.deliveryPlaceTrader.flatMap(_.traderId)
      logger.info(s"[submit] Sending payload to emcs-tfe for arc: ${submission.arc} - payload:\n${Json.prettyPrint(Json.toJson(submission))}")
      logger.info(s"[submit] Delivery Place Trader Excise Number for arc ${submission.arc}: ${deliveryPlaceTraderExciseNumberForDestinationOfficePrefix.getOrElse("None")}")

    val auditSubmission = SubmitReportOfReceiptAuditModel(
      credentialId = dataRequest.request.request.credId,
      internalId = dataRequest.internalId,
      correlationId = UUID.randomUUID().toString,
      submission = submission,
      ern = ern
    )
    auditingService.audit(auditSubmission)

    if(isEnabled(EnableNRS)(appConfig)) {
      nrsBrokerService.submitPayload(submission, ern)
    }

    submitReportOfReceiptConnector.submit(ern, submission).map {
      case Right(success) =>
        auditingService.audit(
          SubmitReportOfReceiptResponseAuditModel(
            credentialId = auditSubmission.credentialId,
            internalId = dataRequest.internalId,
            correlationId = auditSubmission.correlationId,
            arc = dataRequest.arc,
            traderId = ern,
            receipt = success.receipt
          )
        )
        success
      case Left(_) => throw SubmitReportOfReceiptException(s"Failed to submit Report of Receipt to emcs-tfe for ern: '$ern' & arc: '$arc'")
    }
  }
}
