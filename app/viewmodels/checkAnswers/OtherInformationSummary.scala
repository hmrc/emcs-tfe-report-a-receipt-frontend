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
import pages.unsatisfactory.OtherInformationPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.JsonOptionFormatter
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

import javax.inject.Inject

class OtherInformationSummary @Inject()(link: link) extends JsonOptionFormatter {

  def row()(implicit request: DataRequest[_], messages: Messages): SummaryListRow = {
    request.userAnswers.get(OtherInformationPage) match {
      case Some(answer) if answer != "" =>
        SummaryListRowViewModel(
          key = s"$OtherInformationPage.checkYourAnswers.label",
          value = ValueViewModel(Text(answer)),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.OtherInformationController.submitOtherInformation(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url,
              id = OtherInformationPage
            ).withVisuallyHiddenText(messages(s"$OtherInformationPage.checkYourAnswers.change.hidden"))
          )
        )
      case _ =>
        SummaryListRowViewModel(
          key = s"$OtherInformationPage.checkYourAnswers.label",
          value = ValueViewModel(HtmlContent(link(
            link = routes.OtherInformationController.submitOtherInformation(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url,
            messageKey = s"$OtherInformationPage.checkYourAnswers.addMoreInformation"
          )))
        )
    }
  }
}
