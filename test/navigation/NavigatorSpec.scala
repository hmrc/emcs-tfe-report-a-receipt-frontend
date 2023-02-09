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

package navigation

import base.SpecBase
import controllers.routes
import models.AcceptMovement.{PartiallyRefused, Refused, Satisfactory, Unsatisfactory}
import pages._
import models._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe routes.IndexController.onPageLoad(testErn, testArc)
      }

      "must go from DateOfArrival page to AcceptMovement page" in {

        navigator.nextPage(DateOfArrivalPage, NormalMode, emptyUserAnswers) mustBe routes.AcceptMovementController.onPageLoad(testErn, testArc, NormalMode)
      }

      "for the AcceptMovementPage page" - {

        s"when the user answers is $Satisfactory" - {

          "must go to the AddMoreInformation page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Satisfactory)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe routes.AddMoreInformationController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        s"when the user answers is $Unsatisfactory" - {

          //TODO: Update this as part of future stories to go through the actual flow
          "must go to the CheckAnswersPage page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Unsatisfactory)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }

        s"when the user answers is $Refused" - {

          //TODO: Update this as part of future stories to go through the actual flow
          "must go to the CheckAnswersPage page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, Refused)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }

        s"when the user answers is $PartiallyRefused" - {

          //TODO: Update this as part of future stories to go through the actual flow
          "must go to the CheckAnswersPage page" in {

            val userAnswers = emptyUserAnswers.set(AcceptMovementPage, PartiallyRefused)

            navigator.nextPage(AcceptMovementPage, NormalMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }
      }

      "for the AddMoreInformation page" - {

        s"when the user answers is Yes" - {

          //TODO: Future story will have this route to the MoreInformation page
          "must go to the CheckYourAnswers page" in {

            val userAnswers = emptyUserAnswers.set(AddMoreInformationPage, true)

            navigator.nextPage(AddMoreInformationPage, NormalMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }

        s"when the user answers is No" - {

          "must go to the CheckYourAnswers page" in {

            val userAnswers = emptyUserAnswers.set(AddMoreInformationPage, false)

            navigator.nextPage(AddMoreInformationPage, NormalMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
      }
    }
  }
}
