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
import fixtures.messages.OtherInformationMessages
import models.CheckMode
import pages.unsatisfactory.OtherInformationPage
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.checkAnswers.OtherInformationSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class OtherInformationSummarySpec extends SpecBase {

  "OtherInformationSummary" - {

    "rendered for the OtherInformationPage" - {

      Seq(OtherInformationMessages.English, OtherInformationMessages.Welsh).foreach { langMessages =>

        s"when rendered for language code '${langMessages.lang.code}'" - {

          implicit lazy val app = applicationBuilder().build()
          implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
          lazy val link = app.injector.instanceOf[link]
          lazy val otherInformationSummary = new OtherInformationSummary(link)

          "when an answer is set" - {

            "must render the expected SummaryListRow" in {

              val answers = emptyUserAnswers.set(OtherInformationPage, Some("Info"))
              implicit val request = dataRequest(FakeRequest(), answers)
              lazy val changeRoute = routes.OtherInformationController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, CheckMode)

              otherInformationSummary.row() mustBe
                SummaryListRowViewModel(
                  key = langMessages.checkYourAnswersLabel,
                  value = ValueViewModel(Text("Info")),
                  actions = Seq(
                    ActionItemViewModel(
                      langMessages.change,
                      changeRoute.url
                    ).withVisuallyHiddenText(langMessages.hiddenChangeLink)
                  )
                )
            }
          }

          "when no answer is set" - {

            "must render the expected SummaryListRow" in {

              implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
              lazy val changeRoute = routes.OtherInformationController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, CheckMode)

              otherInformationSummary.row() mustBe
                SummaryListRowViewModel(
                  key = langMessages.checkYourAnswersLabel,
                  value = ValueViewModel(HtmlContent(link(
                    changeRoute.url,
                    langMessages.addMoreInformation
                  )))
                )
            }
          }
        }
      }
    }
  }
}
