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

package connectors.userAllowList

import config.AppConfig
import models.requests.CheckUserAllowListRequest
import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAllowListConnector @Inject()(http: HttpClient,
                                       config: AppConfig) extends UserAllowListHttpParser {

  def check(checkRequest: CheckUserAllowListRequest)
           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] = {
    val headerCarrierWithInternalAuthToken = hc.copy(authorization = Some(Authorization(config.internalAuthToken)))

    http.POST[CheckUserAllowListRequest, Either[ErrorResponse, Boolean]](
      url = config.userAllowListBaseUrl + "/emcs-tfe/reportOfReceipt/check",
      body = checkRequest
    )(CheckUserAllowListRequest.writes, UserAllowListReads, headerCarrierWithInternalAuthToken, ec)
  }.recover {
    error =>
      logger.warn(s"[check] Unexpected error from user-allow-list: ${error.getClass} ${error.getMessage}")
      Left(UnexpectedDownstreamResponseError)
  }

}
