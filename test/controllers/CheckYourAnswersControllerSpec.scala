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

package controllers

import base.SpecBase
import mocks.viewmodels.MockCheckAnswersHelper
import navigation.{FakeNavigator, Navigator}
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.CheckAnswersHelper
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency with MockCheckAnswersHelper {

  "Check Your Answers Controller" - {

    ".onPageLoad" - {

      val link = routes.SelectItemsController.onPageLoad(testErn, testArc).url

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(inject.bind[CheckAnswersHelper].toInstance(mockCheckAnswersHelper))
          .build()

        running(application) {

          val list = SummaryListViewModel(Seq.empty)
          val itemList = Seq.empty[(String, SummaryList)]

          MockCheckAnswersHelper.summaryList().returns(list)

          val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(testErn, testArc).url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[CheckYourAnswersView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(routes.CheckYourAnswersController.onSubmit(testErn, testArc), link, list, itemList, false)(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(testErn, testArc).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    ".onSubmit" - {

      "must redirect to the onward route" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              inject.bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute))
            )
            .build()

        running(application) {

          val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(testErn, testArc).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
      }
    }
  }
}
