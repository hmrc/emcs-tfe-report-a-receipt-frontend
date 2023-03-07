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
import models.requests.DataRequest
import models.{NormalMode, WrongWithMovement}
import pages.unsatisfactory.WrongWithMovementPage
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.list

import javax.inject.Inject

class WrongWithMovementSummary @Inject()(list: list) {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(WrongWithMovementPage).map { answers =>
      SummaryListRowViewModel(
        key     = s"$WrongWithMovementPage.checkYourAnswers.label",
        value   = ValueViewModel(HtmlContent(
          list(WrongWithMovement.values.filter(answers.contains).map { answer =>
            Html(messages(s"$WrongWithMovementPage.checkYourAnswers.$answer"))
          })
        )),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            routes.WrongWithMovementController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, NormalMode).url,
            id = WrongWithMovementPage
          )
            .withVisuallyHiddenText(messages(s"$WrongWithMovementPage.checkYourAnswers.change.hidden"))
        )
      )
    }
}
