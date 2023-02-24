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

package mocks.viewmodels

import models.requests.DataRequest
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.WrongWithMovementSummary


trait MockWrongWithMovementSummary extends MockFactory {

  lazy val mockWrongWithMovementSummary: WrongWithMovementSummary = mock[WrongWithMovementSummary]

  object MockWrongWithMovementSummary {

    def row(): CallHandler2[DataRequest[_], Messages, Option[SummaryListRow]] =
      (mockWrongWithMovementSummary.row()(_: DataRequest[_], _: Messages))
        .expects(*, *)
  }
}
