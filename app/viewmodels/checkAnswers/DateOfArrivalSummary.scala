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
import pages.DateOfArrivalPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.DateUtils
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DateOfArrivalSummary extends DateUtils {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(DateOfArrivalPage).map {
      answer =>
        SummaryListRowViewModel(
          key = s"$DateOfArrivalPage.checkYourAnswers.label",
          value = ValueViewModel(answer.formatDateForUIOutput()),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.DateOfArrivalController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url,
              id = DateOfArrivalPage
            ).withVisuallyHiddenText(messages(s"$DateOfArrivalPage.checkYourAnswers.change.hidden"))
          )
        )
    }
}
