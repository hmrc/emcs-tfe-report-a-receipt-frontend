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

package viewmodels.checkAnswers

import controllers.routes
import models.CheckMode
import models.requests.DataRequest
import pages.DestinationOfficePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DestinationOfficeSummary  {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(DestinationOfficePage).map {
      answer =>
        SummaryListRowViewModel(
          key     = s"$DestinationOfficePage.checkYourAnswers.label",
          value   = ValueViewModel(Text(messages(s"$DestinationOfficePage.checkYourAnswers.$answer"))),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.DestinationOfficeController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url,
              id = DestinationOfficePage
            ).withVisuallyHiddenText(messages(s"$DestinationOfficePage.checkYourAnswers.change.hidden"))
          )
        )
    }
}
