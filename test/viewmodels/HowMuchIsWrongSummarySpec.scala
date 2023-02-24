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

package viewmodels

import base.SpecBase
import controllers.routes
import fixtures.messages.HowMuchIsWrongMessages
import models.CheckMode
import models.HowMuchIsWrong.TheWholeMovement
import pages.HowMuchIsWrongPage
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import utils.DateUtils
import viewmodels.checkAnswers.HowMuchIsWrongSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class HowMuchIsWrongSummarySpec extends SpecBase with DateUtils {


  "HowMuchIsWrongSummary" - {

    Seq(HowMuchIsWrongMessages.English, HowMuchIsWrongMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val wrongWithMovementSummary = new HowMuchIsWrongSummary

        "when an answer is set" - {

          "must render the expected SummaryListRow" in {

            val answers = emptyUserAnswers.set(HowMuchIsWrongPage, TheWholeMovement)
            implicit val request = dataRequest(FakeRequest(), answers)

            wrongWithMovementSummary.row() mustBe
              Some(SummaryListRowViewModel(
                key = langMessages.checkYourAnswersLabel,
                value = ValueViewModel(HtmlContent(langMessages.wholeMovement)),
                actions = Seq(
                  ActionItemViewModel(
                    langMessages.change,
                    routes.HowMuchIsWrongController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url
                  ).withVisuallyHiddenText(langMessages.hiddenChangeLinkText)
                )
              ))
          }
        }

        "when no answer is set" - {

          "must render None" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
            wrongWithMovementSummary.row() mustBe None
          }
        }
      }
    }
  }
}
