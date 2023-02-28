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
import fixtures.messages.MoreInformationMessages
import models.CheckMode
import pages.MoreInformationPage
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.checkAnswers.MoreInformationSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class MoreInformationSummarySpec extends SpecBase {


  "AddMoreInformationSummary" - {

    Seq(MoreInformationMessages.English, MoreInformationMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val link = app.injector.instanceOf[link]
        lazy val moreMoreInformationSummary = new MoreInformationSummary(link)

        "when an answer is set" - {

          "must render the expected SummaryListRow" in {

            val answers = emptyUserAnswers.set(MoreInformationPage, Some("Info"))
            implicit val request = dataRequest(FakeRequest(), answers)

            moreMoreInformationSummary.row() mustBe
              SummaryListRowViewModel(
                key = langMessages.checkYourAnswersLabel,
                value = ValueViewModel(Text("Info")),
                actions = Seq(
                  ActionItemViewModel(
                    langMessages.change,
                    routes.MoreInformationController.loadMoreInformation(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url
                  ).withVisuallyHiddenText(langMessages.hiddenChangeLink)
                )
              )
          }
        }

        "when no answer is set" - {

          "must render the expected SummaryListRow" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)

            moreMoreInformationSummary.row() mustBe
              SummaryListRowViewModel(
                key = langMessages.checkYourAnswersLabel,
                value = ValueViewModel(HtmlContent(link(
                  routes.MoreInformationController.loadMoreInformation(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url,
                  langMessages.addMoreInformation
                )))
              )
          }
        }
      }
    }
  }
}
